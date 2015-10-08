package com.godic.d_ui.e_bookmark;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.go.dic.R;
import com.godic.c_data.c_item.ItemData;

public class BookmarkActivity extends AppCompatActivity{

	Toolbar toolbar;
	RecyclerView rv_words;
	RecyclerView.Adapter mainAdapter;
	RecyclerView.LayoutManager layoutManager;
	ItemData itemData = ItemData.getInstance();
	BookmarkDAO bmkDAO = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_main);
		
		
		toolbar = (Toolbar) findViewById(R.id.item_toolbar);
		setSupportActionBar(toolbar);		
		rv_words = (RecyclerView) findViewById(R.id.item_rv);
		rv_words.setHasFixedSize(true);
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("@@@onResume","BookmarkA onResume");
		bmkDAO = BookmarkDAO.getInstance();
		bmkDAO.bmkInit(this);
		layoutManager = new LinearLayoutManager(this);
		rv_words.setLayoutManager(layoutManager);
		mainAdapter = new BookmarkAdapter(this,bmkDAO.getCursor());
		rv_words.setAdapter(mainAdapter);
	}
}
