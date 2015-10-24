package com.wse.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonCrawlParserTest 
{
	private Logger logger = LoggerFactory.getLogger(CommonCrawlParserTest.class);
	@Test
	public void readGzipTest()
	{
		try(FileInputStream fis = new FileInputStream("/Users/payamrastogi/CommonCrawl/CommonCrawl/CC-MAIN-20150728002301-00003-ip-10-236-191-2.ec2.internal.warc.wet.gz")) 
		{
			GZIPInputStream gzis = new GZIPInputStream(fis);
			InputStreamReader isr = new InputStreamReader(gzis);
			try(BufferedReader br = new BufferedReader(isr))
			{
				int length;
				for(int i=0;i<17;i++)
				{
					br.readLine();
				}
				first: while(br.readLine()!=null)
				{
					CommonCrawlParser ccp = new CommonCrawlParser(br);
					StringBuilder sb = new StringBuilder();
					try
					{
						length = ccp.readGzip(sb);
					}
					catch(NullPointerException e)
					{
						logger.error("End of Document Reached");
						break;
					}
					if(length==-1)
					{
						break;
					}
						
					System.out.println("-------- "+ length);
					System.out.println(sb.toString());
					System.out.println("=========");
				}
				//while((line=br.readLine())!=null)
				//	System.out.println(line);
			}
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}
