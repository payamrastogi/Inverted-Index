package com.wse.parse;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multiset;
import com.wse.util.ElapsedTime;
import com.wse.util.Pair;

public class Posting 
{
	private BlockingQueue<String> priorityQueue;
	private Set<String> stopWords;
	
	private final Logger logger = LoggerFactory.getLogger(Posting.class);
	
	public Posting(BlockingQueue<String> priorityQueue, Set<String> stopWords)
	{
		this.priorityQueue = priorityQueue;
		this.stopWords = stopWords;
	}
	
	public void create(Pair<Integer, Multiset<String>> pair)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		Multiset<String> set = pair.getRight();
		for(String s: set.elementSet())
		{
			if(!stopWords.contains(s))
			{
				String temp = s+"\t"+pair.getLeft()+"\t"+set.count(s);
				priorityQueue.add(temp);
			}
		}
		logger.debug("Total time: "+ elapsedTime.getTotalTimeInSeconds()+" seconds");
	}
}
