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

import com.wse.io.CommonCrawlParser;
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
	
	public ReadGzip(BlockingQueue<ParsedObject> parsedObjectQueue, BlockingQueue<String> documentQueue)
	{
		this.parsedObjectQueue = parsedObjectQueue;
		this.documentQueue = documentQueue;
	}
	
	public void readNZ(File file) throws InterruptedException
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
							this.documentQueue.add(documentId + "\t"+ dataFile.getAbsolutePath() + "\t"+ this.size+"\n");
							if(++this.totalDocuments%10000==0)
								logger.debug("Done: "+ totalDocuments+" Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
						}
					}
				}		
			}
			
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
	
	public long getTotalDocuments() {
		return totalDocuments;
	}

	public double getAverageLengthOfDocuments() {
		return this.averageLengthOfDocuments/this.totalDocuments; 
	}
	
	public void readCC(File file) throws InterruptedException
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		try(FileInputStream fis = new FileInputStream(file)) 
		{
			GZIPInputStream gzis = new GZIPInputStream(fis);
			InputStreamReader isr = new InputStreamReader(gzis);
			try(BufferedReader br = new BufferedReader(isr))
			{
				int volumeId = SequenceGenerator.getNextInSequence(CommonCrawlParser.class);
				for(int i=0;i<17;i++)
				{
					br.readLine();
				}
				while(br.readLine()!=null)
				{
					int length = 0;
					CommonCrawlParser ccp = new CommonCrawlParser(br);
					StringBuilder sb = new StringBuilder();
					StringBuilder url = new StringBuilder();
					try
					{
						length = ccp.readGzip(sb, url);
						this.averageLengthOfDocuments+=length;
					}
					catch(NullPointerException e)
					{
						logger.error("End of Document Reached");
						break;
					}
					int documentId = SequenceGenerator.getNextInSequence(ReadGzip.class);
					//this.parsedObjectQueue.add(new ParsedObject(volumeId, documentId, sb));
					this.documentQueue.add(documentId + "\t"+ url.toString() + "\t"+ length+"\n");
					if(++this.totalDocuments%10000==0)
						logger.debug("Done: "+ totalDocuments+" Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
				}
			}
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}	


