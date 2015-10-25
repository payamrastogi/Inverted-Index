package com.wse.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class ThreadedDocumentWriter implements Runnable
{
	private final Logger logger = LoggerFactory.getLogger(ThreadedParsedObjectWriter.class);
	private BlockingQueue<String> documentQueue;
	private int count=0;
	private String filePath;
	
	public ThreadedDocumentWriter(String filePath, BlockingQueue<String> documentQueue)
	{
		this.filePath = filePath+"/document";
		this.documentQueue = documentQueue;
	}
	
	public void run()
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		try(FileWriter writer = new FileWriter(new File(filePath))) {
		for(int i=0;i<30;i++)
		{
			try
			{
				String text = null;
				while((text=documentQueue.poll(10, TimeUnit.SECONDS))!=null)
				{
					writer.write(text);
					if (++count % 25000 ==0) 
					{
						logger.debug(" Tota Time: "+ elapsedTime.getTotalTimeInSeconds()+" seconds");
						logger.debug("documentQueue: "+documentQueue.size());
						count = 0;
					}
				}
			}
			catch(InterruptedException e)
			{
				logger.error("InterruptedException: "+ e);
			}
		}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
