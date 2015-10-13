package com.wse.shell;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class UnixSort 
{	
	private final String sortCommand;
	private final Logger logger = LoggerFactory.getLogger(UnixSort.class);
	private BlockingQueue<String> indexFileQueue;
	
	public UnixSort(String sortCommand, BlockingQueue<String> indexFileQueue)
	{
		this.sortCommand = sortCommand;
		this.indexFileQueue = indexFileQueue;
	}
	//execute sort command to sort parsed files 
	//sort -k1,1 -k2,2n <file1> -o <output>
	public void sortFile(String filePath)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		logger.debug("execute: "+this.sortCommand+ filePath+ " -o "+filePath+"_sorted");
		try 
		{
			//execute sort command
			Process p  = new ProcessBuilder("/bin/bash", "-c",this.sortCommand+ filePath+ " -o "+filePath+"_sorted").start();
		    int returnCode = p.waitFor();
		    indexFileQueue.add(filePath+"_sorted");
		    //deleted unsorted file
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