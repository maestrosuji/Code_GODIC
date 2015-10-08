package com.godic.d_ui.d_item;

import com.go.dic.R;
import com.godic.c_data.c_item.ItemData;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity{

	Toolbar toolbar;
	RecyclerView rv_words;
	RecyclerView.Adapter mainAdapter;
	RecyclerView.LayoutManager layoutManager;
	TextView tv_title, tv_date;
	ImageView iv_capture;
	ItemData itemData = ItemData.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_main);
		toolbar = (Toolbar) findViewById(R.id.item_toolbar);
		setSupportActionBar(toolbar);
		
		rv_words = (RecyclerView) findViewById(R.id.item_rv);
		rv_words.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);
		rv_words.setLayoutManager(layoutManager);
		mainAdapter = new ItemAdapter(this);
		rv_words.setAdapter(mainAdapter);
		
		tv_title = (TextView) findViewById(R.id.item_tv_title);
		tv_date = (TextView) findViewById(R.id.item_tv_date);
		iv_capture = (ImageView) findViewById(R.id.item_iv_capture);
		
		tv_title.setText(itemData.title);
		tv_date.setText(itemData.date);
		iv_capture.setImageDrawable(itemData.captureImage);
	}
}
