package com.wse.shell;

import org.junit.Assert;
import org.junit.Test;

public class ExecuteTest
{
	@Test
	public void testExecuteCommand()
	{
		Exceute execute = new Exceute();
		String result = execute.executeCommand("ls -lrt"); 
		Assert.assertNotNull(result);
		System.out.println(result);
	}
}
