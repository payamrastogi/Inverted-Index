package com.wse.parse;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser 
{
	private Logger logger = LoggerFactory.getLogger(Parser.class);
	
	public void parseDocFromFile(File inputFile)
	{
		try
		{
			Document document = Jsoup.parse(inputFile, "UTF-8");
			BreakIterator boundary = BreakIterator.getWordInstance();
	        boundary.setText(document.text());
	        List<String> words = getWords(boundary, document.text());
	        Map<String ,WordIndex> postingMap = generatePostingList(words,IDGenerator.getDocumentID());
	        System.out.println(postingMap);
		}
		catch(IOException e)
		{
			logger.error("IOException", e.getMessage());
		}
	}
	
	public Map<String, WordIndex> parseDocFromText(String text)
	{
		Document document = Jsoup.parse(text, "UTF-8");
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(document.text());
		List<String> words = getWords(boundary, document.text());
		Map<String ,WordIndex> postingMap = generatePostingList(words,IDGenerator.getDocumentID());
		logger.debug(postingMap.toString());
		return postingMap;
	}
	
	public List<String> getWords(BreakIterator boundary, String source)
	{
		List<String> words = new ArrayList<String>();
	    int start = boundary.first();
	    for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) 
	    {
	    	//logger.debug(source.substring(start,end));
	        words.add(source.substring(start,end));
	    }
	    return words;
	 }
	
	public Map<String, WordIndex> generatePostingList(List<String> words, String docId)
	{
		Map<String, WordIndex> postingMap = new HashMap<String, WordIndex>();
		for (String word : words)
		{
			if (word == null || word.trim().isEmpty())
				continue;
			if (!postingMap.containsKey(word)) 
			{
				postingMap.put(word, new WordIndex(word, docId, Collections.frequency(words, word)));
			}
		}
		return postingMap;
	}
}
