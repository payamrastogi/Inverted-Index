package com.wse.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//to generate unique document id ot any ids according to calling class
public final class SequenceGenerator 
{
	@SuppressWarnings("rawtypes")
	private static final ConcurrentHashMap<Class,AtomicInteger> mapper = new ConcurrentHashMap<Class,AtomicInteger>();
	
	private SequenceGenerator(){}
	
	public static int getNextInSequence(@SuppressWarnings("rawtypes") Class className) 
	{
		 mapper.putIfAbsent(className, new AtomicInteger(1));
	        return mapper.get(className).getAndIncrement();
	}
}
