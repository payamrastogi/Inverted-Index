package com.wse.parse;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser 
{
	private Logger logger = LoggerFactory.getLogger(Parser.class);
	
	public void parseDoc(File inputFile)
	{
		try
		{
			Document document = Jsoup.parse(inputFile, "UTF-8");
			System.out.println(document.text());
		}
		catch(IOException e)
		{
			logger.error("IOException", e.getMessage());
		}
	}

}
