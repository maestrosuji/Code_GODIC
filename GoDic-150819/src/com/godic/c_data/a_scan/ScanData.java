package com.godic.c_data.a_scan;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.graphics.Bitmap;

import com.godic.c_data.WordInfo;

public class ScanData {

	public ArrayList<String> wordList = new ArrayList<String>();
//	public ArrayList<HashMap<String, String>> wordMapList = new ArrayList<HashMap<String, String>>();
	public ArrayList<WordInfo> wordInfoList = new ArrayList<WordInfo>();
	// public int wordLength = 1;
	// public int selectScreen = 0;
	// public int selectDic = 0;
	public BitmapInfo bitmapInfo = new BitmapInfo();
	static ScanData scanData = new ScanData();

	public class BitmapInfo {
		public Bitmap bitmap;
		public int x, y, w, h;
		public boolean stateBitmap = false;
	}
	
	private ScanData() {

	}

	static public ScanData getInstance() {
		return scanData;
	}

	public int getLength() {
		return wordList.size();
	}

//	public void sort() {
//		Collections.sort(wordList);
//		Collections.sort(wordMapList, myComparator);
//	}
	
	public void sort(){
		Collections.sort(wordList);
		Collections.sort(wordInfoList, infoComparator);
	}

//	Comparator<HashMap<String, String>> myComparator = new Comparator<HashMap<String, String>>() {
//
//		@Override
//		public int compare(HashMap<String, String> lhs,
//				HashMap<String, String> rhs) {
//			Collator collator = Collator.getInstance();
//			return collator.compare(lhs.get("engword"), rhs.get("engword"));
//		}
//	};

	Comparator<WordInfo> infoComparator = new Comparator<WordInfo>() {
		
		@Override
		public int compare(WordInfo lhs, WordInfo rhs) {
			Collator collator = Collator.getInstance();
			return collator.compare(lhs.engword, rhs.engword);
		}
	};
	
//	public void reverse() {
//		Collections.reverse(wordList);
//		Collections.reverse(wordMapList);
//	}
	
	public void reverse(){
		Collections.reverse(wordList);
		Collections.reverse(wordInfoList);
	}

//	public void remove(String engword) {
//		wordList.remove(engword);
//		for (HashMap<String, String> wordMap : wordMapList) {
//			if (wordMap.get("engword").equals(engword)) {
//				wordMapList.remove(wordMap);
//				break;
//			}
//		}
//	}

//	public void remove(int index) {
//		wordList.remove(index);
//		wordMapList.remove(index);
//	}
	
	public void remove(int index) {
//		wordList.remove(index);
		wordInfoList.remove(index);
	}

//	public void clear() {
//		wordList.clear();
//		wordMapList.clear();
//	}
	
	public void clear() {
		wordList.clear();
		wordInfoList.clear();
	}

//	public String getKorWord(String engword) {
//		for (HashMap<String, String> wordMap : wordMapList) {
//			if (wordMap.get("engword").equals(engword)) {
//				return wordMap.get("korword");
//			}
//		}
//		return null;
//	}

	public String getEngword(int index) {
		return wordList.get(index);
	}

//	public String getKorword(int index) {
//		return wordMapList.get(index).get("korword");
//	}
	
	public String getKorword(int index) {
		return wordInfoList.get(index).korword;
	}

//	public void addWord(String engword, String korword) {
//		HashMap<String, String> wordMap = new HashMap<String, String>();
//		wordMap.put("engword", engword);
//		wordMap.put("korword", korword);
//		wordList.add(engword);
//		wordMapList.add(wordMap);
//	}
	
	public void addWord(String engword, String korword) {
		wordInfoList.add(new WordInfo(engword, korword));
	}
	
	public void addWord(WordInfo info){
		wordInfoList.add(info);
	}

//	public void wordChange(String fromEngword, String toEngword, String korword) {
//		int index = wordList.indexOf(fromEngword);
//		wordChange(index, toEngword, korword);
//	}

//	public void wordChange(int index, String engword, String korword) {
//		wordList.remove(index);
//		wordList.add(index, engword);
//
//		wordMapList.remove(index);
//		HashMap<String, String> wordMap = new HashMap<String, String>();
//		wordMap.put("engword", engword);
//		wordMap.put("korword", korword);
//		wordMapList.add(index, wordMap);
//
//	}
}