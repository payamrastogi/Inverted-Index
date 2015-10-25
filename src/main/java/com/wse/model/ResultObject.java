package com.wse.model;

import java.util.Comparator;

public class ResultObject implements Comparator<ResultObject>
{
	private long documentId;
	private double bm25Score;
	
	public ResultObject(long documentId)
	{
		this.documentId = documentId;
		this.bm25Score = 0.0;
	}
	
	public long getDocumentId()
	{
		return this.documentId;
	}
	
	public void setBm25Score(double bm25Score)
	{
		this.bm25Score = bm25Score;
	}
	
	public double getBm25Score()
	{
		return this.bm25Score;
	}

	@Override
	public int compare(ResultObject r1, ResultObject r2)
	{
		return (int)Math.ceil(r2.bm25Score-r1.bm25Score);
	}
}
