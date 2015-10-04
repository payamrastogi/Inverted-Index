package com.wse.parse;

import java.io.File;

import org.junit.Test;

public class ReadGzipTest 
{
	@Test
	public void testRead()
	{
		ReadGzip readGzip = new ReadGzip();
		File file = new File("/Users/payamrastogi/NZ/data/4c/tux-4/polybot/gzipped_sorted_nz/vol_0_99/0_data");
		readGzip.read(file);
	}
}
