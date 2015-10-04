package com.wse.parse;

import org.junit.Test;

import com.wse.util.SequenceGenerator;

public class SequenceGeneratorTest 
{
	@Test
	public void testGetDocumentID()
	{
		System.out.println(SequenceGenerator.getNextInSequence());
		System.out.println(SequenceGenerator.getNextInSequence());
	}
}
