package com.wse.queryprocessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.compress.VByte;
import com.wse.model.Lexicon;
import com.wse.model.PostingObject;
import com.wse.model.ResultObject;
import com.wse.ranking.BM25;
import com.wse.util.CloseUtil;

public class DocumentAtATime 
{
	private BM25 bm25;
	private int resultCount;
	private Lexicon[] lexicons;
	private Lexicon[] termLexicons;
	private List<String> queryTerms;
	private String invertedIndexFilePath;
	private Map<String, Long> filePointerMap;
	private Queue<ResultObject> priorityQueue;
	private RandomAccessFile randomAccessFile;
	private Map<String, PostingObject> postingObjectMap;
	
	private final Logger logger = LoggerFactory.getLogger(DocumentAtATime.class);
	
	public DocumentAtATime(List<String> queryTerms, Lexicon[] lexicons, int resultCount, BM25 bm25)
	{
		this.queryTerms = queryTerms;
		this.lexicons = lexicons;
		this.filePointerMap = new HashMap<>();
		this.postingObjectMap = new HashMap<>();
		this.termLexicons = new Lexicon[queryTerms.size()];
		this.resultCount = resultCount;
		this.priorityQueue = new PriorityQueue<>();
		this.bm25 = bm25;
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
		
	public void openList(String queryTerm, int addAtIndex)
	{
		long filePointer = -1; 
		Lexicon q = new Lexicon(queryTerm, 0, 0, 0);
		int index = Arrays.binarySearch(lexicons, q, new Lexicon("", 0, 0, 0));
		Lexicon lexicon = lexicons[index];
		this.termLexicons[addAtIndex] = lexicons[index];
		try
		{
			this.randomAccessFile.seek(lexicon.getPostingListStart());
			filePointer = this.randomAccessFile.getFilePointer();
			filePointerMap.put(lexicon.getWord(), filePointer);
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
	
	public void getConjunctionResult()
	{
		for(int i=0;i<this.queryTerms.size();i++)
		{
			this.openList(queryTerms.get(i), 0);
		}
		Arrays.sort(this.termLexicons, new Comparator<Lexicon>()
		{
			@Override
			public int compare(Lexicon l1, Lexicon l2) 
			{
				return l1.getPostingListLength()-l2.getPostingListLength();
			}
		});
		
		int count = 0;
		boolean flagEndOfList =  false;
		PostingObject po = new PostingObject(0,0);
		while(count < resultCount && flagEndOfList==false)
		{
			/* get next post from shortest list */
			String term = termLexicons[0].getWord();
			po = nextGEQ(term, filePointerMap.get(term), po.getDocumentId());
			//ToDo: check endOfList reached break the while loop
			/* see if you find entries with same docID in other lists */
			PostingObject d = null;
			for (int i=1;
					 (i<termLexicons.length) && 
					 	((d=nextGEQ(termLexicons[i].getWord(), 
					 			filePointerMap.get(termLexicons[i].getWord()), 
					 				po.getDocumentId()))).getDocumentId() == po.getDocumentId(); i++);
			if(d.getDocumentId()> po.getDocumentId())
				po.setDocumentId(d.getDocumentId());
			else
			{
				ResultObject ro = new ResultObject(po.getDocumentId());
				for(int i=0;i<termLexicons.length;i++)
				{
					//ToDo: getDocument Length from the list of documentId, url and document length
					// replace 12345
					ro.setBm25Score(bm25.getScore(termLexicons[i].getPostingListLength(), 
							postingObjectMap.get(termLexicons[i].getWord()).getFrequency(), 12345) + ro.getBm25Score());
				}
				priorityQueue.add(ro);
				po.setDocumentId(po.getDocumentId() + 1);
			}
				
		}
		CloseUtil.close(this.randomAccessFile);
	}
}
