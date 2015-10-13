package com.wse.parse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

//create Final index from the merged file
public class Indexer 
{
	//input file (merged) to create final index
	private FileInputStream inputStream;
	//final output file
	private FileOutputStream outputStream;
	//private FileWriter outputStream;
	private Scanner sc;
	private String prevWord;
	private int prevDocumentId = -1;
	private int count = 1;
	private long lineCount;
	private final Logger logger = LoggerFactory.getLogger(Indexer.class);
	
	public Indexer(String inputFilePath, String outputFilePath) throws FileNotFoundException, IOException
	{
		this.inputStream = new FileInputStream(inputFilePath);
		this.sc = new Scanner(inputStream, "UTF-8");
		this.outputStream = new FileOutputStream(outputFilePath);
	}
	//to create final index
	public void index()
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		StringBuilder sb = null;
		 while (sc.hasNextLine())
		 {
			 lineCount++;
		       String[] chunk = sc.nextLine().split("\t");
		       String word = chunk[0];
		       int documentId = Integer.parseInt(chunk[1]);
		      // System.out.println("prevWord: "+prevWord+" word: "+word);
	    	  // System.out.println("prevDocumentId: "+prevDocumentId+" documentId: "+documentId);
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
		       }
		       if(lineCount%100000==0)
		    	   logger.debug("line count: " + lineCount + " in "+elapsedTime.getTotalTimeInSeconds()+ "seconds");
		 }
		 try
		 {
			 sb.append(prevDocumentId+":"+count);
			 //System.out.println("last: "+sb.toString());
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
			 } 
			 catch (IOException e)
			 {
				 logger.error(e.getMessage(), e);
			 }
		 }
	}

}
