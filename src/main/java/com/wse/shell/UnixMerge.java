package com.wse.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class UnixMerge 
{
	private final String mergeCommand;
	private final Logger logger = LoggerFactory.getLogger(UnixMerge.class);
	private int count;
	public UnixMerge(String mergeCommand)
	{
		this.mergeCommand = mergeCommand;
	}
	
	public String mergeFiles(String file1, String file2, String outputPath)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		String outputFileName = outputPath+"m_"+count;
		this.count++;
		logger.debug("execute: "+this.mergeCommand+ file1 + " "+file2+" -o " + outputFileName);
		try 
		{
			Process p  = new ProcessBuilder("/bin/bash", "-c",this.mergeCommand+ file1 + " "+file2+" -o " + outputFileName).start();
		    int returnCode = p.waitFor();
		    new ProcessBuilder("/bin/bash", "-c","rm "+ file1).start();
		    new ProcessBuilder("/bin/bash", "-c","rm "+ file2).start();
		    logger.debug("execute Return code : "+returnCode);
		} 
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
		return outputFileName;
	}
}

