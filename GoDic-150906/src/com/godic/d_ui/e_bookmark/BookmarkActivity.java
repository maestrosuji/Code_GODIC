package com.godic.d_ui.e_bookmark;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.go.dic.R;
import com.go.dic.R.id;
import com.godic.c_data.d_bookmark.BookmarkDAO;

public class BookmarkActivity extends AppCompatActivity {

	ViewPager viewPager;
	SectionsPagerAdapter adapter;
	Toolbar toolbar;
	BookmarkDAO bmkDAO;

	private ImageButton fab;
	private static final String TAG = "Floating Action Button";
	private static final String TRANSLATION_Y = "translationY";

	private boolean expanded = false;

	private View fabAction1;
	private View fabAction2;
	private View fabAction3;

	private float offset1;
	private float offset2;
	private float offset3;

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
		adapter = new SectionsPagerAdapter(getApplicationContext(),
				getSupportFragmentManager());
		viewPager.setAdapter(adapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.addTab(tabLayout.newTab().setText("Read"));
		tabLayout.addTab(tabLayout.newTab().setText("Test"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

		viewPager
				.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
						tabLayout) {
					@Override
					public void onPageSelected(int position) {
						// TODO Auto-generated method stub
						super.onPageSelected(position);
						Fragment fragment = ((SectionsPagerAdapter) viewPager
								.getAdapter()).getFragment(position);
						Log.e("@@@currentPage", position + "");
						if (position == 1 && fragment != null) {
							fragment.onResume();
						}
					}
				});
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

		final ViewGroup fabContainer = (ViewGroup) findViewById(R.id.fab_container);
		fab = (ImageButton) findViewById(R.id.fab);
		fabAction1 = findViewById(R.id.fab_action_1);
		fabAction2 = findViewById(R.id.fab_action_2);
		fabAction3 = findViewById(R.id.fab_action_3);
		fab.setOnClickListener(btClickListener);
		fabAction1.setOnClickListener(btClickListener);
		fabAction2.setOnClickListener(btClickListener);
		fabAction3.setOnClickListener(btClickListener);

		fabContainer.getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						fabContainer.getViewTreeObserver()
								.removeOnPreDrawListener(this);
						offset1 = fab.getY() - fabAction1.getY();
						fabAction1.setTranslationY(offset1);
						offset2 = fab.getY() - fabAction2.getY();
						fabAction2.setTranslationY(offset2);
						offset3 = fab.getY() - fabAction3.getY();
						fabAction3.setTranslationY(offset3);
						return true;
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

	private OnClickListener btClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case id.fab:
				expanded = !expanded;
				if (expanded) {
					expandFab();
				} else {
					collapseFab();
				}
				break;

			case id.fab_action_1:
				break;

			case id.fab_action_2:
				break;

			case id.fab_action_3: //
				finish();
			default:
				break;
			}
		}
	};

	private void collapseFab() {
		// fab.setImageResource(R.drawable.animated_minus);
		fab.setImageResource(R.drawable.ic_settings);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
				createCollapseAnimator(fabAction2, offset2),
				createCollapseAnimator(fabAction3, offset3));
		animatorSet.start();
		animateFab();
	}

	private void expandFab() {
		// fab.setImageResource(R.drawable.animated_plus);
		fab.setImageResource(R.drawable.ic_action_undo);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
				createExpandAnimator(fabAction2, offset2),
				createExpandAnimator(fabAction3, offset3));
		animatorSet.start();
		animateFab();
	}

	private Animator createCollapseAnimator(View view, float offset) {
		return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
				.setDuration(
						getResources().getInteger(
								android.R.integer.config_mediumAnimTime));
	}

	private Animator createExpandAnimator(View view, float offset) {
		return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
				.setDuration(
						getResources().getInteger(
								android.R.integer.config_mediumAnimTime));
	}

	private void animateFab() {
		Drawable drawable = fab.getDrawable();
		if (drawable instanceof Animatable) {
			((Animatable) drawable).start();
		}
	}
}
