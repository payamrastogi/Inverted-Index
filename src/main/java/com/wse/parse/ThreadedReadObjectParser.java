package com.wse.parse;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.model.ReadObject;

import edu.poly.cs912.Parser;

public class ThreadedReadObjectParser implements Runnable
{
	private final Logger logger = LoggerFactory.getLogger(ThreadedReadObjectParser.class);
	private BlockingQueue<ReadObject> readObjectQueue;
	private ReadObjectParser readObjectParser;
	
	private CountDownLatch cld;
	
	public ThreadedReadObjectParser(ReadObjectParser readObjectParser,BlockingQueue<ReadObject> readObjectQueue, CountDownLatch cld)
	{
		this.readObjectParser = readObjectParser;
		this.readObjectQueue = readObjectQueue;
		this.cld = cld;
	}
	
	public void run()
	{
		int count = 0;
		for(int i=0;i<20;i++)
		{
			try
			{
				ReadObject readObject = null;
				while((readObject=readObjectQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					readObjectParser.parseText(readObject);
					count++;
					if(count%1000==0)
					{
						//readObjectQueue.notify();
						//if(readObjectQueue.size()<5000)
						//	cld.countDown();
						logger.debug("Done: "+count+" readObjectQueue: "+ readObjectQueue.size());
					}
				}
			}
			catch(IOException | InterruptedException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}
}