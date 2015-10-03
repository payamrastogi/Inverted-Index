package com.wse.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class Execute 
{
	private Logger logger = LoggerFactory.getLogger(Execute.class);
	public List<String> executeCommand(String command) 
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		logger.debug("executeCommand");
		List<String> list = null;
		StringBuffer output = new StringBuffer();
		Process process;
		try 
		{
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
			{
				String line = "";
				list = new ArrayList<>(100);
				while ((line = reader.readLine())!= null) 
				{
					list.add(line);
					//output.append(line + "\n");
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
		return list;
	}
}
