package com.wse.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.model.VolumeIndexedObject;

public class Writer 
{
	private final Logger logger = LoggerFactory.getLogger(Writer.class);
	private final Map<Integer, BufferedWriter> writers;
	private Set<String> stopWords;
	private String filePath;
	
	public Writer(String filePath, Set<String> stopWords) throws Exception
	{
		this.filePath = filePath;
		this.stopWords = stopWords;
		this.writers = new ConcurrentHashMap<>();
		for(int i=0;i<45;i++)
			this.writers.put(i,  new BufferedWriter(new FileWriter(new File(filePath, i+""))));
	}
	
	public void write(ParsedObject parsedObject)
	{
		try 
		{
			String[] wordContents = parsedObject.getParsedContent().toString().split("\\n");
			int documentId = parsedObject.getDocumentId();
			int volumeId = parsedObject.getVolumeId();
			for (String wordContent : wordContents) 
			{
				String word = wordContent.split(" ")[0];
				if (word== null || word.trim().isEmpty() || word.matches(".*\\d+.*") || word.matches("((\\w)\\2\\2)+") ||stopWords.contains(word))
					continue;
				BufferedWriter writer = this.writers.get(parsedObject.getVolumeId());
				if (writer == null) 
				{
			          throw new RuntimeException("No writer for volume: " + parsedObject.getVolumeId());
			    }
				writer.write(word+"\t"+documentId);
			}
	    } 
		catch (Exception e) 
		{
	          logger.error("Exception in writer: " + e.getMessage(), e);
	    }
	}
	
}
