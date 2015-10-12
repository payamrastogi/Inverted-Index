package com.wse.shell;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class UnixSort 
{	
	private final String sortCommand;
	private final Logger logger = LoggerFactory.getLogger(UnixSort.class);
	private BlockingQueue<String> mergeFileQueue;
	
	public UnixSort(String sortCommand, BlockingQueue<String> mergeFileQueue)
	{
		this.sortCommand = sortCommand;
		this.mergeFileQueue = mergeFileQueue;
	}
	
	public void sortFile(String filePath)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		logger.debug("executeCommand"+this.sortCommand+ filePath+ " > "+filePath+"_sorted");
		try 
		{
			Process p  = new ProcessBuilder("/bin/bash", "-c",this.sortCommand+ filePath+ " -o "+filePath+"_sorted").start();
		    int returnCode = p.waitFor();
		    mergeFileQueue.add(filePath+"_sorted");
		    new ProcessBuilder("/bin/bash", "-c","rm "+ filePath).start();
		    logger.debug("executeCommand Return code : "+returnCode);
		} 
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
	}
}