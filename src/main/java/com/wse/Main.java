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
import com.wse.model.ReadObject;
import com.wse.model.VolumeIndexedObject;
import com.wse.parse.ReadGzip;
import com.wse.parse.ThreadedReadGzip;
import com.wse.parse.ThreadedVolumeIndexer;
import com.wse.parse.VolumeIndexer;
import com.wse.shell.ExecuteCommand;
import com.wse.shell.ThreadedExecuteCommand;
import com.wse.util.Config;
import com.wse.util.ElapsedTime;
import com.wse.util.FileReader;
//import com.wse.util.Pair;

public class Main
{
	private static final String configPropPath = "src/main/resources/config.properties";
	private BlockingQueue<String> pathQueue;
	private BlockingQueue<ReadObject> readObjectQueue;
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	private BlockingQueue<VolumeIndexedObject> volumeIndexedObjectQueue;
	
	private Set<String> stopWords;
	
	private Config config;
	private ExecuteCommand executeCommand;
	private ReadGzip readGzip;
	private VolumeIndexer volumeIndexer;
	private Writer writer;
	private FileReader fileReader;
	
	private Logger logger = LoggerFactory.getLogger(Main.class);
	
	public Main() throws Exception
	{
		this.config = new Config(new File(configPropPath));
		this.fileReader = new FileReader(this.config.getStopWordsFilePath());
		this.stopWords = this.fileReader.getStopWords();
		
		this.pathQueue = new ArrayBlockingQueue<>(5000);
		this.parsedObjectQueue = new ArrayBlockingQueue<>(100000);
		this.volumeIndexedObjectQueue = new ArrayBlockingQueue<>(100000);
		this.executeCommand = new ExecuteCommand(this.config.getFindCommand(), pathQueue);
		this.readGzip = new ReadGzip(this.parsedObjectQueue);
		this.volumeIndexer = new VolumeIndexer(this.stopWords, this.volumeIndexedObjectQueue);
		this.writer = new Writer(this.config.getOutputFilePath(), this.stopWords);
	}
	
	public static void main(String args[]) throws Exception
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		Main main = new Main();
		main.execute();
		main.logger.debug("Total Time: "+ elapsedTime.getTotalTimeInSeconds() +" seconds");
	}
	
	public void execute()
	{
		try
		{
			ExecutorService executor = Executors.newCachedThreadPool();		
			executor.submit(new ThreadedExecuteCommand(this.executeCommand));
			executor.submit(new ThreadedReadGzip(this.readGzip, this.pathQueue));
			//executor.submit(new ThreadedVolumeIndexer(this.volumeIndexer, this.parsedObjectQueue));
			executor.submit(new ThreadedWriter(this.writer, this.parsedObjectQueue));
			executor.submit(new ThreadedWriter(this.writer, this.parsedObjectQueue));
			executor.submit(new ThreadedWriter(this.writer, this.parsedObjectQueue));
			executor.submit(new ThreadedWriter(this.writer, this.parsedObjectQueue));
			executor.shutdownNow();
		    executor.awaitTermination(10000, TimeUnit.SECONDS);
			logger.debug(pathQueue.size()+"");
			logger.debug(readObjectQueue.size()+"");
			logger.debug(parsedObjectQueue.size()+"");
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}