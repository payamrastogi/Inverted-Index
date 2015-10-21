package com.wse.model;

public class MetaObject 
{
	private long totalDocuments;
	private double averageLengthOfDocuments;
	private Integer lexiconCount;
	
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
	
	public Integer getLexiconCount()
	{
		return this.lexiconCount;
	}
}
