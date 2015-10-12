package com.wse.shell;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;

public class ThreadedUnixSort 
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
		for(int i=0;i<30;i++)
		{

			try
			{
				String filePath = null;
				while((filePath = toSortQueue.poll(10, TimeUnit.SECONDS))!=null)
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
