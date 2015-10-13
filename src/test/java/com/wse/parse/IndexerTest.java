package com.wse.parse;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class IndexerTest 
{
	@Test
	public void indexTest() throws FileNotFoundException, IOException
	{
		String fileInputPath = "/Users/payamrastogi/Dropbox/workspace/indexer/output/e20_sorted";
		String outputFilePath = "/Users/payamrastogi/Dropbox/workspace/indexer/output/e20_test";
		Indexer indexer = new Indexer(fileInputPath, outputFilePath);
		indexer.index();
	}
}
