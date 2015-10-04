package com.wse.parse;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Multiset;
import com.wse.util.Pair;

public class ThreadedPosting implements Runnable
{
	private Posting posting;
	private BlockingQueue<Pair<Integer, Multiset<String>>> postingQueue;
	
	public ThreadedPosting(Posting posting, BlockingQueue<Pair<Integer, Multiset<String>>> postingQueue)
	{
		this.posting = posting;
		this.postingQueue = postingQueue;
	}
	
	public void run()
	{
		for(int i=0;i<5;i++)
		{
			try
			{
				Pair<Integer, Multiset<String>> pair = null;
				while((pair = postingQueue.poll(10, TimeUnit.SECONDS))!=null)
				{
					posting.create(pair);
				}
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
