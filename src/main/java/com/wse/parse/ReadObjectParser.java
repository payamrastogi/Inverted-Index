package com.wse.parse;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.model.ReadObject;
import com.wse.util.ElapsedTime;

import edu.poly.cs912.Parser;

public class ReadObjectParser 
{	
	private final Logger logger = LoggerFactory.getLogger(Parser.class);
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	
	public ReadObjectParser(BlockingQueue<ParsedObject> parsedObjectQueue)
	{
		this.parsedObjectQueue = parsedObjectQueue;
	}
	
	public void parseText(ReadObject readObject)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		StringBuilder sb = new StringBuilder();
		Parser.parseDoc("www.google.com", new String(readObject.getContent()), sb);
		parsedObjectQueue.add(new ParsedObject(readObject.getVolumeId(), readObject.getDocumentId(), sb));
		logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
	}
}
