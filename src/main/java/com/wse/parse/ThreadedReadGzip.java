package com.wse.parse;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ThreadedReadGzip implements Runnable
{
	private ReadGzip readGzip;
	private BlockingQueue<String> pathQueue;
	private BlockingQueue<StringBuffer> contentQueue;
	
	//private Logger logger = LoggerFactory.getLogger(ThreadedReadGzip.class);
	
	public ThreadedReadGzip(ReadGzip readGzip, BlockingQueue<String> pathQueue, BlockingQueue<StringBuffer> contentQueue)
	{
		this.readGzip = readGzip;
		this.pathQueue = pathQueue;
		this.contentQueue = contentQueue;
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
					this.contentQueue.add(readGzip.read(new File(path)));
				}
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
