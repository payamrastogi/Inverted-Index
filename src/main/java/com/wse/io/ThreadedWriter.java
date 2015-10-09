package com.wse.io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class ThreadedWriter implements Runnable
{
	private final Logger logger = LoggerFactory.getLogger(ThreadedWriter.class);
	private BlockingQueue<String> priorityQueue;
	private AtomicInteger count = new AtomicInteger(1);
	private Writer writer;
	
	public ThreadedWriter(Writer writer, BlockingQueue<String> priorityQueue)
	{
		this.writer = writer;
		this.priorityQueue = priorityQueue;
	}
	
	public void run()
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		for(int i=0;i<30;i++)
		{
			try
			{
				String text = null;
				while((text=priorityQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					writer.write(text);
					count.getAndIncrement();
					if(count.get()%10000==0)
						logger.debug("Tota Time: "+ elapsedTime.getTotalTimeInSeconds()+" seconds");
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
		
	}
}
