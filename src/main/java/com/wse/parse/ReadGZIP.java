package com.wse.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

public class ReadGZIP 
{
	private Logger logger = LoggerFactory.getLogger(ReadGZIP.class);
	public StringBuffer read(File file)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		StringBuffer stringBuffer = null;
		try(FileInputStream fis = new FileInputStream(file)) 
		{
			GZIPInputStream gzis = new GZIPInputStream(fis);
			InputStreamReader isr = new InputStreamReader(gzis);
			try(BufferedReader br = new BufferedReader(isr))
			{
				stringBuffer = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null)
					stringBuffer.append(line);
			}
		} 
		catch (IOException e) 
		{
			logger.error(e.getMessage());
		}
		logger.debug("Total Time: "+ elapsedTime.getTotalTimeInSeconds() + " seconds");
		return stringBuffer;
	}
}	
