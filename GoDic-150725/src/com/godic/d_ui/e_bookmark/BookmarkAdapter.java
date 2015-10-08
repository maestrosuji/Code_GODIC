package com.godic.d_ui.e_bookmark;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.go.dic.R;
import com.godic.c_data.a_scan.WordInfo;
import com.godic.d_ui.d_item.StarCheckBox;

public class BookmarkAdapter extends 
		RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>{

	BookmarkDAO bmkDAO = BookmarkDAO.getInstance();
	Cursor mCursor;
	Context mContext;
	Toast toast;
		
	public BookmarkAdapter(Context context, Cursor cursor){
		mContext = context;
		mCursor = cursor;
	}
	
	public class BookmarkViewHolder extends RecyclerView.ViewHolder{
		TextView tv_engword, tv_korword;
		StarCheckBox btn_star;
		WordInfo bmkInfo;
				
		public BookmarkViewHolder(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			tv_engword = (TextView) itemView.findViewById(R.id.bookmark_tv_eng);
			tv_korword = (TextView) itemView.findViewById(R.id.bookmark_tv_kor);
			btn_star = (StarCheckBox) itemView.findViewById(R.id.btn_star);
			
			btn_star.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (bmkInfo.bmkstate) {
						bmkDAO.delete(bmkInfo);
					}
				}
			});
			
		}

	}
	
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mCursor.getCount();
	}
	@Override
	public void onBindViewHolder(BookmarkViewHolder holder, int position) {
		// TODO Auto-generated method stub
		
		mCursor.moveToPosition(getItemCount() - position - 1);

		holder.bmkInfo = new WordInfo(mCursor.getString(2),mCursor.getString(3));
		if(mCursor.getInt(1)==1){
			holder.bmkInfo.bmkstate = true;
		}else{
			holder.bmkInfo.bmkstate = false;
		}
		Log.e("@@@bmkInfo state", holder.bmkInfo.engword+", "+holder.bmkInfo.korword+", "+holder.bmkInfo.bmkstate+"");
		holder.tv_engword.setText(holder.bmkInfo.engword);
		holder.tv_korword.setText(holder.bmkInfo.korword);
		holder.btn_star.setChecked(holder.bmkInfo.bmkstate);
		
	}
	@Override
	public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.bookmark_adapt, parent, false);
		return new BookmarkViewHolder(v);
	}

	
}
