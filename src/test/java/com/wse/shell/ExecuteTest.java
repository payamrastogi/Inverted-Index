package com.wse.shell;

import org.junit.Assert;
import org.junit.Test;

public class ExecuteTest
{
	@Test
	public void testExecuteCommand()
	{
		Execute execute = new Execute();
		String result = execute.executeCommand("ls -lrt"); 
		Assert.assertNotNull(result);
		System.out.println(result);
	}
}
