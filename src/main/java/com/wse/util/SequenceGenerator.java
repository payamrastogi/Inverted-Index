package com.wse.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class SequenceGenerator 
{
	private static final AtomicInteger sequence= new AtomicInteger(1);
	private SequenceGenerator(){}
	
	public static int getNextInSequence() 
	{
		return sequence.getAndIncrement();
	}
}
