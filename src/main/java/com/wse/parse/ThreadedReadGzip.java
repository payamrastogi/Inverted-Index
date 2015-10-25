package com.wse.parse;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class ThreadedReadGzip implements Runnable
{
	private ReadGzip readGzip;
	private BlockingQueue<String> pathQueue;
	private AtomicBoolean flagReadGzip;
	private String dataType;
	private Logger logger = LoggerFactory.getLogger(ThreadedReadGzip.class);
	
	public ThreadedReadGzip(ReadGzip readGzip, BlockingQueue<String> pathQueue,AtomicBoolean flagReadGzip, String dataType)
	{
		this.flagReadGzip = flagReadGzip;
		this.readGzip = readGzip;
		this.pathQueue = pathQueue;
		this.dataType = dataType;
	}
	
	public void run()
	{
		int count = 0;
		ElapsedTime elapsedTime = new ElapsedTime();
		for(int i=0;i<10;i++)
		{
			try
			{
				String path = null;
				while((path = this.pathQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					if(this.dataType.equals("NZ"))
						readGzip.readNZ(new File(path));
					else
						readGzip.readCC(new File(path));
					count++;
					if(count%25000==0)
					{
						logger.debug("Done: "+ count+ " Total Time: " + elapsedTime.getTotalTimeInSeconds() + " seconds");
						logger.debug("pathQueue: "+pathQueue.size());
					}
				}
			}
			catch(InterruptedException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
		this.flagReadGzip.getAndSet(false);
	}
}