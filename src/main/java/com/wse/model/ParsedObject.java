package com.wse.model;

public class ParsedObject 
{
	private int volumeId;
	private int documentId;
	private StringBuilder parsedContent;
	
	public ParsedObject(int volumeId, int documentId, StringBuilder parsedContent)
	{
		this.volumeId = volumeId;
		this.documentId = documentId;
		this.parsedContent = parsedContent;
	}
	
	public int getVolumeId()
	{
		return this.volumeId;
	}
	
	public int getDocumentId()
	{
		return this.documentId;
	}
	
	public StringBuilder getParsedContent()
	{
		return this.parsedContent;
	}
	@Override
	public String toString()
	{
		return this.volumeId+" : "+this.documentId+" : "+this.parsedContent.length();
	}
}
