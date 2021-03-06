package com.wse.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config
{
	private String findCommand;
	private String stopWordsFilePath;
	private String outputfilePath;
	private String sortCommand;
	private String mergeCommand;
	private String dataType;
	
	public Config(File configFile) 
	{
	    this(loadProperties(configFile));
	}
	
	private static Properties loadProperties(File file) 
	{
	    FileInputStream is = null;
	    try
	    {
	    	is = new FileInputStream(file);
	    	return loadProperties(is);
	    } 
	    catch (IOException e) 
	    {
	    	throw new RuntimeException("Error loading property file " + file.getAbsolutePath(), e);
	    } 
	    finally 
	    {
	    	CloseUtil.close(is);
	    }
	}
	
	Config(Properties prop) 
	{
		this.findCommand = prop.getProperty("findCommand");
		this.stopWordsFilePath = prop.getProperty("stopWordsFilePath");
		this.outputfilePath=prop.getProperty("outputFilePath");
		this.sortCommand = prop.getProperty("sortCommand");
		this.mergeCommand = prop.getProperty("mergeCommand");
		this.dataType = prop.getProperty("dataType");
	}
	
	private static Properties loadProperties(InputStream is) 
	{
	    Properties prop = new Properties();
	    try 
	    {
	      prop.load(is);
	    } 
	    catch (IOException e) 
	    {
	      throw new RuntimeException("Error loading property from stream", e);
	    }
	    return prop;
	}
	
	public String getFindCommand()
	{
		return this.findCommand;
	}
	
	public String getStopWordsFilePath()
	{
		return this.stopWordsFilePath;
	}
	
	public String getOutputFilePath()
	{
		return this.outputfilePath;
	}

	public String getSortCommand() {
		return sortCommand;
	}

	public String getMergeCommand() {
		return mergeCommand;
	}
	
	public String getDataType(){
		return this.dataType;
	}
}
