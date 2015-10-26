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
	public String toString() {
		return this.documentId+":" + this.bm25Score ;
	}

	@Override
	 public int compare(ResultObject o1, ResultObject o2) {
	 	return (int)((o2.bm25Score-o1.bm25Score)/Math.abs(o2.bm25Score-o1.bm25Score));
	 }
}
