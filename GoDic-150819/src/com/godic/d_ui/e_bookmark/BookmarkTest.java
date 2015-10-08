package com.godic.d_ui.e_bookmark;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.go.dic.R;
import com.godic.c_data.d_bookmark.BookmarkDAO;

public class BookmarkTest extends Fragment {

	Context mContext;
	View view;
	Toolbar toolbar;
	RecyclerView rv_words;
	RecyclerView.Adapter mainAdapter;
	RecyclerView.LayoutManager layoutManager;
	BookmarkDAO bmkDAO = null;
	TextView tv_vocaword_eng, tv_vocaword_kor;

	public BookmarkTest(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.bookmark_test, container, false);

		rv_words = (RecyclerView) view.findViewById(R.id.item_rv);
		rv_words.setHasFixedSize(true);

		tv_vocaword_eng = (TextView) view.findViewById(R.id.tv_vocaword_eng);
		tv_vocaword_kor = (TextView) view.findViewById(R.id.tv_vocaword_kor);

		bmkDAO = BookmarkDAO.getInstance();
		layoutManager = new LinearLayoutManager(mContext);
		rv_words.setLayoutManager(layoutManager);
		mainAdapter = new BookmarkTestAdapter(mContext, touchListener);
		rv_words.setAdapter(mainAdapter);

		return view;
	}

	OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				TextView tv = (TextView) v;
				String engword = tv.getText().toString();
				tv_vocaword_eng.setText(engword);
				tv_vocaword_kor.setText(bmkDAO.getKorword(engword));
				Log.e("@@@EngWord on Touched", engword);
				break;

			case MotionEvent.ACTION_UP:
				tv_vocaword_eng.setText("");
				tv_vocaword_kor.setText("");
				break;

			default:
				break;
			}
			return true;
		}
	};
}
