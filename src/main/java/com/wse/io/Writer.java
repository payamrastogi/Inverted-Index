package com.wse.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.SequenceGenerator;

public class Writer 
{
	private final Logger logger = LoggerFactory.getLogger(Writer.class);
	private File outputFile;
	private String filePath;
	private final static int MAX_SIZE = 1024*1024*1024;
	private AtomicInteger currentSize = new AtomicInteger(0);
	
	public Writer(String filePath)
	{
		this.filePath = filePath;
		this.outputFile = new File(filePath, ""+SequenceGenerator.getNextInSequence(Writer.class));
	}
	
	public void write(String text)
	{
		if (currentSize.get() >= MAX_SIZE) {
			currentSize = new AtomicInteger(0);
			this.outputFile = new File(filePath, ""+SequenceGenerator.getNextInSequence(Writer.class));
		}
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true)))
		{
			currentSize.addAndGet(text.length());
			bw.write(text+"\n");
		}
		catch(IOException e)
		{
			logger.error("IOException", e);
		}	
	}
	
}
