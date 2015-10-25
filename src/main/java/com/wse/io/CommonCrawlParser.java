package com.wse.io;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonCrawlParser 
{
	private final Logger logger = LoggerFactory.getLogger(CommonCrawlParser.class);
	private BufferedReader br;
	
	public CommonCrawlParser(BufferedReader br)
	{
		this.br = br;
	}
	
	public int readGzip(StringBuilder sb)
	{
		int length=-1;
		try
		{
			while(!br.readLine().equals("WARC/1.0"));
				br.readLine();
			String line;
			for(int i=0;i<9;i++)
			{
				line = br.readLine();
				if(line.startsWith("Content-Length:"))
				{
					try
					{
						length = Integer.parseInt(line.split("\\s+")[1]);
					}
					catch(NumberFormatException e)
					{
						logger.error(e.getMessage(), e);
						length = -1;
					}
				}
			}
			this.readGzipContent(sb);
			if(sb.length()<1)
				length = 0;
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		return length;
	}
	
	private void readGzipContent(StringBuilder sb)
	{
		try
		{
			String line;
			while(!(line=br.readLine()).isEmpty())
			{
				line = line.replaceAll("[^a-zA-Z\\s\\n]", " ");
				String[] s = line.split("\\s+");
				if(s!=null && s.length>0)
				{
					if(sb.length()==0 && !s[0].trim().isEmpty())
						sb.append(s[0].trim().toLowerCase());
					else if(!s[0].trim().isEmpty())
						sb.append("\n").append(s[0].trim().toLowerCase());
					for(int i=1;i<s.length;i++)
						sb.append("\n").append(s[i].trim().toLowerCase());
				}
			}
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}
