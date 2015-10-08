package com.godic.d_ui.e_bookmark;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.go.dic.R;

public class BookmarkRead extends Fragment {

	Context mContext;
	View view;
	Toolbar toolbar;
	RecyclerView rv_words;
	RecyclerView.Adapter mainAdapter;
	RecyclerView.LayoutManager layoutManager;

	public BookmarkRead(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.bookmark_read, container, false);

		rv_words = (RecyclerView) view.findViewById(R.id.item_rv);
		rv_words.setHasFixedSize(true);

		layoutManager = new LinearLayoutManager(mContext);
		rv_words.setLayoutManager(layoutManager);
		mainAdapter = new BookmarkReadAdapter(mContext);
		rv_words.setAdapter(mainAdapter);

		return view;
	}

	@Override
	public void onResume() {
		Log.e("BookmarkRead", "onResume");
		super.onResume();
		mainAdapter.notifyDataSetChanged();
	}
}
