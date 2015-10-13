package com.wse;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.io.ThreadedWriter;
import com.wse.io.Writer;
import com.wse.model.ParsedObject;
import com.wse.parse.Indexer;
import com.wse.parse.ReadGzip;
import com.wse.parse.ThreadedReadGzip;
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
	private BlockingQueue<String> sortFileQueue;
	//Queue to store filePaths of files to be merged by unix merge
	private BlockingQueue<String> mergeFileQueue1;
	private BlockingQueue<String> mergeFileQueue2;
	
	private Set<String> stopWords;
	
	private Config config;
	private ExecuteCommand executeCommand;
	private ReadGzip readGzip;
	private Writer[] writers = new Writer[5];
	private FileReader fileReader;
	private UnixSort unixSort;
	private UnixMerge unixMerge;
	private Indexer indexer;
	
	private Logger logger = LoggerFactory.getLogger(Main.class);
	
	public Main() throws Exception
	{
		this.config = new Config(new File(configPropPath));
		this.fileReader = new FileReader(this.config.getStopWordsFilePath());
		this.stopWords = this.fileReader.getStopWords();
		
		this.pathQueue = new ArrayBlockingQueue<>(5000);
		this.parsedObjectQueue = new ArrayBlockingQueue<>(100000);
		this.sortFileQueue = new ArrayBlockingQueue<>(200);
		this.mergeFileQueue1 = new ArrayBlockingQueue<>(200);
		this.mergeFileQueue2 = new ArrayBlockingQueue<>(200);
		
		this.executeCommand = new ExecuteCommand(this.config.getFindCommand(), pathQueue);
		this.readGzip = new ReadGzip(this.parsedObjectQueue);
		this.unixSort = new UnixSort(this.config.getSortCommand(), this.mergeFileQueue1, this.mergeFileQueue2);
		this.unixMerge = new UnixMerge(this.config.getMergeCommand(), this.config.getOutputFilePath());
		char ch = 'a' ;
		for (int i =0 ;i<5 ;i++) 
			this.writers[i] = new Writer(this.config.getOutputFilePath(),ch++, this.stopWords, this.sortFileQueue);
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
			executor.submit(new ThreadedReadGzip(this.readGzip, this.pathQueue));
			// write parsed object to file
			for (int i =0 ;i<5 ;i++)
				executor.submit(new ThreadedWriter(this.writers[i], this.parsedObjectQueue));
			// sort parsed file using unix sort
			executor.submit(new ThreadedUnixSort(this.unixSort, this.sortFileQueue));
			// merge sorted files
			executor.submit(new ThreadedUnixMerge(this.unixMerge, this.mergeFileQueue1, this.mergeFileQueue2));
			executor.shutdownNow();
		    executor.awaitTermination(10000, TimeUnit.SECONDS);
		    
		    //create final index
		    String inputFilePath = mergeFileQueue1.isEmpty()?mergeFileQueue2.poll():mergeFileQueue1.poll();
		    this.indexer = new Indexer(inputFilePath, this.config.getOutputFilePath()+"/final");
		    this.indexer.index();
			logger.debug(pathQueue.size()+"");
			logger.debug(parsedObjectQueue.size()+"");
			logger.debug(sortFileQueue.size()+"");
			logger.debug(mergeFileQueue1.size()+"");
			logger.debug(mergeFileQueue2.size()+"");
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}