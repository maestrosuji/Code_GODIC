package com.godic.c_data.c_item;

import java.util.ArrayList;
import java.util.HashMap;

import com.godic.c_data.a_scan.WordInfo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ItemData {
	public String title, date;
	public Drawable captureImage;
//	public ArrayList<HashMap<String, String>> wordMapList;
	public ArrayList<WordInfo> wordInfoList;
	private static ItemData itemData = new ItemData();

	private ItemData() {
	}

	static public ItemData getInstance() {
		return itemData;
	}

//	public void setData(String title, String date,
//			ArrayList<HashMap<String, String>> mapList, Drawable image) {
//		this.title = title;
//		this.date = date;
//		wordMapList  = mapList;
//		captureImage = image;
//	}
	
	public void setData(String title, String date,
			ArrayList<WordInfo> infoList, Drawable image) {
		this.title = title;
		this.date = date;
		wordInfoList  = infoList;
		captureImage = image;
	}
}
