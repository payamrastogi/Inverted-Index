package com.wse.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.compress.VByte;
import com.wse.util.ElapsedTime;

// to index sorted files
public class Indexer {
	private final Logger logger = LoggerFactory.getLogger(Indexer.class);
	private BlockingQueue<String> toMergeQueue1;
	private BlockingQueue<String> toMergeQueue2;
	private BlockingQueue<String> lexiconQueue;

	public Indexer(BlockingQueue<String> toMergeQueue1,
			BlockingQueue<String> toMergeQueue2, BlockingQueue<String> lexiconQueue) {
		this.toMergeQueue1 = toMergeQueue1;
		this.toMergeQueue2 = toMergeQueue2;
		this.lexiconQueue = lexiconQueue;
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
							.append("\t");
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
						.append(documentId).append("\t");
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
	
	public void createFinalIndexVByte(String inputFilePath, String outputFilePath)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		int lineCount = 0;
		long prevDocumentId = 0;
		long byteCount = 0;
		long postingListLength = 0;
		long postingListStart = 0;
		long postingListEnd = 0;
		FileInputStream inputStream=null;
		FileOutputStream outputStream=null;
		FileWriter writer=null;
		VByte vByte = new VByte();
		Scanner sc=null;
		try 
		{
			inputStream = new FileInputStream(inputFilePath);
			sc = new Scanner(inputStream, "UTF-8");
			outputStream = new FileOutputStream(outputFilePath);
			writer = new FileWriter(new File(inputFilePath+"_lexicon"));

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
					byte[] b = chunk[0].getBytes();
					outputStream.write(b);
					byteCount+=b.length;
					postingListStart = byteCount;
					prevDocumentId = Long.parseLong(chunk[1]);
					byteCount+=vByte.encode(Long.parseLong(chunk[1]), outputStream);
					byteCount+=vByte.encode(Long.parseLong(chunk[2]), outputStream);
					prevWord = chunk[0];
					postingListLength++;
					sb = new StringBuilder();
					sb.append(chunk[0]);
					continue;
				}
				if(prevWord.equals(chunk[0]))
				{
					//logger.debug(prevWord +" "+Long.parseLong(chunk[1])+" "+ +prevDocumentId +" "+ (Long.parseLong(chunk[1])-prevDocumentId));
					byteCount+=vByte.encode(Long.parseLong(chunk[1])-prevDocumentId, outputStream);
					byteCount+=vByte.encode(Long.parseLong(chunk[2]), outputStream);
					prevWord = chunk[0];
					postingListLength++;
					prevDocumentId = Long.parseLong(chunk[1]);
					continue;
				}
				postingListEnd = byteCount;
				sb.append("\t").append(postingListStart)
				.append("\t").append(postingListEnd)
				.append("\t").append(postingListLength);
				writer.write(sb.toString());
				sb = new StringBuilder();
				
				byte[] b = chunk[0].getBytes();
				outputStream.write(b);
				byteCount+=b.length;
				postingListStart = byteCount;
				byteCount+=vByte.encode(Long.parseLong(chunk[1]), outputStream);
				byteCount+=vByte.encode(Long.parseLong(chunk[2]), outputStream);
				prevDocumentId = Long.parseLong(chunk[1]);
				postingListLength = 1;
				sb.append("\n").append(chunk[0]);
				prevWord = chunk[0];
				if(++lineCount%100000==0)
					logger.debug("Done:"+lineCount+ " Total time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
			}
			postingListEnd = byteCount;
			sb.append("\t").append(postingListStart)
			.append("\t").append(postingListEnd)
			.append("\t").append(postingListLength);
			writer.write(sb.toString());
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
				writer.close();
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
		Indexer indexer = new Indexer(merge, merge, merge);
		indexer.createFinalIndexVByte("/home/jenil/Downloads/indexer/output/m_0", "/home/jenil/Downloads/indexer/output/m_0_final");
	}
}
