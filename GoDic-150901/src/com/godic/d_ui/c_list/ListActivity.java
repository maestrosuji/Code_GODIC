package com.godic.d_ui.c_list;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.go.dic.R;
import com.go.dic.R.id;
import com.go.dic.R.layout;
import com.godic.c_data.b_list.ListDAO;
import com.godic.d_ui.b_scan.ScanActivity;
import com.godic.d_ui.e_bookmark.BookmarkActivity;

public class ListActivity extends AppCompatActivity {

	RecyclerView mainRecyclerView;
	RecyclerView.Adapter mainAdapter;
	RecyclerView.LayoutManager mainLayoutManager;
	Toolbar tb_list;
	ListDAO listDAO = null;

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
		Log.e("ListActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(layout.list_main);
		listDAO = ListDAO.getInstance();
		listDAO.dbInit(this);
		mainRecyclerView = (RecyclerView) findViewById(R.id.list_rv);
		mainRecyclerView.setHasFixedSize(true);
		mainLayoutManager = new LinearLayoutManager(this);
		mainRecyclerView.setLayoutManager(mainLayoutManager);
		mainAdapter = new ListAdapter(this, this);
		mainRecyclerView.setAdapter(mainAdapter);

		tb_list = (Toolbar) findViewById(R.id.list_toolbar);
		setSupportActionBar(tb_list);
		tb_list.setNavigationIcon(R.drawable.ic_action_back);
		tb_list.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
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

		/*
		 * fabButton = (ImageButton) findViewById(R.id.fab);
		 * fabButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new
		 * Intent(ListActivity.this, ScanActivity.class); finish();
		 * startActivity(intent); } });
		 */

		tb_list = (Toolbar) findViewById(R.id.list_toolbar);
		setSupportActionBar(tb_list);
		tb_list.setNavigationIcon(R.drawable.ic_action_back);
		tb_list.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	protected void onResume() {
		Log.e("ListActivity", "onResume");
		super.onResume();
		listDAO.dbInit(this);
		listDAO.openCursor();
		mainAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		Log.e("ListActivity", "onPause");
		super.onPause();
		listDAO.dbDeInit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bookmark_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Bookmark_menu_Mark:
			Intent intent = new Intent(this, BookmarkActivity.class);
			this.startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void adapterViewUpdate(){
		listDAO.dbInit(getApplicationContext());
		listDAO.openCursor();
		mainAdapter = new ListAdapter(this, this);
		mainRecyclerView.setAdapter(mainAdapter);
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
		//fab.setImageResource(R.drawable.animated_minus);
		fab.setImageResource(R.drawable.ic_settings);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
				createCollapseAnimator(fabAction2, offset2),
				createCollapseAnimator(fabAction3, offset3));
		animatorSet.start();
		animateFab();
	}

	private void expandFab() {
		//fab.setImageResource(R.drawable.animated_plus);
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
