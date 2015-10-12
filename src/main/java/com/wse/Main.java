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
import com.wse.parse.ReadGzip;
import com.wse.parse.ThreadedReadGzip;
import com.wse.parse.VolumeIndexer;
import com.wse.shell.ExecuteCommand;
import com.wse.shell.ThreadedExecuteCommand;
import com.wse.shell.ThreadedUnixSort;
import com.wse.shell.UnixSort;
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
	private BlockingQueue<String> sortFileQueue;
	
	private Set<String> stopWords;
	
	private Config config;
	private ExecuteCommand executeCommand;
	private ReadGzip readGzip;
	private Writer[] writers = new Writer[5];
	private FileReader fileReader;
	private UnixSort unixSort;
	
	private Logger logger = LoggerFactory.getLogger(Main.class);
	
	public Main() throws Exception
	{
		this.config = new Config(new File(configPropPath));
		this.fileReader = new FileReader(this.config.getStopWordsFilePath());
		this.stopWords = this.fileReader.getStopWords();
		
		this.pathQueue = new ArrayBlockingQueue<>(5000);
		this.parsedObjectQueue = new ArrayBlockingQueue<>(100000);
		this.sortFileQueue = new ArrayBlockingQueue<>(1000);
		this.executeCommand = new ExecuteCommand(this.config.getFindCommand(), pathQueue);
		this.readGzip = new ReadGzip(this.parsedObjectQueue);
		this.unixSort = new UnixSort(this.config.getSortCommand());
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
	
	public void execute()
	{
		try
		{
			ExecutorService executor = Executors.newCachedThreadPool();		
			executor.submit(new ThreadedExecuteCommand(this.executeCommand));
			executor.submit(new ThreadedReadGzip(this.readGzip, this.pathQueue));
			//executor.submit(new ThreadedVolumeIndexer(this.volumeIndexer, this.parsedObjectQueue));
			for (int i =0 ;i<5 ;i++)
				executor.submit(new ThreadedWriter(this.writers[i], this.parsedObjectQueue));
			executor.submit(new ThreadedUnixSort(this.unixSort, this.sortFileQueue));
			executor.shutdownNow();
		    executor.awaitTermination(10000, TimeUnit.SECONDS);
			logger.debug(pathQueue.size()+"");
			logger.debug(parsedObjectQueue.size()+"");
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}