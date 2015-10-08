package com.godic.d_ui.e_bookmark;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.go.dic.R;
import com.godic.c_data.d_bookmark.BookmarkDAO;

public class BookmarkActivity extends AppCompatActivity {

	ViewPager viewPager;
	SectionsPagerAdapter adapter;
	Toolbar toolbar;
	BookmarkDAO bmkDAO;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("BoomarkActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_main);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_action_back);
		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bmkDAO = BookmarkDAO.getInstance();
		viewPager = (ViewPager) findViewById(R.id.pager);
		adapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.addTab(tabLayout.newTab().setText("Read"));
		tabLayout.addTab(tabLayout.newTab().setText("Test"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
		
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout){
			@Override
        	public void onPageSelected(int position) {
        		// TODO Auto-generated method stub
        		super.onPageSelected(position);
        		Fragment fragment = ((SectionsPagerAdapter)viewPager.getAdapter()).getFragment(position);
				Log.e("@@@currentPage", position+"");
				if(position == 1 && fragment != null){
					fragment.onResume();
				}
        	}
		});
		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
					@Override
					public void onTabSelected(TabLayout.Tab tab) {
						viewPager.setCurrentItem(tab.getPosition());
					}

					@Override
					public void onTabUnselected(TabLayout.Tab tab) {

					}

					@Override
					public void onTabReselected(TabLayout.Tab tab) {

					}
				});
	}
	
	@Override
	protected void onResume() {
		Log.e("BoomarkActivity", "onResume");
		super.onResume();
		BookmarkDAO.getInstance().dbInit(this);
	}
	
	@Override
	protected void onPause() {
		Log.e("BoomarkActivity", "onPause");
		super.onPause();
		BookmarkDAO.getInstance().dbClose();
	}
	
	@Override
	protected void onDestroy() {
		Log.e("BoomarkActivity", "onDestroy");
		super.onDestroy();
	}
}
