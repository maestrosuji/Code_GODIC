package com.godic.d_ui.c_list;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.go.dic.R;
import com.go.dic.R.layout;
import com.godic.c_data.b_list.ListDAO;

public class ListActivity extends AppCompatActivity{
	
	RecyclerView mainRecyclerView;
	RecyclerView.Adapter mainAdapter, editAdapter;
	RecyclerView.LayoutManager mainLayoutManager;
	Toolbar tb_list;
	ListDAO listDAO = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.list_main);
		listDAO = ListDAO.getInstance();
		listDAO.dbInit(this);
		mainRecyclerView = (RecyclerView) findViewById(R.id.list_rv);
		mainRecyclerView.setHasFixedSize(true);
		mainLayoutManager = new LinearLayoutManager(this);
		mainRecyclerView.setLayoutManager(mainLayoutManager);
		mainAdapter = new ListAdapter(this, listDAO.getCursor());
		mainRecyclerView.setAdapter(mainAdapter);
		
		tb_list = (Toolbar) findViewById(R.id.list_toolbar);
		setSupportActionBar(tb_list);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.list_menu_edit:
			editAdapter = new ListEditAdapter();
			mainRecyclerView.setAdapter(editAdapter);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
