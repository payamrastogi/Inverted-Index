package com.wse.parse;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.model.ReadObject;
import com.wse.util.ElapsedTime;

public class ReadObjectParserTest 
{
	@Test
	public void testParseText()
	{
		Logger logger = LoggerFactory.getLogger(ReadObjectParserTest.class);
		ElapsedTime elapsedTime = new ElapsedTime();
		BlockingQueue<ReadObject> readObjectQueue = new ArrayBlockingQueue<>(1000); 
		BlockingQueue<ParsedObject> parsedObjectQueue = new ArrayBlockingQueue<>(10000);
		ReadGzip readGzip = new ReadGzip(readObjectQueue);
		ReadObjectParser readObjectParser = new ReadObjectParser(parsedObjectQueue);
		File file = new File("/Users/payamrastogi/NZ/data/4c/tux-4/polybot/gzipped_sorted_nz/vol_0_99/0_index");
		readGzip.read(file);
		for(ReadObject readObject : readObjectQueue)
		{
			readObjectParser.parseText(readObject);
		}
		//System.out.println(parsedObjectQueue);
		logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
	}
}
