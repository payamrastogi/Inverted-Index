package com.wse.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class Execute 
{
	private BlockingQueue<String> pathQueue;
	private Logger logger = LoggerFactory.getLogger(Execute.class);
	
	public Execute(BlockingQueue<String> pathQueue)
	{
		this.pathQueue = pathQueue;
	}
	
	public void executeCommand(String command) 
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		logger.debug("executeCommand");
		Process process;
		try 
		{
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
			{
				String line = "";
				while ((line = reader.readLine())!= null) 
				{
					pathQueue.add(line);
				}
			}
		} 
		catch (IOException e)
		{
			logger.error(e.getMessage());
		}
		catch(InterruptedException e)
		{
			logger.error(e.getMessage());
		}
		logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
	}
}
