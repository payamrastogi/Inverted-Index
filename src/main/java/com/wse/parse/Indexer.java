package com.wse.parse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;
// to index sorted files
public class Indexer 
{
	private FileInputStream inputStream;
	private FileOutputStream outputStream;
	private Scanner sc;
	private final Logger logger = LoggerFactory.getLogger(Indexer.class);
	private BlockingQueue<String> toMergeQueue1;
	private BlockingQueue<String> toMergeQueue2;
	
	public Indexer(BlockingQueue<String> toMergeQueue1,BlockingQueue<String> toMergeQueue2)
	{
		this.toMergeQueue1 = toMergeQueue1;
		this.toMergeQueue2 = toMergeQueue2;
	}
	
	public void index(String inputFilePath, String outputFilePath) throws FileNotFoundException, IOException
	{
		this.inputStream = new FileInputStream(inputFilePath);
		this.sc = new Scanner(inputStream, "UTF-8");
		this.outputStream = new FileOutputStream(outputFilePath);
		ElapsedTime elapsedTime = new ElapsedTime();
		StringBuilder sb = null;
		String prevWord = null;
		int lineCount = 0;
		int prevDocumentId = -1;
		int count = 0;
		 while (sc.hasNextLine())
		 {
			 lineCount++;
		       String[] chunk = sc.nextLine().split("\t");
		       String word = chunk[0];
		       int documentId = Integer.parseInt(chunk[1]);
		       if(word==null || word.trim().isEmpty())
		    	   continue;
		       if(prevWord==null)
		       {
		    	   sb = new StringBuilder();
		    	   prevWord = word;
		    	   prevDocumentId = documentId;
		    	   sb.append(word+"\t");
		    	   count = 0;
		       }
		       if(prevWord.equalsIgnoreCase(word))
		       {
		    	   if(prevDocumentId == documentId)
		    	   {
		    		   count++;
		    	   }
		    	   else
		    	   {
		    		  sb.append(prevDocumentId+":"+count+" ");
		    		  try 
		    		  {
		    			  outputStream.write(sb.append("\n").toString().getBytes());
		    		  } 
		    		  catch (IOException e) 
		    		  {
						logger.error(e.getMessage(), e);
		    		  }
		    		  sb = new StringBuilder(word+"\t");
		    		  prevDocumentId = documentId;
		    		  count=1;
		    	   }
		       }
		       else
		       {
		    	   try
		    	   {
		    		   sb.append(prevDocumentId+":"+count);
		    		   outputStream.write(sb.append("\n").toString().getBytes());
		    		   sb = new StringBuilder();
		    		   prevWord = word;
		    		   sb.append(word+"\t");
		    		   prevDocumentId = documentId;
		    		   count = 1;
		    	   }
		    	   catch(IOException e)
		    	   {
		    		   logger.error(e.getMessage(), e);
		    	   }
		    	   catch(Exception e)
		    	   {
		  			 	logger.error("inputFilePath:" + inputFilePath + "word: "+word+" prev: "+prevWord+e.getMessage(), e);
		  		   }
		       }
		       if(lineCount%100000==0)
		    	   logger.debug("line count: " + lineCount + " in "+elapsedTime.getTotalTimeInSeconds()+" seconds");
		 }
		 try
		 {
			 sb.append(prevDocumentId+":"+count);
			 outputStream.write(sb.toString().getBytes());
		 }
		 catch(IOException e)
		 {
			 logger.error(e.getMessage(), e);
		 }
		 finally
		 {
			 try 
			 {
				 inputStream.close();
				 outputStream.close();
				 if (toMergeQueue2.size() <= toMergeQueue1.size()) 
				 {
					 toMergeQueue2.add(outputFilePath);
				 } 
				 else 
				 {
					 toMergeQueue1.add(outputFilePath);
				 }
				 logger.debug("Queue size : "+toMergeQueue1.size() + "--" + toMergeQueue2.size());
				 new ProcessBuilder("/bin/bash", "-c","rm "+ inputFilePath).start();
			 } 
			 catch (IOException e)
			 {
				 logger.error(e.getMessage(), e);
			 }
		 }
	}
}