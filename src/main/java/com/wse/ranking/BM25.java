package com.wse.ranking;



/* 
 * http://xapian.org/docs/bm25.html	
 * 
 * k1, k3 are constants,
	q is the wqf, the within query frequency,
	f is the wdf, the within document frequency,
	n is the number of documents in the collection indexed by this term,
	N is the total number of documents in the collection,
	r is the number of relevant documents indexed by this term,
	R is the total number of relevant documents,
	L is the normalised document length (i.e. the length of this document divided by the average length 
	of documents in the collection).
	
	k1=1, k2=0, k3=1, and b=0.5.
 */
public class BM25 
{
	private final static int k1 = 1;
	private final static int k2 = 0;
	private final static int k3 = 1;
	private final static double b = 0.5;
	
	private int q;
	private int N;
	private int R;
	private int a; //avg length of documents in the collection;
	
	private double X;
	
	public BM25(int q, int N, int a, int R)
	{
		this.q = q;
		this.N = N;
		this.a = a;
		this.R = R;
		this.X = ((k3 + 1) * this.q)/(k3+q);
	}
	
	public double getScore(int f, int n, int r, int l)
	{
		double L = l/this.a;
		double K = k1 * (b * L  + (1-b));
		
		double Y = ((k1+1)*f)/(K + f);
		
		double z = ((r+0.5) * (this.N - n - this.R + r + 0.5))/((n-r+0.5)*(this.R-r+0.5));
		double Z = Math.log(z);
		
		return X * Y * Z;
	}

}
