package com.wse.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.DocumentObject;

public class DocumentReader 
{
	private Map<Long, DocumentObject> documentObjectMap;
	private String filePath;
	
	private Logger logger = LoggerFactory.getLogger(DocumentReader.class);

	public DocumentReader(String filePath)
	{
		this.filePath = filePath;
		this.documentObjectMap = new HashMap<>();
	}
	
	public Map<Long, DocumentObject> getDocumentObjectMap()
	{
		try(BufferedReader br = new BufferedReader(new FileReader(new File(this.filePath, "document"))))
		{
			String line = null;
			while((line=br.readLine())!=null)
			{
				String[] tokens = line.split("\t");
				if(tokens.length<3)
					throw new IllegalArgumentException();
				documentObjectMap.put(Long.parseLong(tokens[0]), new DocumentObject(Long.parseLong(tokens[0]), 
											tokens[1], Long.parseLong(tokens[2])));
			}
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return documentObjectMap;
	}
}
