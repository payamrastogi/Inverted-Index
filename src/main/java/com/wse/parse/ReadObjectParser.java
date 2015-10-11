package com.wse.parse;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.model.ReadObject;
import com.wse.util.ElapsedTime;

import edu.poly.cs912.Parser;

public class ReadObjectParser 
{	
	//private final Logger logger = LoggerFactory.getLogger(ReadObjectParser.class);
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	
	public ReadObjectParser(BlockingQueue<ParsedObject> parsedObjectQueue)
	{
		this.parsedObjectQueue = parsedObjectQueue;
	}
	
	public void parseText(ReadObject readObject) throws InterruptedException, IOException
	{
		int volumeId = readObject.getVolumeId();
		int documentId = readObject.getDocumentId();
		byte[] bytes = readObject.getContent();
		StringBuilder sb = new StringBuilder();
		Parser.parseDoc("www.google.com", new String(bytes), sb);
		parsedObjectQueue.add(new ParsedObject(volumeId, documentId, sb));
		readObject = null;
		//logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
	}
}
