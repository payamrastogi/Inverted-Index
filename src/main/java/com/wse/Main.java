package com.wse;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multiset;
import com.wse.io.ThreadedWriter;
import com.wse.io.Writer;
import com.wse.model.ReadObject;
import com.wse.parse.Parser;
import com.wse.parse.Posting;
import com.wse.parse.ReadGzip;
import com.wse.parse.ThreadedReadObjectParser;
import com.wse.parse.ThreadedPosting;
import com.wse.parse.ThreadedReadGzip;
import com.wse.shell.Execute;
import com.wse.util.Config;
import com.wse.util.ElapsedTime;
import com.wse.util.FileReader;
import com.wse.util.Pair;

public class Main
{
	private static final String configPropPath = "src/main/resources/config.properties";
	private BlockingQueue<ReadObject> readObjectQueue;
	private BlockingQueue<String> pathQueue;
	private BlockingQueue<StringBuffer> contentQueue;
	private BlockingQueue<Pair<Integer, Multiset<String>>> postingQueue;
	private BlockingQueue<String> priorityQueue;
	private Set<String> stopWords;
	
	private Config config;
	private Parser parser;
	private ReadGzip readGzip;
	private Posting posting; 
	private Writer writer;
	private FileReader fileReader;
	
	private Logger logger = LoggerFactory.getLogger(Main.class);
	
	public Main()
	{
		this.config = new Config(new File(configPropPath));
		this.pathQueue = new ArrayBlockingQueue<>(5000);
		this.readObjectQueue = new ArrayBlockingQueue<>(10000);
		this.contentQueue = new ArrayBlockingQueue<>(1000);
		this.postingQueue = new ArrayBlockingQueue<>(100000000);
		this.priorityQueue = new ArrayBlockingQueue<String>(10000000);
		this.fileReader = new FileReader(this.config.getStopWordsFilePath());
		
		this.stopWords = this.fileReader.getStopWords();
		this.readGzip = new ReadGzip(this.readObjectQueue);
		this.parser = new Parser(this.stopWords);
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
		//ExecutorService executor = Executors.newCachedThreadPool();	
		
		Execute execute = new Execute(pathQueue);
		System.out.println(this.config.getFindCommand());
		execute.executeCommand(this.config.getFindCommand());
		System.out.println(pathQueue.size());
		System.out.println(pathQueue.poll());
		//executor.submit(new ThreadedReadGzip(readGzip, pathQueue, contentQueue));
		//for(int i=0;i<15;i++)
		//	executor.submit(new ThreadedParser(parser, contentQueue, postingQueue));
		//executor.submit(new ThreadedPosting(posting, postingQueue));
		//for(int i=0;i<30;i++)
		//	executor.submit(new ThreadedWriter(writer, priorityQueue));
		//executor.shutdownNow();
	    //executor.awaitTermination(600, TimeUnit.SECONDS);
	    //System.out.println(pathQueue.size());
		//System.out.println(postingQueue.size());
		//System.out.println(priorityQueue.size());
	}
}
