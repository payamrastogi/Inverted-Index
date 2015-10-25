package com.wse;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.io.ThreadedParsedObjectWriter;
import com.wse.io.DocumentWriter;
import com.wse.io.ParsedObjectWriter;
import com.wse.io.ThreadedDocumentWriter;
import com.wse.io.ThreadedLexiconWriter;
import com.wse.model.MetaObject;
import com.wse.model.ParsedObject;
import com.wse.parse.Indexer;
import com.wse.parse.ReadGzip;
import com.wse.parse.ThreadedIndexer;
import com.wse.parse.ThreadedReadGzip;
import com.wse.serialize.KryoSerializer;
import com.wse.shell.ExecuteCommand;
import com.wse.shell.ThreadedExecuteCommand;
import com.wse.shell.ThreadedUnixMerge;
import com.wse.shell.ThreadedUnixSort;
import com.wse.shell.UnixMerge;
import com.wse.shell.UnixSort;
import com.wse.util.Config;
import com.wse.util.ElapsedTime;
import com.wse.util.FileReader;


//Main file 
public class Main
{
	private static final String configPropPath = "src/main/resources/config.properties";
	//Queue to store filePaths of gzip index files
	private BlockingQueue<String> pathQueue;
	//Queue to store Parsed Objects
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	//Queue to store filePaths of files to be sorted by unix sort
	private BlockingQueue<String> toSortQueue;
	//Queue to store filePaths of files to be merged by unix merge
	private BlockingQueue<String> toIndexQueue;
	private BlockingQueue<String> toMergeQueue1;
	private BlockingQueue<String> toMergeQueue2;
	
	private BlockingQueue<String> documentQueue;
	private BlockingQueue<String> lexiconQueue;
	
	private Set<String> stopWords;
	
	private Config config;
	private ExecuteCommand executeCommand;
	private ReadGzip readGzip;
	private ParsedObjectWriter[] writers = new ParsedObjectWriter[5];
	//private DocumentWriter documentWriter;
	private FileReader fileReader;
	private UnixSort unixSort;
	private UnixMerge unixMerge;
	private Indexer indexer;
	private AtomicBoolean flag1;
	private AtomicBoolean flag2;
	private AtomicBoolean flagReadGzip;
	private AtomicInteger flagWriter;
	private MetaObject metaObject;
	private static final int writerThreads = 2;
	
	private Logger logger = LoggerFactory.getLogger(Main.class);
	
	public Main() throws Exception
	{
		this.config = new Config(new File(configPropPath));
		this.fileReader = new FileReader(this.config.getStopWordsFilePath());
		this.stopWords = this.fileReader.getStopWords();
		
		this.pathQueue = new ArrayBlockingQueue<>(5000);
		this.parsedObjectQueue = new ArrayBlockingQueue<>(100000);
		this.documentQueue = new ArrayBlockingQueue<>(100000);
		this.toSortQueue = new ArrayBlockingQueue<>(200);
		this.toIndexQueue = new ArrayBlockingQueue<>(200);
		this.toMergeQueue1 = new ArrayBlockingQueue<>(200);
		this.toMergeQueue2 = new ArrayBlockingQueue<>(200);
		this.lexiconQueue = new ArrayBlockingQueue<>(100000);
		this.flag1 = new AtomicBoolean(true);
		this.flag2 = new AtomicBoolean(true);
		this.flagReadGzip = new AtomicBoolean(true);
		this.flagWriter = new AtomicInteger(writerThreads);
		this.executeCommand = new ExecuteCommand(this.config.getFindCommand(), pathQueue);
		this.readGzip = new ReadGzip(this.parsedObjectQueue, this.documentQueue);
		this.unixSort = new UnixSort(this.config.getSortCommand(), this.toIndexQueue);
		this.indexer = new Indexer(this.toMergeQueue1, this.toMergeQueue2, this.lexiconQueue);
		this.unixMerge = new UnixMerge(this.config.getMergeCommand(), this.config.getOutputFilePath());
		char ch = 'a' ;
		for (int i =0 ;i<writerThreads ;i++) 
			this.writers[i] = new ParsedObjectWriter(this.config.getOutputFilePath(),ch++, this.stopWords, this.toSortQueue);
		//this.documentWriter = new DocumentWriter(this.config.getOutputFilePath());
	}
	
	public static void main(String args[]) throws Exception
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		Main main = new Main();
		main.execute();
		main.logger.debug("Total Time: "+ elapsedTime.getTotalTimeInSeconds() +" seconds");
	}
	//creating pipeline between different phases
	// phase 1. read Index file and get html page from gzip file
	// phase 2. parse html page and write parsed pages to disk
	// phase 3. sort parsed file using unix sort
	// phase 4. merge sorted files
	// create final index from merged file
	public void execute()
	{
		try
		{
			ExecutorService executor = Executors.newCachedThreadPool();	
			//execute unix find command
			executor.submit(new ThreadedExecuteCommand(this.executeCommand));
			// read gzip file and get parsedobject
			executor.submit(new ThreadedReadGzip(this.readGzip, this.pathQueue, this.flagReadGzip));
			// write parsed object to file
			for (int i =0 ;i<writerThreads ;i++)
				executor.submit(new ThreadedParsedObjectWriter(this.writers[i], this.parsedObjectQueue, this.flagWriter));
			executor.submit(new ThreadedDocumentWriter(this.config.getOutputFilePath(), this.documentQueue));
			// sort parsed file using unix sort
			executor.submit(new ThreadedUnixSort(this.unixSort, this.toSortQueue));
			executor.submit(new ThreadedIndexer(this.indexer, this.toIndexQueue, this.flag1, this.flagReadGzip));
			executor.submit(new ThreadedIndexer(this.indexer, this.toIndexQueue, this.flag2, this.flagReadGzip));
			// merge sorted files
			executor.submit(new ThreadedUnixMerge(this.unixMerge, this.toMergeQueue1, this.toMergeQueue2, this.flag1, this.flag2));
			//executor.submit(new ThreadedLexiconWriter(this.lexiconQueue, this.config.getOutputFilePath(), this.lexiconCount));
			executor.shutdownNow();
		    executor.awaitTermination(5000, TimeUnit.SECONDS);
		    
		   /* ExecutorService executor1 = Executors.newCachedThreadPool();	
		    executor1.submit(new ThreadedLexiconWriter(this.lexiconQueue, this.config.getOutputFilePath(), this.lexiconCount));
		    */
		    //create final index
		    String inputFilePath = toMergeQueue1.isEmpty()?toMergeQueue2.poll():toMergeQueue1.poll();
		    indexer.createFinalIndexVByte(inputFilePath, inputFilePath+"i");
		    
		   
		   /* executor1.shutdownNow();
		    executor1.awaitTermination(5000, TimeUnit.SECONDS);*/
		    
		    KryoSerializer kryoSerializer = new KryoSerializer();
		    this.metaObject = new MetaObject(this.readGzip.getTotalDocuments(), this.readGzip.getAverageLengthOfDocuments());
		    kryoSerializer.serialize(metaObject);
			logger.debug(pathQueue.size()+"");
			logger.debug(parsedObjectQueue.size()+"");
			logger.debug(toSortQueue.size()+"");
			logger.debug(toIndexQueue.size()+"");
			logger.debug(toMergeQueue1.size()+"");
			logger.debug(toMergeQueue2.size()+"");
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}