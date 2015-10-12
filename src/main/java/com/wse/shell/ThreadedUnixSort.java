package com.wse.shell;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadedUnixSort implements Runnable
{
	private BlockingQueue<String> toSortQueue;
	private UnixSort unixSort;
	private final Logger logger = LoggerFactory.getLogger(ThreadedUnixSort.class);
	
	public ThreadedUnixSort(UnixSort unixSort, BlockingQueue<String> toSortQueue)
	{
		this.unixSort = unixSort;
		this.toSortQueue = toSortQueue;
	}
	
	public void run()
	{
		for(int i=0;i<10;i++)
		{
			try
			{
				String filePath = null;
				while((filePath = toSortQueue.poll(25, TimeUnit.SECONDS))!=null)
				{
					unixSort.sortFile(filePath);
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
	}
}
