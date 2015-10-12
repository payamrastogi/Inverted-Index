package com.wse.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;

public class ThreadedVolumeIndexer implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(ThreadedReadObjectParser.class);
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	private VolumeIndexer volumeIndexer;
	private List<ParsedObject> volumeParsedObjectList;
	private int count=0;
		
	public ThreadedVolumeIndexer(VolumeIndexer volumeIndexer, BlockingQueue<ParsedObject> parsedObjectQueue)
	{
		this.volumeIndexer = volumeIndexer;
		this.parsedObjectQueue = parsedObjectQueue;
		this.volumeParsedObjectList = new ArrayList<ParsedObject>();
	}
	
	public void run()
	{
		for(int i=0;i<10;i++)
		{
			try
			{
				ParsedObject parsedObject = null;
				while((parsedObject=this.parsedObjectQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					volumeIndexer.volumeIndex(parsedObject);
					logger.debug("parsedObjectQueue: "+parsedObjectQueue.size());

				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
	}
}