package com.godic.d_ui.e_bookmark;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.go.dic.R;
import com.godic.c_data.d_bookmark.BookmarkDAO;

public class BookmarkActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("BoomarkActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.addTab(tabLayout.newTab().setText("Read"));
		tabLayout.addTab(tabLayout.newTab().setText("Test"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

		final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		final SectionsPagerAdapter adapter = new SectionsPagerAdapter(
				getApplicationContext(), getSupportFragmentManager());

		viewPager.setAdapter(adapter);
		viewPager
				.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
						tabLayout));
		tabLayout
				.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
