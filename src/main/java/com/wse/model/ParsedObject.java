package com.wse.model;

//Parsed Object 
//volumeId - unique id of the volume in NZ data
//documentId - unique id of the document 
//parsedContent - StringBuilder return by the Parser.parseDoc
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
