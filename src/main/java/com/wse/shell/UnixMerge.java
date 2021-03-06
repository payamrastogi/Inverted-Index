package com.wse.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class UnixMerge 
{
	private final String mergeCommand;
	private final String outputPath;
	private final Logger logger = LoggerFactory.getLogger(UnixMerge.class);
	private int count;
	public UnixMerge(String mergeCommand, String outputPath)
	{
		this.mergeCommand = mergeCommand;
		this.outputPath = outputPath;
	}
	//execute unix merge command to merge all the sorted files
	//sort -m <file1> <file2> -o <output>
	public String mergeFiles(String file1, String file2)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		String outputFileName = outputPath+"/m_"+count;
		this.count++;
		logger.debug("execute: "+this.mergeCommand+ file1 + " "+file2+" -o " + outputFileName);
		try 
		{
			//execute sort -m (merge) command
			Process p  = new ProcessBuilder("/bin/bash", "-c",this.mergeCommand+ file1 + " "+file2+" -o " + outputFileName).start();
		    int returnCode = p.waitFor();
		    //delete sorted files
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

