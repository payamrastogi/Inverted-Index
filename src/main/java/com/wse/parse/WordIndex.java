package com.wse.parse;

public class WordIndex {
	private String word;
	private int docId;
	private int count;
	
	public WordIndex(String word, int docId, int count) {
		this.word = word;
		this.docId = docId;
		this.count = count;
	}
	
	public String getWord() {
		return word;
	}

	public int getDocId() {
		return docId;
	}
	
	public int getCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return "WordIndex : word = " + this.word + " : docId = " + this.docId +" : Count : "+ this.count;
	}
}
