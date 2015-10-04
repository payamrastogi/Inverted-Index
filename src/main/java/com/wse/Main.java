package com.wse;

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
import com.wse.parse.Parser;
import com.wse.parse.Posting;
import com.wse.parse.ReadGzip;
import com.wse.parse.ThreadedParser;
import com.wse.parse.ThreadedPosting;
import com.wse.parse.ThreadedReadGzip;
import com.wse.shell.Execute;
import com.wse.util.ElapsedTime;
import com.wse.util.Pair;

public class Main
{
	private final static String getFilePaths = "find /Users/payamrastogi/NZ/data -regex .*/*_data -print";
	
	private BlockingQueue<String> pathQueue;
	private BlockingQueue<StringBuffer> contentQueue;
	private BlockingQueue<Pair<Integer, Multiset<String>>> postingQueue;
	private BlockingQueue<String> priorityQueue;
	
	private Parser parser;
	private ReadGzip readGzip;
	private Posting posting; 
	private Writer writer;
	
	private Logger logger = LoggerFactory.getLogger(Main.class);
	
	public Main()
	{
		this.pathQueue = new ArrayBlockingQueue<>(1000);
		this.contentQueue = new ArrayBlockingQueue<>(1000);
		this.postingQueue = new ArrayBlockingQueue<>(100000);
		this.priorityQueue = new PriorityBlockingQueue<String>();
		
		this.readGzip = new ReadGzip();
		this.parser = new Parser();
		this.posting = new Posting(priorityQueue);
		this.writer = new Writer();
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
		
		Execute execute = new Execute(pathQueue);
		execute.executeCommand(getFilePaths);
		System.out.println(pathQueue.size());
		executor.submit(new ThreadedReadGzip(readGzip, pathQueue, contentQueue));
		for(int i=0;i<15;i++)
			executor.submit(new ThreadedParser(parser, contentQueue, postingQueue));
		executor.submit(new ThreadedPosting(posting, postingQueue));
		//executor.submit(new ThreadedPosting(posting, postingQueue));
		for(int i=0;i<30;i++)
			executor.submit(new ThreadedWriter(writer, priorityQueue));
		executor.shutdownNow();
	    executor.awaitTermination(200, TimeUnit.SECONDS);
	    System.out.println(pathQueue.size());
		System.out.println(postingQueue.size());
		System.out.println(priorityQueue.size());
	}
}
