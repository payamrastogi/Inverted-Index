package com.wse.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.wse.util.ElapsedTime;

public class Parser 
{	
	private final Logger logger = LoggerFactory.getLogger(Parser.class);
	public Multiset<String> parseText(StringBuffer content)
	{
		ElapsedTime elapsedTime = new ElapsedTime();
		Document document = Jsoup.parse(content.toString(), "UTF-8");
		Multiset<String> set =  HashMultiset.create(Splitter.on(CharMatcher.WHITESPACE)
			        						.trimResults(CharMatcher.is('.'))
			        						.omitEmptyStrings()
			        						.split(document.text()));
		logger.debug("Total Time: "+elapsedTime.getTotalTimeInSeconds()+" seconds");
		return set;
	}
}
