package com.wse.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.Lexicon;
//to read stop word list from file
public class FileReader 
{
	private Logger logger = LoggerFactory.getLogger(FileReader.class);
	private String filePath;
	public FileReader(String filePath)
	{
		this.filePath = filePath;
	}
	
	public Set<String> getStopWords()
	{
		File file = new File(this.filePath);
		Set<String> stopWords = null;
		try 
		{
			@SuppressWarnings("unchecked")
			List<String> list = FileUtils.readLines(file, "UTF-8");
			stopWords = new HashSet<>(list);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return stopWords;
	}
	
	public Lexicon[] getLexicons()
	{
		List<Lexicon> list = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new java.io.FileReader(this.filePath+ "/lexicon")))
		{
			String line = null;
			while((line=br.readLine())!=null)
			{
				String[] tokens = line.split("\\s");
				if(tokens==null || tokens.length<4)
					throw new IllegalArgumentException();
				Lexicon lexicon = new Lexicon(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
						Integer.parseInt(tokens[3]));
				list.add(lexicon);
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
		Lexicon[] lexicons = new Lexicon[list.size()];
		lexicons = list.toArray(lexicons);
		return lexicons;
	}
}
