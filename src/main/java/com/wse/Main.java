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

import com.google.common.collect.Multiset;
import com.wse.io.Writer;
import com.wse.model.ParsedObject;
import com.wse.model.ReadObject;
import com.wse.parse.Posting;
import com.wse.parse.ReadGzip;
import com.wse.parse.ReadObjectParser;
import com.wse.parse.ThreadedPosting;
import com.wse.parse.ThreadedReadGzip;
import com.wse.parse.ThreadedReadObjectParser;
import com.wse.shell.ExecuteCommand;
import com.wse.shell.ThreadedExecuteCommand;
import com.wse.util.Config;
import com.wse.util.ElapsedTime;
import com.wse.util.FileReader;
import com.wse.util.Pair;

public class Main
{
	private static final String configPropPath = "src/main/resources/config.properties";
	private BlockingQueue<String> pathQueue;
	private BlockingQueue<ReadObject> readObjectQueue;
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	private BlockingQueue<Pair<Integer, Multiset<String>>> postingQueue;
	private BlockingQueue<String> priorityQueue;
	private Set<String> stopWords;
	
	private Config config;
	private ExecuteCommand executeCommand;
	private ReadObjectParser readObjectParser;
	private ReadGzip readGzip;
	private Posting posting; 
	private Writer writer;
	private FileReader fileReader;
	
	private Logger logger = LoggerFactory.getLogger(Main.class);
	
	public Main()
	{
		this.config = new Config(new File(configPropPath));
		this.pathQueue = new ArrayBlockingQueue<>(5000);
		this.readObjectQueue = new ArrayBlockingQueue<>(100000);
		this.parsedObjectQueue = new ArrayBlockingQueue<>(100000);
		this.fileReader = new FileReader(this.config.getStopWordsFilePath());
		
		this.stopWords = this.fileReader.getStopWords();
		this.executeCommand = new ExecuteCommand(this.config.getFindCommand(), pathQueue);
		this.readGzip = new ReadGzip(this.readObjectQueue);
		this.readObjectParser = new ReadObjectParser(parsedObjectQueue);
		this.posting = new Posting(priorityQueue);
		this.writer = new Writer(this.config.getOutputFilePath());
	}
	
	public static void main(String args[]) throws InterruptedException
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		Main main = new Main();
		main.execute();
		main.logger.debug("Total Time: "+ elapsedTime.getTotalTimeInSeconds() +" seconds");
	}
	
	public void execute() throws InterruptedException
	{
		ExecutorService executor = Executors.newCachedThreadPool();		
		//this.executeCommand.execute();
		executor.submit(new ThreadedExecuteCommand(this.executeCommand));
		executor.submit(new ThreadedReadGzip(this.readGzip, this.pathQueue));
		executor.submit(new ThreadedReadObjectParser(this.readObjectParser, this.readObjectQueue));
		executor.submit(new ThreadedPosting(this.parsedObjectQueue));
		executor.shutdownNow();
	    executor.awaitTermination(600, TimeUnit.SECONDS);
	    System.out.println(pathQueue.size());
	    System.out.println(readObjectQueue.size());
	    System.out.println(parsedObjectQueue.size());
	}
}
