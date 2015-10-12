package com.wse.io;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;

public class Writer 
{
	private final Logger logger = LoggerFactory.getLogger(Writer.class);
	private FileWriter writer;
	private Set<String> stopWords;
	private String filePath;
	private char prefix;
	private int count;
	private int fileCount;
	private BlockingQueue<String> sortFileQueue;
	
	public Writer(String filePath,char prefix,Set<String> stopWords, BlockingQueue<String> sortFileQueue) throws Exception
	{
		this.filePath = filePath;
		this.stopWords = stopWords;
		this.prefix = prefix;
		this.writer = new FileWriter(new File(filePath, prefix+"0"));
		this.sortFileQueue = sortFileQueue;
	}
	
	public void write(ParsedObject parsedObject)
	{
		try 
		{
			String[] wordContents = parsedObject.getParsedContent().toString().split("\\n");
			int documentId = parsedObject.getDocumentId();
			for (String wordContent : wordContents) 
			{
				String word = wordContent.split(" ")[0];
				if (word== null || word.trim().isEmpty() || word.matches(".*\\d+.*") ||stopWords.contains(word))
					continue;
				this.writer.write(word+"\t"+documentId+"\n");
			}
			if (++this.count%25000 == 0) 
			{
				this.writer.flush();
				this.writer.close();
				this.count = 0;
				this.sortFileQueue.add(filePath+"/"+prefix+(this.fileCount));
				this.writer = new FileWriter(new File(filePath, prefix+""+(++this.fileCount)));
			}
	    } 
		catch (Exception e) 
		{
	          logger.error("Exception in writer: " + e.getMessage(), e);
	    }
	}
	
}
