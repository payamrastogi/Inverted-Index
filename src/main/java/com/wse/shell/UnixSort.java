package com.wse.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class UnixSort 
{	
	private final String sortCommand;
	private final Logger logger = LoggerFactory.getLogger(UnixSort.class);
	public UnixSort(String sortCommand)
	{
		this.sortCommand = sortCommand;
	}
	
	public void sortFile(String filePath)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		logger.debug("executeCommand"+this.sortCommand+ filePath+ " > "+filePath+"_sorted");
		Process process;
		try 
		{
			process = Runtime.getRuntime().exec(this.sortCommand+ filePath+ " > sorted_"+filePath);
			process.waitFor();
		} 
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch(InterruptedException e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
	}
}
