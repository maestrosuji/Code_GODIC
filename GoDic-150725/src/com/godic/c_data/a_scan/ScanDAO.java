package com.godic.c_data.a_scan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScanDAO {

	static SQLiteDatabase db = null;
	static Context mContext;

	static ScanDAO scanDAO = new ScanDAO();

	private ScanDAO() {
	}

	static public ScanDAO getInstance(Context context) {
		mContext = context;
		if (db != null) {
			db.close();
		}
		return scanDAO;
	}

	public void dbCopy() {
		StringBuilder sb = new StringBuilder();
		Log.e("@@@@package name", mContext.getPackageName()+"");
		sb.append("/data/data/");
		sb.append(mContext.getPackageName());
		sb.append("/databases");

		String path = sb.toString();
		File dir = new File(path);
		dir.mkdir();
		File outfile = new File(path + "/dic.db");

		if (outfile.length() <= 0) {
			AssetManager am = mContext.getResources().getAssets();
			try {
				InputStream is = am.open("db/dic.db",
						AssetManager.ACCESS_BUFFER);
				long filesize = is.available();
				byte[] data = new byte[(int) filesize];
				is.read(data);
				is.close();
				outfile.createNewFile();

				FileOutputStream fos = new FileOutputStream(outfile);
				fos.write(data);
				fos.close();
				Log.e("@@@@@@@@@@copy db", "copy");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void dbOpen() {
		if(db == null){
			db = mContext
					.openOrCreateDatabase("dic.db", Context.MODE_PRIVATE, null);
			Log.e("ScanData", "db open");
		}
	}

	public void dbClose(){
		if(db != null){
			db.close();
			db = null;
			Log.e("ScanData", "db close");
		}
	}
	
	public String wordSelect(String engword) {
		String korstr;

		StringBuilder sb = new StringBuilder();
		sb.append("select kor from dic where eng=");
		sb.append("\"");
		sb.append(engword);
		sb.append(" \" limit 1");
		Cursor cursor = db.rawQuery(sb.toString(), null);
		if (cursor.moveToFirst()) {
			korstr = cursor.getString(0);
		} else {
			korstr = "";
		}
		if (cursor != null) {
			cursor.close();
		}
		return korstr;
	}

}
