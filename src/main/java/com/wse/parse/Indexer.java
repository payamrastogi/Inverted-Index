package com.wse.parse;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.util.ElapsedTime;

// to index sorted files
public class Indexer {
	private final Logger logger = LoggerFactory.getLogger(Indexer.class);
	private BlockingQueue<String> toMergeQueue1;
	private BlockingQueue<String> toMergeQueue2;

	public Indexer(BlockingQueue<String> toMergeQueue1,
			BlockingQueue<String> toMergeQueue2) {
		this.toMergeQueue1 = toMergeQueue1;
		this.toMergeQueue2 = toMergeQueue2;
	}

	public void index(String inputFilePath, String outputFilePath) {
		ElapsedTime elapsedTime = new ElapsedTime();
		int lineCount = 0;
		FileInputStream inputStream=null;
		FileOutputStream outputStream=null;;
		Scanner sc=null;
		try 
		{
			inputStream = new FileInputStream(inputFilePath);
			sc = new Scanner(inputStream, "UTF-8");
			outputStream = new FileOutputStream(outputFilePath);

			StringBuilder sb = null;
			String prevWord = null;
			String prevDocumentId = null;
			int count = 0;
			while (sc.hasNextLine()) 
			{
				lineCount++;
				String line = sc.nextLine();
				if(line==null || line.isEmpty())
					continue;
				String[] chunk = line.split("\t");
				if (chunk == null || chunk.length == 0 || chunk.length < 2)
				{
					//System.out.println(1);
					continue;
				}
				if (chunk[0] == null || chunk[0].trim().isEmpty() || chunk[0].length()>45)
				{
					//System.out.println(2);
					continue;
				}
				String word = chunk[0];
				String documentId = chunk[1];

				if (prevWord == null) 
				{
					sb = new StringBuilder();
					prevWord = word;
					prevDocumentId = documentId;
					sb.append(word).append("\t").append(documentId)
							.append(":");
					count = 0;
				}
				if (prevWord.equalsIgnoreCase(word)) 
				{
					if (prevDocumentId.equals(documentId))
					{
						//System.out.println(word+" : "+documentId);
						count++;
						continue;
					}
				} 
				sb.append(count).append("\n");
				outputStream.write(sb.toString().getBytes());
				sb = new StringBuilder();
				prevWord = word;
				sb = new StringBuilder(word).append("\t")
						.append(documentId).append(":");
				prevDocumentId = documentId;
				count = 1;

				if (lineCount % 200000 == 0)
					logger.debug("line count: " + lineCount + " in "
							+ elapsedTime.getTotalTimeInSeconds() + " seconds");
			}
			sb.append(count);
			outputStream.write(sb.toString().getBytes());
			

		} 
		catch (IOException e) 
		{
			logger.error(
					inputFilePath + " : " + lineCount + " : " + e.getMessage(),
					e);
		} 
		catch (Exception e) 
		{
			logger.error(
					inputFilePath + " : " + lineCount + " : " + e.getMessage(),
					e);
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
				logger.debug("Queue size : "+toMergeQueue1.size() + "--" +toMergeQueue2.size());
				new ProcessBuilder("/bin/bash", "-c","rm "+ inputFilePath).start();
			} 
			catch (Exception e) 
			{
				logger.error(
						inputFilePath + ":" + lineCount + ":" + e.getMessage(),
						e);
			}
		}
	}
	
	public void createFinalIndex(String inputFilePath, String outputFilePath)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		int lineCount = 0;
		FileInputStream inputStream=null;
		FileOutputStream outputStream=null;;
		Scanner sc=null;
		try 
		{
			inputStream = new FileInputStream(inputFilePath);
			sc = new Scanner(inputStream, "UTF-8");
			outputStream = new FileOutputStream(outputFilePath);

			StringBuilder sb = null;
			String prevWord = null;
			while (sc.hasNextLine()) 
			{
				String line = sc.nextLine();
				if(line==null || line.isEmpty())
					continue;
				String[] chunk = line.split("\t");
				if (chunk == null || chunk.length == 0 || chunk.length < 2)
				{
					continue;
				}
				if (chunk[0] == null || chunk[0].trim().isEmpty() || chunk[0].length()>45)
				{
					continue;
				}
				if(prevWord==null)
				{
					sb = new StringBuilder();
					sb.append(line);
					prevWord = chunk[0];
					continue;
				}
				if(prevWord.equals(chunk[0]))
				{
					sb.append(" ").append(chunk[1]);
					continue;
				}
				sb.append("\n");
				outputStream.write(sb.toString().getBytes());
				sb = new StringBuilder();
				sb.append(line);
				prevWord = chunk[0];
				if(++lineCount%100000==0)
					logger.debug("Done:"+lineCount+ " Total time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
			}
			sb.append("\n");
			outputStream.write(sb.toString().getBytes());
		}
		catch (IOException e) 
		{
			logger.error(
					inputFilePath + " : " + lineCount + " : " + e.getMessage(),
					e);
		} 
		catch (Exception e) 
		{
			logger.error(
					inputFilePath + " : " + lineCount + " : " + e.getMessage(),
					e);
		} 
		finally
		{
			try 
			{
				inputStream.close();
				outputStream.close();
				//new ProcessBuilder("/bin/bash", "-c","rm "+ inputFilePath).start();
				logger.debug("Total time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
			} 
			catch (Exception e) 
			{
				logger.error(
						inputFilePath + ":" + lineCount + ":" + e.getMessage(),
						e);
			}
		}
	}
	
	public static void main(String args[])
	{
		BlockingQueue<String> merge  = new ArrayBlockingQueue<>(1);
		Indexer indexer = new Indexer(merge, merge);
		indexer.createFinalIndex("/Users/payamrastogi/Dropbox/workspace/indexer/output/m_102", "/Users/payamrastogi/Dropbox/workspace/indexer/output/m_102_final");
	}
}
