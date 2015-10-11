package com.wse.parse;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.model.ReadObject;
import com.wse.util.ElapsedTime;

public class ThreadedReadGzip implements Runnable
{
	private ReadGzip readGzip;
	private BlockingQueue<String> pathQueue;
	//private BlockingQueue<ReadObject> readObjectQueue;
	//private BlockingQueue<ParsedObject> parsedObjectQueue;
	private CountDownLatch cld;
	private Logger logger = LoggerFactory.getLogger(ThreadedReadGzip.class);
	
	public ThreadedReadGzip(ReadGzip readGzip, BlockingQueue<String> pathQueue, BlockingQueue<ParsedObject> parsedObjectQueue, CountDownLatch cld)
	{
		this.readGzip = readGzip;
		this.pathQueue = pathQueue;
		//this.parsedObjectQueue = parsedObjectQueue;
		this.cld = cld;
	}
	
	public void run()
	{
		int count = 0;
		ElapsedTime elapsedTime = new ElapsedTime();
		for(int i=0;i<20;i++)
		{
			try
			{
				String path = null;
				while((path = this.pathQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					readGzip.read(new File(path));
					count++;
					if(count%10==0)
					{
						//this.readObjectQueue.wait(3000);
						logger.debug(cld.getCount()+"Done: "+ count+ " in " + elapsedTime.getTotalTimeInSeconds() + " seconds");
					}
				}
			}
			catch(InterruptedException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}
}