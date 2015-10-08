package com.godic.d_ui.a_start;

import java.util.ArrayList;

import com.go.dic.R;
import com.go.dic.R.id;
import com.go.dic.R.layout;
import com.godic.d_ui.b_scan.ScanActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

public class GuideActivity extends AppCompatActivity {

	private ViewPager mPager;
	private ArrayList<View> pageViews; // pages view
	private ImageView imageView; // image => small white and black point
	private ImageView[] imageViews;
	private ViewGroup viewPics; // layout for view group
	private ViewGroup viewPoints; // layout for small points
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// save view into array
		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(layout.guide_view1, null));
		pageViews.add(inflater.inflate(layout.guide_view2, null));
		pageViews.add(inflater.inflate(layout.guide_view3, null));

		// save small points into array
		imageViews = new ImageView[pageViews.size()];
		viewPics = (ViewGroup) inflater.inflate(layout.guide_main, null);

		viewPoints = (ViewGroup) viewPics.findViewById(id.viewGroup);
		mPager = (ViewPager) viewPics.findViewById(id.pager);

		// set images
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(GuideActivity.this);
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			imageViews[i] = imageView;

			if (i == 0) {
				imageViews[i].setBackgroundResource(R.drawable.circle_white);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.circle_grey);
			}

			viewPoints.addView(imageViews[i]);
		}

		setContentView(viewPics);

		// set page adapter and listener
		mPager.setAdapter(new GuidePageAdapter(pageViews, this));
		mPager.setOnPageChangeListener(new GuidePageChangeListener(imageViews));
		
	}
	
	public void BtClick(View v) {
		int position;
		
		switch (v.getId()) {
		case id.bt_previous:
			position = mPager.getCurrentItem();
			mPager.setCurrentItem(position-1, true);
			break;
			
		case id.bt_next:
			position = mPager.getCurrentItem();
			mPager.setCurrentItem(position+1, true);
		default:
			break;
		}
	}

	private View.OnClickListener clickBtClose = new OnClickListener() {

		@Override
		public void onClick(View v) {

			finish();

			Intent intent = new Intent(GuideActivity.this, ScanActivity.class);
			startActivity(intent);

		}
	};

	private View.OnClickListener clickBtNotShow = new OnClickListener() {

		@Override
		public void onClick(View v) {

			int infoFirst = 1;
			SharedPreferences pref = getSharedPreferences(getPackageName(), Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt("guideShow", infoFirst);
			editor.commit();
			finish();

			Intent intent = new Intent(GuideActivity.this, ScanActivity.class);
			startActivity(intent);

		}
	};

	public class GuidePageAdapter extends PagerAdapter {

		private ArrayList<View> pageViews;
		private Context mContext;
		private Button bt_close, bt_notShow;

		public GuidePageAdapter(ArrayList<View> pageViews, Context mContext) {
			super();
			this.pageViews = pageViews;
			this.mContext = mContext;
		}

		@Override
		public Object instantiateItem(View v, int position) {
			switch (position) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				bt_close = (Button) pageViews.get(position).findViewById(
						id.bt_close);
				bt_close.setOnClickListener(clickBtClose);
				bt_notShow = (Button) pageViews.get(position).findViewById(
						id.bt_notShow);
				bt_notShow.setOnClickListener(clickBtNotShow);
				
				break;
			default:
				break;
			}

			((ViewPager) v).addView(pageViews.get(position));
			return pageViews.get(position);
		}

		@Override
		public void destroyItem(View v, int position, Object object) {
			((ViewPager) v).removeView(pageViews.get(position));
		}

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			return v == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

	}

	public class GuidePageChangeListener implements OnPageChangeListener {

		private ImageView[] imageViews;

		public GuidePageChangeListener(ImageView[] imageViews) {
			super();
			this.imageViews = imageViews;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[position]
						.setBackgroundResource(R.drawable.circle_white);
				if (position != i) {
					imageViews[i].setBackgroundResource(R.drawable.circle_grey);
				}
			}
		}

	}

}
