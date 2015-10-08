package com.godic.d_ui.e_bookmark;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.go.dic.R;
import com.godic.c_data.WordInfo;
import com.godic.c_data.d_bookmark.BookmarkDAO;

public class BookmarkTestAdapter extends RecyclerView.Adapter<BookmarkTestAdapter.BookmarkViewHolder>{

	BookmarkDAO bmkDAO = BookmarkDAO.getInstance();
	Context mContext;
	OnTouchListener touchListener;
	
	public BookmarkTestAdapter(Context context, OnTouchListener listener){
		mContext = context;
		touchListener = listener;
	}
	
	public class BookmarkViewHolder extends RecyclerView.ViewHolder{
		TextView tv_engword;
		WordInfo bmkInfo;
		public BookmarkViewHolder(View itemView) {
			super(itemView);
			tv_engword = (TextView) itemView.findViewById(R.id.bookmark_tv_eng);
			tv_engword.setOnTouchListener(touchListener);
		}
		
	}

	@Override
	public int getItemCount() {
		return bmkDAO.getCursorCount();
	}

	@Override
	public void onBindViewHolder(BookmarkViewHolder holder, int position) {
		bmkDAO.setCursorPosition(getItemCount() - position - 1);

		holder.tv_engword.setText(bmkDAO.getCursorEng());
	}

	@Override
	public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.bookmark_test_adapter, parent, false);
		return new BookmarkViewHolder(v);
	}
}
