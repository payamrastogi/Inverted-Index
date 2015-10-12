package com.wse.shell;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadedUnixMerge implements Runnable
{
	private BlockingQueue<String> mergeQueue1;
	private BlockingQueue<String> mergeQueue2;
	private UnixMerge unixMerge;
	private final Logger logger = LoggerFactory.getLogger(ThreadedUnixSort.class);
	
	public ThreadedUnixMerge(UnixMerge unixMerge, BlockingQueue<String> mergeQueue1, BlockingQueue<String> mergeQueue2)
	{
		this.unixMerge = unixMerge;
		this.mergeQueue1 = mergeQueue1;
		this.mergeQueue2 = mergeQueue2;
	}
	
	public void run()
	{
		for(int i=0;i<50;i++)
		{
			try
			{
				String filePath1 = null;
				String filePath2 = null;
				logger.debug("Queue size : "+mergeQueue1.size() + "--" + mergeQueue2.size());
				while((filePath1 = mergeQueue1.poll(10, TimeUnit.SECONDS))!=null && (filePath2 = mergeQueue2.poll(10, TimeUnit.SECONDS))!=null)
				{
					if (mergeQueue2.size() <= mergeQueue1.size()) {
						mergeQueue2.add(this.unixMerge.mergeFiles(filePath1, filePath2));
				    } else {
				    	mergeQueue1.add(this.unixMerge.mergeFiles(filePath1, filePath2));
				    }
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
	}
}
