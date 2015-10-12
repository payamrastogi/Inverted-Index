package com.wse.shell;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class UnixSort 
{	
	private final String sortCommand;
	private final Logger logger = LoggerFactory.getLogger(UnixSort.class);
	private BlockingQueue<String> mergeFileQueue1;
	private BlockingQueue<String> mergeFileQueue2;
	
	public UnixSort(String sortCommand, BlockingQueue<String> mergeFileQueue1,BlockingQueue<String> mergeFileQueue2)
	{
		this.sortCommand = sortCommand;
		this.mergeFileQueue1 = mergeFileQueue1;
		this.mergeFileQueue2 = mergeFileQueue2;
	}
	
	public void sortFile(String filePath)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		logger.debug("execute: "+this.sortCommand+ filePath+ " -o "+filePath+"_sorted");
		try 
		{
			Process p  = new ProcessBuilder("/bin/bash", "-c",this.sortCommand+ filePath+ " -o "+filePath+"_sorted").start();
		    int returnCode = p.waitFor();
		    if (mergeFileQueue2.size() <= mergeFileQueue1.size()) {
		    	mergeFileQueue2.add(filePath+"_sorted");
		    } else {
		    	mergeFileQueue1.add(filePath+"_sorted");
		    }
		    logger.debug("Queue size : "+mergeFileQueue1.size() + "--" + mergeFileQueue2.size());
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