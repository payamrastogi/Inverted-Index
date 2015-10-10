package com.wse.parse;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadedReadGzip implements Runnable
{
	private ReadGzip readGzip;
	private BlockingQueue<String> pathQueue;
	
	private Logger logger = LoggerFactory.getLogger(ThreadedReadGzip.class);
	
	public ThreadedReadGzip(ReadGzip readGzip, BlockingQueue<String> pathQueue)
	{
		this.readGzip = readGzip;
		this.pathQueue = pathQueue;
	}
	
	public void run()
	{
		for(int i=0;i<3;i++)
		{
			try
			{
				String path = null;
				while((path = this.pathQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					readGzip.read(new File(path));
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
	}
}