package com.wse.model;

import java.util.Comparator;

public class LexiconObject
{
	private String word;
	private long postingListStart;
	private long postingListEnd;
	private int postingListLength;
	
	public LexiconObject()
	{
		
	}
	
	public LexiconObject(String word, long postingListStart, long postingListEnd, int postingListLength)
	{
		this.word = word;
		this.postingListStart = postingListStart;
		this.postingListEnd = postingListEnd;
		this.postingListLength = postingListLength;
	}
	
	public String getWord()
	{
		return this.word;
	}
	
	public long getPostingListStart()
	{
		return this.postingListStart;
	}
	
	public long getPostingListEnd()
	{
		return this.postingListEnd;
	}
	
	public int getPostingListLength()
	{
		return this.postingListLength;
	}
}
