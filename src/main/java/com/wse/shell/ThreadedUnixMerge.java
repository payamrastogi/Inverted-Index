package com.wse.shell;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadedUnixMerge implements Runnable
{
	private BlockingQueue<String> toMergeQueue;
	private UnixMerge unixMerge;
	private final Logger logger = LoggerFactory.getLogger(ThreadedUnixSort.class);
	
	public ThreadedUnixMerge(UnixMerge unixMerge, BlockingQueue<String> toMergeQueue)
	{
		this.unixMerge = unixMerge;
		this.toMergeQueue = toMergeQueue;
	}
	
	public void run()
	{
		for(int i=0;i<10;i++)
		{
			try
			{
				String filePath1 = null;
				String filePath2 = null;
				while((filePath1 = toMergeQueue.poll(25, TimeUnit.SECONDS))!=null && (filePath2 = toMergeQueue.poll(25, TimeUnit.SECONDS))!=null)
				{
					String outputPath = new File(filePath1).getParent();
					toMergeQueue.add(this.unixMerge.mergeFiles(filePath1, filePath2, outputPath));
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
	}
}
