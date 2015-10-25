package com.wse.shell;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import com.wse.util.Config;

public class ExecuteTest
{
	private static final String configPropPath = "src/main/resources/config.properties";
	@Test
	public void testExecuteCommand()
	{
		BlockingQueue<String> pathQueue = new ArrayBlockingQueue<>(200);
		Config config = new Config(new File(configPropPath));
		
		ExecuteCommand ec = new ExecuteCommand(config.getFindCommand(), pathQueue);
		ec.execute();
		System.out.println(pathQueue);
	}
}
