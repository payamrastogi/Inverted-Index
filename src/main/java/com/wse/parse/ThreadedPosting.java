package com.wse.parse;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.util.ElapsedTime;

public class ThreadedPosting implements Runnable
{
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	private final Logger logger = LoggerFactory.getLogger(ThreadedPosting.class);
	
	public ThreadedPosting(BlockingQueue<ParsedObject> parsedObjectQueue)
	{
		this.parsedObjectQueue = parsedObjectQueue;
	}
	
	public void run()
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		int count = 0;
		for(int i=0;i<20;i++)
		{
			try
			{
				ParsedObject parsedObject = null;
				while((parsedObject = parsedObjectQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					count++;
					if(count%10000 == 0)
						logger.debug("Done: " +count+" in "+ elapsedTime.getTotalTimeInSeconds() + " seconds");
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
	}
}
