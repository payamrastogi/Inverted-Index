package com.wse.model;

import java.util.Comparator;

public class Lexicon implements Comparator<Lexicon>
{
	private String word;
	private int postingListStart;
	private int postingListEnd;
	private int postingListLength;
	
	public Lexicon(String word, int postingListStart, int postingListEnd, int postingListLength)
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
	public int compare(Lexicon l1, Lexicon l2) 
	{
		return l1.getWord().compareTo(l2.getWord());
	}
	
}
