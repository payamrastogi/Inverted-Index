package com.wse.queryprocessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.compress.VByte;
import com.wse.model.Lexicon;
import com.wse.model.PostingObject;

public class DocumentAtATime 
{
	private List<String> queryTerms;
	private Lexicon[] lexicons;
	private Lexicon[] termLexicons;
	private String invertedIndexFilePath;
	private RandomAccessFile randomAccessFile;
	private final Logger logger = LoggerFactory.getLogger(DocumentAtATime.class);
	private Map<String, Long> filePointerMap;
	private Map<String, PostingObject> postingObjectMap;
	
	public DocumentAtATime(List<String> queryTerms, Lexicon[] lexicons)
	{
		this.queryTerms = queryTerms;
		this.lexicons = lexicons;
		this.filePointerMap = new HashMap<>();
		this.postingObjectMap = new HashMap<>();
		this.termLexicons = new Lexicon[queryTerms.size()];
		for(String term:queryTerms)
		{
			postingObjectMap.put(term, new PostingObject(0, 0));
		}
		try
		{
			this.randomAccessFile = new RandomAccessFile(this.invertedIndexFilePath, "r");
		}
		catch(FileNotFoundException e)
		{
			logger.error(e.getMessage(), e);
		}
	}
	//ToDo: sort termLexicons after call to openList finishes;
	//	Arrays.sort(termLexicons, new Comparator<Lexicon>()
	//	{
	//		@Override
	//		public int compare(Lexicon l1, Lexicon l2) 
	//		{
	//			return l1.getPostingListLength()-l2.getPostingListLength();
	//		}
	//	});
	public void openList(Lexicon queryTerm, int addAtIndex)
	{
		long filePointer = -1; 
		int index = Arrays.binarySearch(lexicons, queryTerm, new Lexicon("", 0, 0, 0));
		Lexicon lexicon = lexicons[index];
		this.termLexicons[addAtIndex] = lexicons[index];
		try
		{
			this.randomAccessFile.seek(lexicon.getPostingListStart());
			filePointer = this.randomAccessFile.getFilePointer();
			filePointerMap.put(queryTerm.getWord(), filePointer);
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}
	
	public PostingObject nextGEQ(String term, long filePointer, long documentId)
	{
		PostingObject postingObject = this.getDocumentId(term, filePointer);
		while(postingObject.getDocumentId() < documentId)
		{
			postingObject = this.getDocumentId(term, filePointerMap.get(term));
		}
		return postingObject;
	}
	
	public PostingObject getDocumentId(String term, long filePointer)
	{	
		//ToDo : check for end of the posting list reach
		long docId = -1;
		long freq = -1;
		PostingObject postingObject = null;
		try
		{
			VByte vByte = new VByte();
			docId = vByte.decode(this.randomAccessFile, filePointer);
			freq = vByte.decode(randomAccessFile, randomAccessFile.getFilePointer());
			postingObject = postingObjectMap.get(term);
			docId += postingObject.getDocumentId();
			postingObject = new PostingObject(docId, freq);
			postingObjectMap.put(term, postingObject);
			filePointerMap.put(term, randomAccessFile.getFilePointer());
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		return postingObject;
	}
}
