package com.wse.parse;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadedIndexer implements Runnable
{
	private Indexer indexer;
	private BlockingQueue<String> toIndexQueue;
	private AtomicBoolean flag;
	private final Logger logger = LoggerFactory.getLogger(ThreadedIndexer.class);
	
	public ThreadedIndexer(Indexer indexer, BlockingQueue<String> toIndexQueue, AtomicBoolean flag)
	{
		this.flag = flag;
		this.indexer = indexer;
		this.toIndexQueue = toIndexQueue;
	}

	@Override
	public void run() 
	{
		for(int i=0;i<20;i++)
		{
			try
			{
				String filePath = null;
				while((filePath = toIndexQueue.poll(10, TimeUnit.SECONDS))!=null)
				{
					try
					{
						indexer.index(filePath, filePath+"_i");
					}
					catch(Exception e)
					{
						logger.debug(e.getMessage(), e);
					}
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
		this.flag.getAndSet(false);
	}
	
	
	
}
