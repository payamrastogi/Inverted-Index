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

import com.wse.model.ReadObject;
import com.wse.util.ElapsedTime;
import com.wse.util.SequenceGenerator;

public class ReadGzip 
{	
	private Logger logger = LoggerFactory.getLogger(ReadGzip.class);
	private BlockingQueue<ReadObject> readObjectQueue;
	private int size;
	
	public ReadGzip(BlockingQueue<ReadObject> readObjectQueue)
	{
		this.readObjectQueue = readObjectQueue;
	}
	
	public void read(File file)
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
							ReadObject readObject = new ReadObject(Integer.parseInt(fileName)/100, bytes, SequenceGenerator.getNextInSequence(ReadGzip.class));
							readObjectQueue.add(readObject);
						}
					}
				}		
			}
		} 
		catch (IOException e) 
		{
			logger.error("Error", e);
		}
		logger.debug("Total Time: "+ elapsedTime.getTotalTimeInSeconds() + " seconds");
	}
}	


