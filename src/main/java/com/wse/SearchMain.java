package com.wse;

import java.io.File;
import java.util.Queue;
import java.util.Scanner;

import com.wse.model.Lexicon;
import com.wse.model.MetaObject;
import com.wse.model.ResultObject;
import com.wse.queryprocessing.DocumentAtATime;
import com.wse.ranking.BM25;
import com.wse.serialize.KryoSerializer;
import com.wse.util.Config;
import com.wse.util.FileReader;

public class SearchMain 
{
	private Config config;
	private DocumentAtATime daat;
	private BM25 bm25;
	private FileReader fileReader;
	private Lexicon[] lexicons;
	private KryoSerializer kryoSerializer;
	private MetaObject metaObject;
	private Queue<ResultObject> roQueue;
	
	private static final String configPropPath = "src/main/resources/config.properties";
	private static final int resultCount=10;
	public SearchMain()
	{
		this.config = new Config(new File(configPropPath));
		this.fileReader = new FileReader(this.config.getOutputFilePath());
		this.metaObject = this.kryoSerializer.deserialize();
		this.lexicons = this.fileReader.getLexicons();
		this.bm25 = new BM25(this.metaObject.getTotalDocuments(), this.metaObject.getAverageLengthOfDocuments());
		
	}
	
	public void getSearchResults(String searchQuery)
	{
		String[] searchTerms = searchQuery.split("\\s");
		this.daat = new DocumentAtATime(searchTerms, this.lexicons, resultCount, this.bm25);
		roQueue = this.daat.getConjunctionResult();
		
	}
	
	public static void main()
	{
		SearchMain sm = new SearchMain();
		System.out.println("Search Query: ");
		Scanner scanner = new Scanner(System.in);
		String searchQuery = scanner.nextLine();
		sm.getSearchResults(searchQuery);
		
	}
}
