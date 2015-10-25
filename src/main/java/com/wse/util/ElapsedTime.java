package com.wse.util;
//to calculate execution time
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
