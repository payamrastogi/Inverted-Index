package com.wse.util;

public class ElapsedTime 
{
	private final long startTime;
	public ElapsedTime()
	{
		this.startTime = System.currentTimeMillis();
	}
	
	public double getTotalTimeInSeconds()
	{
		return (System.currentTimeMillis() - startTime)/1000.0;
	}
}
