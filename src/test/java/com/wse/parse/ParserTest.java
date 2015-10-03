package com.wse.parse;

import java.io.File;

import org.junit.Test;

public class ParserTest 
{
	@Test
	public void testParseDoc()
	{
		File inputFile = new File("src/test/resources/test.html");
		Parser parser = new Parser();
		parser.parseDocFromFile(inputFile);
	}
}
