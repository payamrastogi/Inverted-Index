package com.wse;

import java.io.File;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import com.wse.io.DocumentReader;
import com.wse.model.DocumentObject;
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
	private DocumentReader documentReader;
	private Map<Long, DocumentObject> documentObjectMap;
	
	private static final String configPropPath = "src/main/resources/config.properties";
	private static final int resultCount=10;
	public SearchMain()
	{
		this.config = new Config(new File(configPropPath));
		this.fileReader = new FileReader(this.config.getOutputFilePath());
		this.metaObject = this.kryoSerializer.deserialize();
		this.lexicons = this.fileReader.getLexicons();
		this.bm25 = new BM25(this.metaObject.getTotalDocuments(), this.metaObject.getAverageLengthOfDocuments());
		this.documentReader = new DocumentReader(this.config.getOutputFilePath());
		this.documentObjectMap = this.documentReader.getDocumentObjectMap();
	}
	
	public void getSearchResults(String searchQuery)
	{
		String[] searchTerms = searchQuery.split("\\s");
		this.daat = new DocumentAtATime(searchTerms, this.lexicons, resultCount, this.bm25, this.documentObjectMap);
		roQueue = this.daat.getConjunctionResult();
		for(ResultObject ro:roQueue)
		{
			DocumentObject documentObject = this.documentObjectMap.get(ro.getDocumentId());
			System.out.println(documentObject.getDocumentPath() + " : " + ro.getBm25Score());
		}
	}
	
	public static void main()
	{
		SearchMain sm = new SearchMain();
		System.out.println("Search Query: ");
		Scanner scanner = new Scanner(System.in);
		String searchQuery = scanner.nextLine();
		scanner.close();
		sm.getSearchResults(searchQuery);
	}
}
