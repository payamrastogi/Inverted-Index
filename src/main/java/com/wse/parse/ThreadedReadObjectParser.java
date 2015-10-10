package com.wse.parse;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ReadObject;

public class ThreadedReadObjectParser implements Runnable
{
	private final Logger logger = LoggerFactory.getLogger(ThreadedReadObjectParser.class);
	private BlockingQueue<ReadObject> readObjectQueue;
	private ReadObjectParser parser;
		
	public ThreadedReadObjectParser(ReadObjectParser parser, BlockingQueue<ReadObject> readObjectQueue)
	{
		this.parser = parser;
		this.readObjectQueue = readObjectQueue;
	}
	
	public void run()
	{
		for(int i=0;i<5;i++)
		{
			try
			{
				ReadObject readObject = null;
				while((readObject=this.readObjectQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					parser.parseText(readObject);
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
	}
}