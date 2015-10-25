package com.wse.queryprocessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wse.compress.VByte;
import com.wse.model.DocumentObject;
import com.wse.model.LexiconObject;
import com.wse.model.PostingObject;
import com.wse.model.ResultObject;
import com.wse.ranking.BM25;
import com.wse.util.CloseUtil;

public class DocumentAtATime 
{
	private BM25 bm25;
	private int resultCount;
	private Map<String, LexiconObject> lexiconObjectMap;
	private LexiconObject[] termLexicons;
	private String[] queryTerms;
	private String invertedIndexFilePath="/home/jenil/Downloads/indexer/output/m_0i";
	private Map<String, Long> filePointerMap;
	private Queue<ResultObject> priorityQueue;
	private RandomAccessFile randomAccessFile;
	private Map<String, PostingObject> postingObjectMap;
	private Map<Long, DocumentObject> documentObjectMap;
	
	private final Logger logger = LoggerFactory.getLogger(DocumentAtATime.class);
	
	public DocumentAtATime(String[] queryTerms, Map<String, LexiconObject> lexiconObjectMap, int resultCount, BM25 bm25,
			Map<Long, DocumentObject> documentObjectMap)
	{
		this.queryTerms = queryTerms;
		this.lexiconObjectMap = lexiconObjectMap;
		this.filePointerMap = new HashMap<>();
		this.postingObjectMap = new HashMap<>();
		this.termLexicons = new LexiconObject[queryTerms.length];
		this.resultCount = resultCount;
		this.priorityQueue = new PriorityQueue<>();
		this.bm25 = bm25;
		this.documentObjectMap = documentObjectMap;
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
		LexiconObject lexicon = lexiconObjectMap.get(queryTerm);
		this.termLexicons[addAtIndex] = lexicon;
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
		while(postingObject!=null && postingObject.getDocumentId() < documentId)
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
		if (filePointer >= lexiconObjectMap.get(term).getPostingListEnd())
			return null;
		try
		{
			VByte vByte = new VByte();
			docId = vByte.decode(this.randomAccessFile, filePointer);
			freq = vByte.decode(randomAccessFile, randomAccessFile.getFilePointer());
			postingObject = postingObjectMap.get(term);
			logger.debug("-->"+docId + " " +postingObject.getDocumentId()+" "+(postingObject.getDocumentId()+docId) +" "+term);
			docId += postingObject.getDocumentId();
			//logger.debug("-->"+docId + " " + term);
			postingObject = new PostingObject(docId, freq);
			postingObjectMap.put(term, new PostingObject(docId, freq));
			filePointerMap.put(term, randomAccessFile.getFilePointer());
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		return postingObject;
	}
	
	public Queue<ResultObject> getConjunctionResult()
	{
		for(int i=0;i<this.queryTerms.length;i++)
		{
			this.openList(queryTerms[i], i);
		}
		Arrays.sort(this.termLexicons, new Comparator<LexiconObject>()
		{
			@Override
			public int compare(LexiconObject l1, LexiconObject l2) 
			{
				return l1.getPostingListLength()-l2.getPostingListLength();
			}
		});
		
		int count = 0;
		boolean flagEndOfList =  false;
		PostingObject po = new PostingObject(0,0);
		while(flagEndOfList==false)
		{
			/* get next post from shortest list */
			logger.debug(priorityQueue.toString());
			String term = termLexicons[0].getWord();
			po = nextGEQ(term, filePointerMap.get(term), po.getDocumentId());
			if (po==null)
				break;
			//ToDo: check endOfList reached break the while loop
			/* see if you find entries with same docID in other lists */
			PostingObject d = null;
			try {
			for (int i=1;
					 (i<termLexicons.length) && 
					 	((d=nextGEQ(termLexicons[i].getWord(), 
					 			filePointerMap.get(termLexicons[i].getWord()), 
					 				po.getDocumentId()))).getDocumentId() == po.getDocumentId(); i++);
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
			if (d==null)
				break;
			if(d.getDocumentId()> po.getDocumentId())
				po.setDocumentId(d.getDocumentId());
			else
			{
				ResultObject ro = new ResultObject(po.getDocumentId());
				for(int i=0;i<termLexicons.length;i++)
				{
					long documentLength = documentObjectMap.get(po.getDocumentId()).getDocumentLength(); 
					ro.setBm25Score(bm25.getScore(termLexicons[i].getPostingListLength(), 
							postingObjectMap.get(termLexicons[i].getWord()).getFrequency(), documentLength) + ro.getBm25Score());
				}
				priorityQueue.add(ro);
				po.setDocumentId(po.getDocumentId() + 1);
			}
				
		}
		CloseUtil.close(this.randomAccessFile);
		return priorityQueue;
	}
}
