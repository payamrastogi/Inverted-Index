package com.wse.model;

public class MetaObject 
{
	private long totalDocuments;
	private double averageLengthOfDocuments;
	
	public MetaObject(long totalDocuments, double averageLengthOfDocuments)
	{
		this.totalDocuments = totalDocuments;
		this.averageLengthOfDocuments = averageLengthOfDocuments;
	}
	
	public long getTotalDocuments()
	{
		return this.totalDocuments;
	}
	
	public double getAverageLengthOfDocuments()
	{
		return this.averageLengthOfDocuments;
	}
}
