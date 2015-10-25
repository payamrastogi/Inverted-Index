package com.wse.util;

import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseUtil 
{
	static final Logger logger = LoggerFactory.getLogger(CloseUtil.class);

	private CloseUtil() 
	{
		// this class is not designed to be instantiated
	}

	public static void close(Closeable c) 
	{
		if (c != null) 
		{
			try 
			{
				c.close();
			} 
			catch (Exception e) 
			{
				logger.warn(e.getMessage(), e);
			}
		}
	}
}
