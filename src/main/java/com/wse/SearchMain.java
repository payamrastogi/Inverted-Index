package com.wse;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import com.wse.io.DocumentReader;
import com.wse.io.LexiconReader;
import com.wse.model.DocumentObject;
import com.wse.model.LexiconObject;
import com.wse.model.MetaObject;
import com.wse.model.ResultObject;
import com.wse.queryprocessing.DocumentAtATime;
import com.wse.ranking.BM25;
import com.wse.serialize.KryoSerializer;
import com.wse.util.Config;
import com.wse.util.ElapsedTime;

public class SearchMain 
{
	private Config config;
	private DocumentAtATime daat;
	private BM25 bm25;
	private KryoSerializer kryoSerializer;
	private MetaObject metaObject;
	private Queue<ResultObject> roQueue;
	private DocumentReader documentReader;
	private Map<Long, DocumentObject> documentObjectMap;
	private Map<String, LexiconObject> lexiconObjectMap;
	private LexiconReader lexiconReader;
	
	private static final String configPropPath = "src/main/resources/config.properties";

	public SearchMain()
	{
		this.config = new Config(new File(configPropPath));
		//this.lexiconObjectMap = new Hashtable<>(14535908);
		//this.lexiconReader = new LexiconReader(this.config.getOutputFilePath(), lexiconObjectMap);
		this.kryoSerializer = new KryoSerializer();
		ElapsedTime time = new ElapsedTime();
		this.lexiconObjectMap = this.kryoSerializer.deserializeLexiconMap();
		this.documentObjectMap = this.kryoSerializer.deserializeDocumentMap();
		this.metaObject = this.kryoSerializer.deserialize();
		
		this.bm25 = new BM25(this.metaObject.getTotalDocuments(), this.metaObject.getAverageLengthOfDocuments());
		System.out.println(time.getTotalTimeInSeconds());
		//this.documentReader = new DocumentReader(this.config.getOutputFilePath());
		//this.kryoSerializer.serializeDocumentMap(documentObjectMap);
	}
	
	public void getSearchResults(String searchQuery)
	{
		String[] searchTerms = searchQuery.split("\\s");
		this.daat = new DocumentAtATime(searchTerms, this.lexiconObjectMap, this.bm25, this.documentObjectMap);
		roQueue = this.daat.getConjunctionResult();
		int count = 0;
		while(count<10 && !roQueue.isEmpty())
		{
			ResultObject ro = roQueue.poll();
			DocumentObject documentObject = this.documentObjectMap.get(ro.getDocumentId());
			System.out.println((count+1)+". "+documentObject.getDocumentPath() + " : " + ro.getBm25Score());
			count ++;
		}
	}
	
	public static void main(String[] args)
	{
		SearchMain sm = new SearchMain();
		int count = 0;
		Scanner scanner = new Scanner(System.in);
		while (count <20) {
			System.out.println("Search Query: ");
			String searchQuery = scanner.nextLine();
			ElapsedTime time = new ElapsedTime();
			sm.getSearchResults(searchQuery);
			count++;
			System.out.println(time.getTotalTimeInSeconds());
		}
		scanner.close();
	}
}
