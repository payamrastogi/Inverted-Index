package com.wse.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.util.ElapsedTime;
import com.wse.util.SequenceGenerator;

import edu.poly.cs912.Parser;

//to read and parse gzip file
public class ReadGzip 
{	
	private Logger logger = LoggerFactory.getLogger(ReadGzip.class);
	//parsed data to be added in this queue
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	private BlockingQueue<String> documentQueue;
	private int size;
	
	private long totalDocuments;
	private double averageLengthOfDocuments;
	
	public ReadGzip(BlockingQueue<ParsedObject> parsedObjectQueue, BlockingQueue<String> documentQueue, long totalDocuments, double averageLengthOfDocuments)
	{
		this.parsedObjectQueue = parsedObjectQueue;
		this.documentQueue = documentQueue;
		this.totalDocuments = totalDocuments;
		this.averageLengthOfDocuments = averageLengthOfDocuments;
	}
	
	public void read(File file) throws InterruptedException
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		try(FileInputStream fis = new FileInputStream(file)) 
		{
			String parent = file.getParent();
			String fileName = file.getName().split("_")[0];
			String dataFileName = fileName +"_data";
			File dataFile = new File(parent, dataFileName);
			GZIPInputStream gzis = new GZIPInputStream(fis);
			InputStreamReader isr = new InputStreamReader(gzis);
			try(BufferedReader br = new BufferedReader(isr))
			{
				String line;
				try(FileInputStream fisData = new FileInputStream(dataFile))
				{
					try(GZIPInputStream gzisData = new GZIPInputStream(fisData, (int)dataFile.length()))
					{
						while((line=br.readLine())!=null)
						{
							this.size = Integer.parseInt(line.split("\\s")[3]);
							this.averageLengthOfDocuments += size;
							byte[] bytes = new byte[this.size];
							gzisData.read(bytes);
							int volumeId = Integer.parseInt(fileName)/100;
							int documentId = SequenceGenerator.getNextInSequence(ReadGzip.class);
							StringBuilder sb = new StringBuilder();
							try
							{
								Parser.parseDoc("www.google.com", new String(bytes), sb);
							}
							catch(Exception e)
							{
								logger.error(e.getMessage(), e);
								continue;
							}
							this.parsedObjectQueue.add(new ParsedObject(volumeId, documentId, sb));
							this.documentQueue.add(documentId + "\t"+ dataFile.getAbsolutePath() + "\t"+ this.size);
							if(++this.totalDocuments%10000==0)
								logger.debug("Done: "+ totalDocuments+" Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
						}
					}
				}		
			}
			this.averageLengthOfDocuments = this.averageLengthOfDocuments/this.totalDocuments; 
		} 
		catch (IOException e) 
		{
			logger.error(e.getMessage(), e);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
}	


