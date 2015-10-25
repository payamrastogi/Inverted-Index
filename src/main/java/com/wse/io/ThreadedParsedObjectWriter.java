package com.wse.io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.util.ElapsedTime;

public class ThreadedParsedObjectWriter implements Runnable
{
	private final Logger logger = LoggerFactory.getLogger(ThreadedParsedObjectWriter.class);
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	private AtomicInteger flagWriter;
	private int count=0;
	private ParsedObjectWriter writer;
	
	public ThreadedParsedObjectWriter(ParsedObjectWriter writer, BlockingQueue<ParsedObject> parsedObjectQueue, AtomicInteger flagWriter)
	{
		this.writer = writer;
		this.flagWriter = flagWriter;
		this.parsedObjectQueue = parsedObjectQueue;
	}
	
	public void run()
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		for(int i=0;i<30;i++)
		{
			try
			{
				ParsedObject parsedObject = null;
				while((parsedObject=parsedObjectQueue.poll(10, TimeUnit.SECONDS))!=null)
				{
					writer.write(parsedObject);
					if (++count % 25000 ==0) 
					{
						logger.debug(" Tota Time: "+ elapsedTime.getTotalTimeInSeconds()+" seconds");
						logger.debug("parsedObjectQueue: "+parsedObjectQueue.size());
						count = 0;
					}
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
		flagWriter.decrementAndGet();
	}
}
