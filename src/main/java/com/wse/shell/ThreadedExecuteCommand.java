package com.wse.shell;

public class ThreadedExecuteCommand implements Runnable
{
	private ExecuteCommand executeCommand;
	
	public ThreadedExecuteCommand(ExecuteCommand executeCommand)
	{
		this.executeCommand = executeCommand;
	}
	
	public void run()
	{
		executeCommand.execute();
	}
}
