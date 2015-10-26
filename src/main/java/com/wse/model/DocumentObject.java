package com.wse.model;

public class DocumentObject 
{
	private long documentId;
	private String documentPath;
	private long documentLength;
	
	public DocumentObject()
	{
		
	}
	
	public DocumentObject(long documentId, String documentPath, long documentLength)
	{
		this.documentId = documentId;
		this.documentPath = documentPath;
		this.documentLength = documentLength;
	}
	
	public long getDocumentId()
	{
		return this.documentId;
	}
	
	public String getDocumentPath()
	{
		return this.documentPath;
	}
	
	public long getDocumentLength()
	{
		return this.documentLength;
	}
}
