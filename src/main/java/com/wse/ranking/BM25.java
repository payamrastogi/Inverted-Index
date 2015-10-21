package com.wse.ranking;



/* 
 * http://xapian.org/docs/bm25.html	
 * 
 * N: total number of documents in the collection;
• ft : number of documents that contain term t;
• fd,t: frequency of term t in document d;
• |d|: length of document d;
• |d|avg: the average length of documents in the collection;
• k1 and b: constants, usually k1 = 1.2 and b = 0.75 
 */
public class BM25 
{
	private final static double k1 = 1.2;
	private final static double b = 0.75;
	
	private long N;
	private double dAvg; //avg length of documents in the collection;
	
	public BM25(long N, double dAvg)
	{
		this.N = N;
		this.dAvg = dAvg;
	}
	
	public double getScore(int ft, long fdt, long d)
	{
		double score = 0.0;
		double K = k1 * (1-b) + b * (d/this.dAvg);
		
		double X = (N - ft + 0.5)/ft+0.5;
		double Y = ((k1 + 1) * fdt) / (K + fdt);
		score = Math.log(X * Y);
		return score;
	}
}
