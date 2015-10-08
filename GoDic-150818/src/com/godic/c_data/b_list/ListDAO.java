package com.godic.c_data.b_list;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.godic.c_data.WordInfo;
import com.godic.c_data.a_scan.ScanData;

public class ListDAO {

	Context mContext;
	ListSQLiteOpenHelper helper = null;
	SQLiteDatabase listDB = null;
	static private ListDAO listDAO = new ListDAO();
	Cursor mCursor = null;

	private ListDAO() {
	}

	static public ListDAO getInstance() {
		return listDAO;
	}

	public void dbInit(Context context) {
		mContext = context;
		helper = new ListSQLiteOpenHelper(context);
		closeCursor();
		if (listDB != null) {
			listDB.close();
		}
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

	public void openCursor() {
		String sql = "select * from wordshistory order by _id";
		mCursor = listDB.rawQuery(sql, null);
	}

	public int getCursorCount() {
		if (mCursor == null) {
			openCursor();
		}
		return mCursor.getCount();
	}

	public void closeCursor() {
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
	}

	public void changeCursor() {
//		Cursor oldCursor = mCursor;
		openCursor();
//		oldCursor.close();
	}

	public void setCursorOnPosition(int position) {
		if (mCursor == null) {
			openCursor();
		}
		mCursor.moveToPosition(position);
	}

	public int getIdInCursor(int position) {
		setCursorOnPosition(position);
		return mCursor.getInt(0);
	}

	public String getTitleInCursor() {
		return mCursor.getString(1);
	}

	public long getDateInCursor() {
		return mCursor.getLong(2);
	}

	public long getDateInCursor(int position) {
		setCursorOnPosition(position);
		return mCursor.getLong(2);
	}

	public String getFormattingDateInCursor() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy.MM.dd(EEE) HH:mm:ss");
		cal.setTimeInMillis(mCursor.getLong(2));
		return dateFormat.format(cal.getTime());
	}

	public Bitmap getImageInCursor() {
		String imagePath = mContext.getFilesDir().getPath() + "/capture/"
				+ mCursor.getLong(2);
		Log.e("open image path", imagePath);
		// BitmapFactory.Options bfo = new BitmapFactory.Options();
		// bfo.inSampleSize = 2;
		return BitmapFactory.decodeFile(imagePath);
	}

	public ArrayList<WordInfo> getWlistInCursor() {
		ArrayList<WordInfo> list = null;

		byte[] buf = mCursor.getBlob(3);
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(buf);
			ois = new ObjectInputStream(bais);
			list = (ArrayList<WordInfo>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (ois != null) {
				ois.close();
			}
			if (bais != null) {
				bais.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public ArrayList<WordInfo> getWlistInCursor(int position) {
		setCursorOnPosition(position);

		ArrayList<WordInfo> list = null;

		byte[] buf = mCursor.getBlob(3);
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(buf);
			ois = new ObjectInputStream(bais);
			list = (ArrayList<WordInfo>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (ois != null) {
				ois.close();
			}
			if (bais != null) {
				bais.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String getWlistStrInCursor() {
		ArrayList<WordInfo> list;
		StringBuilder sb = null;

		byte[] buf = mCursor.getBlob(3);
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(buf);
			ois = new ObjectInputStream(bais);
			list = (ArrayList<WordInfo>) ois.readObject();
			sb = new StringBuilder();
			for (WordInfo info : list) {
				sb.append(info.engword);
				sb.append(' ');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (ois != null) {
				ois.close();
			}
			if (bais != null) {
				bais.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
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
			Log.e("list db", "insert");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				baos.close();
				fos.close();
				listDB.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void update(String editTitle, int id) {
		listDB = helper.getWritableDatabase();
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append("update wordshistory set writetitle='");
		sqlsb.append(editTitle);
		sqlsb.append("' where _id='");
		sqlsb.append(id);
		sqlsb.append("'");

		listDB.execSQL(sqlsb.toString());
		listDB.close();
	}

	public void delete(long ldate) {
		String imagepath = mContext.getFilesDir().getPath() + "/capture/"
				+ ldate;
		File dir = new File(imagepath);
		if (dir.exists()) {
			dir.delete();
		}

		listDB = helper.getWritableDatabase();
		String sql = "delete from wordshistory where writedate='" + ldate + "'";
		listDB.execSQL(sql);
		listDB.close();
	}

	public int getCursorPosition() {
		return mCursor.getPosition();
	}

	// public void cursorRegister(){
	// mCursor.registerDataSetObserver(new DataSetObserver() {
	// @Override
	// public void onChanged() {
	// super.onChanged();
	//
	// }
	// });
	// }

}
