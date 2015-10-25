package com.wse.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class ExecuteCommand 
{
	private String command;
	private BlockingQueue<String> pathQueue;
	private Logger logger = LoggerFactory.getLogger(ExecuteCommand.class);
	
	public ExecuteCommand(String command, BlockingQueue<String> pathQueue)
	{
		this.command = command;
		this.pathQueue = pathQueue;
	}
	//execute find command to list path of all the index files
	//find /Users/payamrastogi/NZ/data/4c/tux-4/polybot/gzipped_sorted_nz -regex .*/*_index -print
	public void execute() 
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		logger.debug("executeCommand");
		Process process;
		try 
		{
			process = Runtime.getRuntime().exec(this.command);
			//process.waitFor();
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
			{
				String line = "";
				while ((line = reader.readLine())!= null) 
				{
					this.pathQueue.add(line);;
				}
			}
		} 
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
	}
}
