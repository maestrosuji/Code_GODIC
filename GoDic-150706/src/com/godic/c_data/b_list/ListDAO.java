package com.godic.c_data.b_list;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.godic.c_data.a_scan.ScanData;
import com.godic.c_data.a_scan.WordInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class ListDAO {

	Context mContext;
	ListSQLiteOpenHelper helper = null;
	SQLiteDatabase listDB = null;
	static private ListDAO listDAO = new ListDAO();

	private ListDAO() {
	}

	static public ListDAO getInstance() {
		return listDAO;
	}

	public void dbInit(Context context) {
		mContext = context;
		helper = new ListSQLiteOpenHelper(context);
		listDB = helper.getWritableDatabase();
	}

	class ListSQLiteOpenHelper extends SQLiteOpenHelper {

		public ListSQLiteOpenHelper(Context context) {
			super(context, "wordshistory.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder sb = new StringBuilder();
			sb.append("create table wordshistory (");
			sb.append("_id integer primary key autoincrement, ");
			sb.append("writetitle text, ");
			sb.append("writedate integer, ");
			sb.append("wordslist blob);");

			String sql = sb.toString();
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "drop table if exists wordshistory";
			db.execSQL(sql);

			onCreate(db);
		}
	}

	public Cursor getCursor() {
		String sql = "select * from wordshistory order by _id";
		Cursor cursor = listDB.rawQuery(sql, null);
		return cursor;
	}

	// insert
	public void insert(String writetitle, Object wordslist, Bitmap bitmap) {
		listDB = helper.getWritableDatabase(); // db 객체를 얻어옴
		ContentValues values = new ContentValues();

		// 현재 날짜를 1000분의 1초 단위로 변환하여 저장
		Calendar cal = Calendar.getInstance();
		long writedate = cal.getTimeInMillis();

		String path = mContext.getFilesDir().getPath() + "/capture";
		File imageDir = new File(path);
		File imageOutFile = new File(path + "/" + writedate);

		Log.e("db save path", path);
		Log.e("db save time", writedate + "");

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;

		try {
			if (!imageDir.exists()) {
				imageDir.mkdir();
			}
			fos = new FileOutputStream(imageOutFile);
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(wordslist);

			ScanData.getInstance().bitmapInfo.bitmap.compress(
					CompressFormat.PNG, 100, fos);

			values.put("writetitle", writetitle);
			values.put("writedate", writedate);
			values.put("wordslist", baos.toByteArray());

			listDB.insert("wordshistory", null, values);
			listDB.close();
			Log.e("list db", "insert");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				baos.close();
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	// public ArrayList<HashMap<String, String>> select(long writedate) {
	// ArrayList<HashMap<String, String>> mapList = null;
	//
	// listDB = helper.getReadableDatabase();
	// String sql = "SELECT wordslist FROM wordshistory WHERE writedate=\""
	// + writedate + "\" limit 1";
	// Cursor cursor = listDB.rawQuery(sql, null);
	//
	// ByteArrayInputStream bais = null;
	// ObjectInputStream ois = null;
	//
	// if (cursor.moveToFirst()) {
	// byte[] buf = cursor.getBlob(0);
	//
	// try {
	// bais = new ByteArrayInputStream(buf);
	// ois = new ObjectInputStream(bais);
	// mapList = (ArrayList<HashMap<String, String>>) ois
	// .readObject();
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// ois.close();
	// bais.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// cursor.close();
	// listDB.close();
	// }
	// }
	// return mapList;
	// }

	public ArrayList<WordInfo> select(long writedate) {
		ArrayList<WordInfo> infoList = null;

		listDB = helper.getReadableDatabase();
		String sql = "SELECT wordslist FROM wordshistory WHERE writedate=\""
				+ writedate + "\" limit 1";
		Cursor cursor = listDB.rawQuery(sql, null);

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		if (cursor.moveToFirst()) {
			byte[] buf = cursor.getBlob(0);

			try {
				bais = new ByteArrayInputStream(buf);
				ois = new ObjectInputStream(bais);
				infoList = (ArrayList<WordInfo>) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					ois.close();
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				cursor.close();
				listDB.close();
			}
		}
		return infoList;
	}

	// delete
	// public void delete(String word) {
	// listDB = helper.getWritableDatabase();
	// String sql = "DELETE FROM vocabulary WHERE writetitle=\"" + word + "\"";
	// listDB.execSQL(sql);
	// listDB.close();
	//
	// for (VocaInfo info : vocaData.vocaList) {
	// if (info.title.equals(word)) {
	// vocaData.vocaList.remove(info);
	// break;
	// }
	// }
	// }
	//
	// public void delete(long id) {
	// listDB = helper.getWritableDatabase();
	// String sql = "DELETE FROM vocabulary WHERE _id=" + id;
	// listDB.execSQL(sql);
	// listDB.close();
	//
	// for (VocaInfo info : vocaData.vocaList) {
	// if (info.id == id) {
	// vocaData.vocaList.remove(info);
	// break;
	// }
	// }
	// }
	//
	// // select
	// public void select(int _id) {
	// listDB = helper.getReadableDatabase();
	// String sql =
	// "SELECT _id, writedate, writetitle, wordslist FROM vocabulary WHERE _id=\""
	// + _id + "\" ORDER BY _id";
	// ByteArrayInputStream bais;
	// ObjectInputStream ois;
	// VocaInfo vocainfo;
	//
	// Cursor cursor = listDB.rawQuery(sql, null);
	// while (cursor.moveToNext()) {
	// vocainfo = new VocaInfo();
	// vocainfo.id = cursor.getInt(0);
	// vocainfo.writedate = cursor.getLong(1);
	// vocainfo.title = cursor.getString(2);
	// byte[] buf = cursor.getBlob(3);
	//
	// try {
	// bais = new ByteArrayInputStream(buf);
	// ois = new ObjectInputStream(bais);
	// vocainfo.mapList = (ArrayList<HashMap<String, String>>) ois
	// .readObject();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// vocaData.vocaList.add(vocainfo);
	// }
	// cursor.close();
	// listDB.close();
	// }
	//
	// public void select(String writetitle) {
	// listDB = helper.getReadableDatabase();
	// String sql =
	// "SELECT _id, writedate, writetitle, wordslist FROM vocabulary WHERE writetitle=\""
	// + writetitle + "\" ORDER BY _id";
	// ByteArrayInputStream bais;
	// ObjectInputStream ois;
	// VocaInfo vocainfo;
	//
	// Cursor cursor = listDB.rawQuery(sql, null);
	// while (cursor.moveToNext()) {
	// vocainfo = new VocaInfo();
	// vocainfo.id = cursor.getInt(0);
	// vocainfo.writedate = cursor.getLong(1);
	// vocainfo.title = cursor.getString(2);
	// byte[] buf = cursor.getBlob(3);
	//
	// try {
	// bais = new ByteArrayInputStream(buf);
	// ois = new ObjectInputStream(bais);
	// vocainfo.mapList = (ArrayList<HashMap<String, String>>) ois
	// .readObject();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// vocaData.vocaList.add(vocainfo);
	// }
	// cursor.close();
	// listDB.close();
	// }
	//
	// public void selectLast() {
	// listDB = helper.getReadableDatabase();
	// String sql =
	// "select * from vocabulary where _id=(select max(_id) from vocabulary)";
	// ByteArrayInputStream bais;
	// ObjectInputStream ois;
	// VocaInfo vocainfo;
	//
	// Cursor cursor = listDB.rawQuery(sql, null);
	// if (cursor.moveToFirst()) {
	// vocainfo = new VocaInfo();
	// vocainfo.id = cursor.getInt(0);
	// vocainfo.writedate = cursor.getLong(1);
	// vocainfo.title = cursor.getString(2);
	// byte[] buf = cursor.getBlob(3);
	// try {
	// bais = new ByteArrayInputStream(buf);
	// ois = new ObjectInputStream(bais);
	// vocainfo.mapList = (ArrayList<HashMap<String, String>>) ois
	// .readObject();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// vocaData.vocaList.add(vocainfo);
	// }
	// cursor.close();
	// listDB.close();
	// }
	//
	// // get voca list
	// public void selectAll() {
	// listDB = helper.getReadableDatabase();
	// ByteArrayInputStream bais;
	// ObjectInputStream ois;
	// VocaInfo vocainfo;
	// Cursor cursor = listDB.rawQuery(
	// "SELECT _id, writedate, writetitle, wordslist FROM vocabulary",
	// null);
	// while (cursor.moveToNext()) {
	// vocainfo = new VocaInfo();
	// vocainfo.id = cursor.getInt(0);
	// vocainfo.writedate = cursor.getLong(1);
	// vocainfo.title = cursor.getString(2);
	// byte[] buf = cursor.getBlob(3);
	//
	// try {
	// bais = new ByteArrayInputStream(buf);
	// ois = new ObjectInputStream(bais);
	// vocainfo.mapList = (ArrayList<HashMap<String, String>>) ois
	// .readObject();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// vocaData.vocaList.add(vocainfo);
	// }
	// cursor.close();
	// listDB.close();
	// }

}
