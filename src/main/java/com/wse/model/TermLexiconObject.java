package com.wse.model;

import java.util.Comparator;

public class TermLexiconObject implements Comparator<TermLexiconObject>
{
	private String word;
	private long postingListStart;
	private long postingListEnd;
	private int postingListLength;
	
	public TermLexiconObject(String word, long postingListStart, long postingListEnd, int postingListLength)
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
	
	@Override
	public int compare(TermLexiconObject l1, TermLexiconObject l2) 
	{
		return l1.getWord().compareTo(l2.getWord());
	}
}
