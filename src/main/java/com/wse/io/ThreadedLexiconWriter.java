package com.wse.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class ThreadedLexiconWriter implements Runnable
{
	private final Logger logger = LoggerFactory.getLogger(ThreadedLexiconWriter.class);
	private BlockingQueue<String> lexiconQueue;
	private FileWriter writer;
	private String filePath;
	private Integer count;
	
	public ThreadedLexiconWriter(BlockingQueue<String> lexiconQueue, String filePath, Integer count)
	{
		this.lexiconQueue = lexiconQueue;
		this.filePath = filePath;
		this.count = count;
	}
	
	public void run()
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		try
		{
			this.writer = new FileWriter(new File(filePath, "lexicon"));
			for(int i=0;i<30;i++)
			{
				try
				{
					String text = null;
					while((text=lexiconQueue.poll(10, TimeUnit.SECONDS))!=null)
					{
						writer.write(text);
						if (++count % 25000 ==0) 
						{
							logger.debug(" Tota Time: "+ elapsedTime.getTotalTimeInSeconds()+" seconds");
							logger.debug("lexiconQueue: "+lexiconQueue.size());
						}
					}
				}
				catch(InterruptedException e)
				{
					logger.error("InterruptedException: "+ e);
				}
			}
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch(IOException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}
}
