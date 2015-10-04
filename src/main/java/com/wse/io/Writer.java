package com.wse.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;
import com.wse.util.SequenceGenerator;

public class Writer 
{
	private final Logger logger = LoggerFactory.getLogger(Writer.class);
	private AtomicInteger count = new AtomicInteger(0);
	private File outputFile;
	private String filePath;
	
	public Writer()
	{
		this.filePath = "/Users/payamrastogi/Dropbox/workspace/indexer/output";
		this.outputFile = new File(filePath, ""+SequenceGenerator.getNextInSequence(Writer.class));
	}
	
	public void write(String text)
	{
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true)))
		{
			bw.write(text+"\n");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}	
	}
	
}
