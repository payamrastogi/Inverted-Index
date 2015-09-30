package com.wse.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Execute 
{
	private Logger logger = LoggerFactory.getLogger(Execute.class);
	public String executeCommand(String command) 
	{
		logger.debug("executeCommand");
		StringBuffer output = new StringBuffer();
		Process process;
		try 
		{
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";			
			while ((line = reader.readLine())!= null) 
			{
				output.append(line + "\n");
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		return output.toString();

	}
}
