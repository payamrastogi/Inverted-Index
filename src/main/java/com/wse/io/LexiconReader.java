package com.wse.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.LexiconObject;

public class LexiconReader 
{
	private String filePath;
	private Map<String, LexiconObject> lexiconObjectMap;
	private Logger logger = LoggerFactory.getLogger(LexiconReader.class);
	
	public LexiconReader(String filePath, Map<String, LexiconObject> lexiconObjectMap)
	{
		this.filePath = filePath;
		this.lexiconObjectMap = lexiconObjectMap;
	}
	
	public Map<String, LexiconObject> getLexiconObjectMap()
	{
		try(BufferedReader br = new BufferedReader(new java.io.FileReader(this.filePath+ "/lexicon")))
		{
			String line = null;
			while((line=br.readLine())!=null)
			{
				String[] tokens = line.split("\\s");
				if(tokens==null || tokens.length<4)
					throw new IllegalArgumentException();
				LexiconObject lexiconObject = new LexiconObject(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
						Integer.parseInt(tokens[3]));
				lexiconObjectMap.put(tokens[0], lexiconObject);
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
		return lexiconObjectMap;
	}
}
