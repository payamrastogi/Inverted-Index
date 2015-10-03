package com.wse.parse;

public class WordIndex {
	private String word;
	private String docId;
	private int count;
	
	public WordIndex(String word, String docId, int count) {
		this.word = word;
		this.docId = docId;
		this.count = count;
	}
	
	public WordIndex() {
		this.word = null;
		this.docId = null;
		this.count = 0;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "WordIndex : word = " + this.word + " : docId = " + this.docId +" : Count : "+ this.count;
	}
}
