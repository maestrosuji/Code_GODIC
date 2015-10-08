package com.godic.d_ui.e_bookmark;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
	
	public BookmarkTestAdapter(Context context){
		mContext = context;
	}
	
	public class BookmarkViewHolder extends RecyclerView.ViewHolder{
		TextView tv_engword;
		WordInfo bmkInfo;
		public BookmarkViewHolder(View itemView) {
			super(itemView);
			tv_engword = (TextView) itemView.findViewById(R.id.bookmark_tv_eng);
			tv_engword.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						TextView tv = (TextView) v;
						String engword = tv.getText().toString();
						BookmarkTest.tv_vocaword_eng.setText(engword);
						BookmarkTest.tv_vocaword_kor.setText(bmkDAO.getKorword(engword));
						Log.e("@@@EngWord on Touched", engword);
//						tv_engword.setBackgroundColor(color.background_material_light);
						break;

					case MotionEvent.ACTION_UP:
						BookmarkTest.tv_vocaword_eng.setText("");
						BookmarkTest.tv_vocaword_kor.setText("");
//						tv_engword.setBackgroundColor(color.background_material_dark);
						break;
						
					case MotionEvent.ACTION_CANCEL:
						BookmarkTest.tv_vocaword_eng.setText("");
						BookmarkTest.tv_vocaword_kor.setText("");
//						tv_engword.setBackgroundColor(color.background_material_dark);
						break;

					default:
						break;
					}
					return true;
				}

			});
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
