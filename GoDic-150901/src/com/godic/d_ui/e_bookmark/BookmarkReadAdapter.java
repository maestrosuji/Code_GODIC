package com.godic.d_ui.e_bookmark;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.go.dic.R;
import com.godic.c_data.WordInfo;
import com.godic.c_data.d_bookmark.BookmarkDAO;
import com.godic.d_ui.d_item.StarCheckBox;

public class BookmarkReadAdapter extends
		RecyclerView.Adapter<BookmarkReadAdapter.BookmarkViewHolder> {

	BookmarkDAO bmkDAO = BookmarkDAO.getInstance();
	Context mContext;
	Toast toast;

	public BookmarkReadAdapter(Context context) {
		mContext = context;
	}

	public class BookmarkViewHolder extends RecyclerView.ViewHolder {
		TextView tv_engword, tv_korword;
		StarCheckBox btn_star;
		WordInfo bmkInfo;

		public BookmarkViewHolder(View itemView) {
			super(itemView);
			tv_engword = (TextView) itemView.findViewById(R.id.bookmark_tv_eng);
			tv_korword = (TextView) itemView.findViewById(R.id.bookmark_tv_kor);
			btn_star = (StarCheckBox) itemView.findViewById(R.id.btn_star);

			btn_star.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (bmkInfo.bmkstate) {
						bmkInfo.bmkstate = false;
						bmkDAO.delete(bmkInfo);
					}
					else{
						bmkInfo.bmkstate = true;
						bmkDAO.insert(bmkInfo);
					}
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

//		holder.tv_engword.setText(bmkDAO.getCursorEng());
//		holder.tv_korword.setText(bmkDAO.getCursorKor());
//		holder.btn_star.setChecked(true);
		holder.bmkInfo = bmkDAO.getCursorInfo();
		holder.tv_engword.setText(holder.bmkInfo.engword);
		holder.tv_korword.setText(holder.bmkInfo.korword);
		holder.btn_star.setChecked(holder.bmkInfo.bmkstate);
	}

	@Override
	public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.bookmark_read_adapter, parent, false);
		return new BookmarkViewHolder(v);
	}

}
