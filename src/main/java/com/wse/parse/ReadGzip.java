package com.wse.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.model.ParsedObject;
import com.wse.model.ReadObject;
import com.wse.util.ElapsedTime;
import com.wse.util.SequenceGenerator;

import edu.poly.cs912.Parser;

public class ReadGzip 
{	
	private Logger logger = LoggerFactory.getLogger(ReadGzip.class);
	private BlockingQueue<ParsedObject> parsedObjectQueue;
	private int size;
	private int count=0;
	
	public ReadGzip(BlockingQueue<ParsedObject> parsedObjectQueue)
	{
		this.parsedObjectQueue = parsedObjectQueue;
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
							byte[] bytes = new byte[this.size];
							gzisData.read(bytes);
							int volumeId = Integer.parseInt(fileName)/100;
							int documentId = SequenceGenerator.getNextInSequence(ReadGzip.class);
							StringBuilder sb = new StringBuilder();
							//System.out.println(new String(bytes));
							//System.out.println("-------------------------");
							try
							{
								Parser.parseDoc("www.google.com", new String(bytes), sb);
							}
							catch(Exception e)
							{
								logger.error(e.getMessage(), e);
							}
							//ReadObject readObject = new ReadObject(volumeId, bytes, documentId);
							ParsedObject parsedObject = new ParsedObject(volumeId, documentId, sb);
							//parsedObjectQueue.add(parsedObject);
							if(++count%10000==0)
								logger.debug(count+" Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
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
		}
	}
}	


