package com.wse.shell;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ExecuteTest
{
	@Test
	public void testExecuteCommand()
	{
		Execute execute = new Execute();
		List<String> result = execute.executeCommand("find /Users/payamrastogi/NZ/data -regex .*/*_data -print"); 
		Assert.assertNotNull(result);
		System.out.println(result);
	}
}
