package com.wse.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.SequenceGenerator;

public class Writer 
{
	private final Logger logger = LoggerFactory.getLogger(Writer.class);
	private final Map<Integer, BufferedWriter> writers;
	private String filePath;
	private int count;
	
	public Writer(String filePath)
	{
		this.filePath = filePath;
		this.writers = new HashMap<>();
	}
	
	public void write(WriteObject writeObject) throws IOException
	{
		if(!this.writers.containsKey(writeObject.getVolumeId()))
		{
			this.writers(writeObject.getVolumeId(), new BufferedWriter(new FileWriter(new File(filePath, writeObject.getVolumeId()))));
		}
		BufferedWriter writer = this.writers.get(writeObject.getVolumeId());
		if (writer == null) 
		{
	          throw new RuntimeException("No writer for volume: " + writeObject.getVolumeId());
	    }
		try 
		{
			writer.write(writeObject.getPosting());
	        count++;
	        if (count % 10000 == 0)
	        {
	            logger.info("Done: " + count);
	        }
	    } 
		catch (Exception e) 
		{
	          logger.error("Exception in writer: " + e.getMessage(), e);
	    }
	}
	
}
