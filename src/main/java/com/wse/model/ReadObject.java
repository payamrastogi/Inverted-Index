package com.wse.model;

public class ReadObject 
{
	private int volumeId;
	private byte[] content;
	private int documentId;
	
	public ReadObject(int volumeId, byte[] content, int documentId)
	{
		this.volumeId  = volumeId;
		this.content = content;
		this.documentId = documentId;
	}
	
	public int getVolumeId()
	{
		return this.volumeId;
	}
	
	public byte[] getContent()
	{
		return this.content;
	}
	
	public int getDocumentId()
	{
		return this.documentId;
	}
	
	public String toString()
	{
		return this.volumeId +" : "+this.documentId+" : "+ new String(this.content).length();
	}
	
}
