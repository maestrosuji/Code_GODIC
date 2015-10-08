package com.godic.d_ui.e_bookmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.godic.c_data.a_scan.WordInfo;

public class BookmarkDAO {
	Context mContext;
	ListSQLiteOpenHelper helper = null;
	SQLiteDatabase bmkDB = null;
	static private BookmarkDAO bmkDAO = new BookmarkDAO();
	
	private BookmarkDAO() {
	}
	
	static public BookmarkDAO getInstance(){
		
		return bmkDAO;
	}
	
	public void bmkInit(Context context){
		mContext = context;
		if (bmkDB != null) {
			bmkDB.close();
		}
		helper = new ListSQLiteOpenHelper(context);
		bmkDB = helper.getWritableDatabase();		
	}
	public void bmkClose(){
		if(bmkDB != null){
			bmkDB.close();
			bmkDB = null;
			Log.e("BookmarkDB", "db close");
		}
	}
	
	class ListSQLiteOpenHelper extends SQLiteOpenHelper {

		public ListSQLiteOpenHelper(Context context) {
			super(context, "bookmark.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder sb = new StringBuilder();
			sb.append("create table bookmark (");
			sb.append("_id integer primary key autoincrement, ");
			sb.append("bmkstate integer, ");
			sb.append("engword text, ");
			sb.append("korword text);");
			

			String sql = sb.toString();
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "drop table if exists bookmark";
			db.execSQL(sql);

			onCreate(db);
		}
	}
	
	public Cursor getCursor(){
		String sql = "select * from bookmark order by _id";
		Cursor cursor = bmkDB.rawQuery(sql, null);
		return cursor;
	}
	
	public void checkState(WordInfo wordInfo){
				
		String sql = "select engword from bookmark where engword = '" + wordInfo.engword + "' limit 1";
		Cursor cursor = bmkDB.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			wordInfo.bmkstate = true;
		}		
	}
	
	public void insert(WordInfo wordInfo){
		bmkDB = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		wordInfo.bmkstate = true;
		
		if(wordInfo.bmkstate == true){
			values.put("bmkstate",1);
		}else{
			values.put("bmkstate",0);
		}
		values.put("engword", wordInfo.engword);
		values.put("korword", wordInfo.korword);
		
		bmkDB.insert("bookmark", null, values);
		Log.e("bmk db", "insert");
		
	}	
	
	public void delete(WordInfo wordInfo){
				
		bmkDB = helper.getWritableDatabase();
		String sql = "delete from bookmark where engword = '" + wordInfo.engword + "'";
		bmkDB.execSQL(sql);
		
		wordInfo.bmkstate = false;
		
	}
}
