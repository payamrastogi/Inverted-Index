package com.wse.parse;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Multiset;
import com.wse.util.Pair;
import com.wse.util.SequenceGenerator;

public class ThreadedParser implements Runnable
{
	private BlockingQueue<StringBuffer> contentQueue;
	private BlockingQueue<Pair<Integer, Multiset<String>>> postingQueue;
	private Parser parser;
	
	
	public ThreadedParser(Parser parser, BlockingQueue<StringBuffer> contentQueue, BlockingQueue<Pair<Integer, Multiset<String>>> postingQueue)
	{
		this.parser = parser;
		this.contentQueue = contentQueue;
		this.postingQueue = postingQueue;
	}
	
	public void run()
	{
		for(int i=0;i<5;i++)
		{
			try
			{
				StringBuffer content = null;
				while((content=this.contentQueue.poll(1, TimeUnit.SECONDS))!=null)
				{
					postingQueue.add(new Pair<Integer, Multiset<String>>(SequenceGenerator.getNextInSequence(this.getClass()), parser.parseText(content)));
				}
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}