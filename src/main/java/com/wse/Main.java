package com.wse;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.parse.Parser;
import com.wse.parse.ReadGZIP;
import com.wse.parse.WordIndex;
import com.wse.shell.Execute;
import com.wse.util.ElapsedTime;

public class Main
{
	private Logger logger = LoggerFactory.getLogger(Main.class);
	public static void main(String args[])
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		Main main = new Main();
		main.execute();
		main.logger.debug("Total Time: "+ elapsedTime.getTotalTimeInSeconds() +" seconds");
	}
	
	public void execute()
	{
		String getFilePaths = "find /Users/payamrastogi/NZ/data -regex .*/*_data -print";
		Execute execute = new Execute();
		List<String> pathList = execute.executeCommand(getFilePaths);
		ReadGZIP readGzip = new ReadGZIP();
		Parser parser = new Parser();
		for(String filePath : pathList)
		{
			StringBuffer sBuffer = readGzip.read(new File(filePath));
			Map<String , WordIndex> map = parser.parseDocFromText(sBuffer.toString());
		}
	}
}
