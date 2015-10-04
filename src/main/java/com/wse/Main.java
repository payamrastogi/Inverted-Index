package com.wse;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multiset;
import com.wse.parse.Parser;
import com.wse.parse.ReadGzip;
import com.wse.parse.ThreadedParser;
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
	
	private Parser parser;
	private ReadGzip readGzip;
	
	private Logger logger = LoggerFactory.getLogger(Main.class);
	
	public Main()
	{
		this.pathQueue = new ArrayBlockingQueue<>(1000);
		this.contentQueue = new ArrayBlockingQueue<>(1000);
		this.postingQueue = new ArrayBlockingQueue<>(100000);
		this.readGzip = new ReadGzip();
		this.parser = new Parser();
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
		executor.shutdownNow();
	    executor.awaitTermination(100, TimeUnit.SECONDS);
	    System.out.println(pathQueue.size());
		System.out.println(postingQueue.size());
	}
}
