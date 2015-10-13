package com.wse.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
//to read stop word list from file
public class FileReader 
{
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
}
