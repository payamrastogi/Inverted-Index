package com.wse.io;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentWriter {
	private final Logger logger = LoggerFactory.getLogger(DocumentWriter.class);
	private FileWriter writer;
	private String filePath;
	private char prefix='x';
	private int count;
	private int fileCount;

	
	public DocumentWriter(String filePath) throws Exception
	{
		this.filePath = filePath;
		this.writer = new FileWriter(new File(filePath, prefix+"0"));
	}
	// input param : ParsedObject to be written in file
	public void write(String text)
	{
		try 
		{
			this.writer.write(text);
			if (++this.count%25000 == 0) 
			{
				this.writer.flush();
				this.writer.close();
				this.count = 0;
				this.writer = new FileWriter(new File(filePath, prefix+""+(++this.fileCount)));
			}
	    } 
		catch (Exception e) 
		{
	          logger.error("Exception in writer: " + e.getMessage(), e);
	    }
	}
	public void writeLast()
	{
		try
		{
			this.writer.flush();
			this.writer.close();
			this.count = 0;
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}
