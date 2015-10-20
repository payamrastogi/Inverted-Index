package com.wse.model;

public class PostingObject 
{
	private long documentId;
	private long frequency;
	
	public PostingObject(long documentId, long frequency)
	{
		this.documentId = documentId;
		this.frequency = frequency;
	}
	
	public long getDocumentId()
	{
		return this.documentId;
	}
	
	public long getFrequency()
	{
		return this.frequency;
	}
}
