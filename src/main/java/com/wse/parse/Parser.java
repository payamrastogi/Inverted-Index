package com.wse.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
			String[] s = document.text().split("[\\s@&.?$+-:;]+");
			ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(s));
			System.out.println(arrayList);
		}
		catch(IOException e)
		{
			logger.error("IOException", e.getMessage());
		}
	}

}
