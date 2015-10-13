package com.wse.parse;

import java.io.File;

import org.junit.Test;

public class ReadGzipTest 
{
	@Test
	public void testRead() throws InterruptedException
	{
		//BlockingQueue<ReadObject> readObjectQueue = new ArrayBlockingQueue<>(1000); 
		//ReadGzip readGzip = new ReadGzip(readObjectQueue);
		File file = new File("/Users/payamrastogi/NZ/data/4c/tux-4/polybot/gzipped_sorted_nz/vol_0_99/0_index");
		//readGzip.read(file);
		//System.out.println(readObjectQueue);
	}
}
