package com.wse.parse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.io.Writer;
import com.wse.model.ParsedObject;
import com.wse.model.VolumeIndexedObject;

public class VolumeIndexer 
{
	private Set<String> stopWords;
	
	private final Logger logger = LoggerFactory.getLogger(VolumeIndexer.class);
	private BlockingQueue<VolumeIndexedObject> volumeIndexedObjectQueue;
	private Map<String,Map<Integer,Integer>> indexMap;
	private int count;
	
	public VolumeIndexer(Set<String> stopWords,BlockingQueue<VolumeIndexedObject> volumeIndexedObjectQueue)
	{
		this.volumeIndexedObjectQueue = volumeIndexedObjectQueue;
		this.stopWords = stopWords;
		indexMap = new TreeMap<>();
	}
	
	public void volumeIndex(ParsedObject parsedObject)
	{

		String[] wordContents = parsedObject.getParsedContent().toString().split("\\n");
		int documentId = parsedObject.getDocumentId();
		int volumeId = parsedObject.getVolumeId();
		for (String wordContent : wordContents) 
		{
			String word = wordContent.split(" ")[0];
			if (word== null || word.trim().isEmpty() || word.matches(".*\\d+.*") || word.matches("((\\w)\\2\\2)+") ||stopWords.contains(word))
				continue;
			Map<Integer,Integer> docMap = null;
			int wordCount = 0;
			if (!indexMap.containsKey(word)) 
			{
				docMap = new TreeMap<Integer,Integer>();
			}
			else
			{
				docMap = indexMap.get(word);
				if (indexMap.get(word).containsKey(documentId))
				{	
					wordCount = indexMap.get(word).get(documentId);
				}
			}
			docMap.put(documentId, wordCount+1);
			indexMap.put(word,docMap);
		}
		if(++count%1000==0)
		{
			StringBuilder volumeIndexContent = new StringBuilder();
			for (String word: indexMap.keySet())
			{
				Map<Integer,Integer> docMap = indexMap.get(word);
				volumeIndexContent.append(word+"\t");
				for (Integer docId : docMap.keySet())
				{
					Integer wordFrequency = docMap.get(docId);
					volumeIndexContent.append(docId + ":" + wordFrequency + " ");
				}
				volumeIndexContent.append("\n");
			}
			volumeIndexedObjectQueue.add(new VolumeIndexedObject(volumeId,volumeIndexContent));
			indexMap = new TreeMap<>();
			logger.debug("Done Volume: "+count);
		}
	}
	
	public void documentIndex(ParsedObject parsedObject) 
	{
		if (parsedObject == null || parsedObject.getParsedContent() == null)
			return;
		String[] wordContents = parsedObject.getParsedContent().toString().split("\\n");
		Map<String,Integer> docMap = new TreeMap<String,Integer>();
		for (String wordContent : wordContents)
		{
			String word = wordContent.split(" ")[0];
			if (stopWords !=null && stopWords.contains(word))
				continue;
			int wordCount = 0;
			if (docMap.containsKey(word)) 
			{
				wordCount = docMap.get(word);
			}
			docMap.put(word, wordCount+1);
		}
		StringBuilder volumeIndexContent = new StringBuilder();
		for (String word: docMap.keySet())
		{
				volumeIndexContent.append(word);
				volumeIndexContent.append("\t");
				volumeIndexContent.append(parsedObject.getDocumentId());
				volumeIndexContent.append(":");
				volumeIndexContent.append(docMap.get(word));
				volumeIndexContent.append("\n");
		}
		logger.debug("Done Doc : "+volumeIndexContent);
	}

}
