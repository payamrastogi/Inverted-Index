package com.wse.model;

import java.util.Comparator;

public class LexiconObject implements Comparator<LexiconObject>
{
	private String word;
	private int postingListStart;
	private int postingListEnd;
	private int postingListLength;
	
	public LexiconObject(String word, int postingListStart, int postingListEnd, int postingListLength)
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
	
	public int getPostingListStart()
	{
		return this.postingListStart;
	}
	
	public int getPostingListEnd()
	{
		return this.postingListEnd;
	}
	
	public int getPostingListLength()
	{
		return this.postingListLength;
	}
	
	@Override
	public int compare(LexiconObject l1, LexiconObject l2) 
	{
		return l1.getWord().compareTo(l2.getWord());
	}
	
}
