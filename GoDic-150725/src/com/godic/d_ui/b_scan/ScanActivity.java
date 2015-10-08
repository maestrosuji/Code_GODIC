package com.godic.d_ui.b_scan;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.go.dic.R;
import com.go.dic.R.drawable;
import com.go.dic.R.id;
import com.go.dic.R.layout;
import com.godic.a_proc.AppControl;
import com.godic.a_proc.AppException;
import com.godic.a_proc.AppSession;
import com.godic.b_util.AppGLView;
import com.godic.b_util.AppUtils;
import com.godic.b_util.LoadingDialogHandler;
import com.godic.c_data.a_scan.ScanDAO;
import com.godic.c_data.a_scan.ScanData;
import com.godic.c_data.b_list.ListDAO;
import com.godic.d_ui.c_list.ListActivity;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.RectangleInt;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.STORAGE_TYPE;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.TextTracker;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.WordList;

public class ScanActivity extends AppCompatActivity implements AppControl {
	private static final String LOGTAG = "ScanActivity";
	private AppSession vuforiaAppSession;
	private Toast toast = null;
	private AppGLView mGlView;
	private ScanRender mRenderer;
	private Intent intent;
	private ActionBarDrawerToggle abdToggle;
	private LoadingDialogHandler loadingDialogHandler;
	private ScanData scanData = ScanData.getInstance();
	private ScanDAO scanDAO = ScanDAO.getInstance(this);
	private ListDAO listDAO = ListDAO.getInstance();
	private ScanWordsAdapter wordsAdapter;

	private ListView lv_words;
	private ImageView iv_loupe;
	private ImageView iv_margin_top, iv_margin_bottom;
	private ImageView iv_margin_right, iv_margin_left;
	private VerticalSeekbar vsb;
	private RelativeLayout rlo_main, rlo_center, rlo_vsb, rlo_whole;
	private FrameLayout flo_control;
	private RelativeLayout rlo_control;
	private LinearLayout llo_control_check;
	private DrawerLayout dl_main;
	private Button bt_shot, bt_save, bt_cancel, bt_flash;
	private Toolbar toolbar;

	private Spinner option_length, option_dictionary;
	private ArrayList<String> mLengthList, mDictionaryList;
	private ArrayAdapter<String> mLengthAdapter, mDictionaryAdapter;
	private TextView option_question, option_help;

	private int loupeWidth, loupeHeight;
	private int marginWidth, marginHeight;
	private int screenWidth, screenHeight;
	private int vsbWidth;
	private int topViewHeight;
	private int statusBarHeight;
	private int titleBarHeight;
	private int loupeMaxHeight;
	private float vsbProgressUnit;

	private boolean mIsVuforiaStarted = false;
	private boolean mFlash = false;
	private boolean mIsDroidDevice = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("ScanActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(layout.scan_main);

		vuforiaAppSession = new AppSession(this);
		vuforiaAppSession
				.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith(
				"droid");

		// scanDAO.dbCopy();
		listDAO.dbInit(this);

		dl_main = (DrawerLayout) findViewById(id.scan_dl_main);
		toolbar = (Toolbar) findViewById(id.scan_toolbar);
		setSupportActionBar(toolbar);
		abdToggle = new ActionBarDrawerToggle(this, dl_main, toolbar,
				R.string.open, R.string.close);
		dl_main.setDrawerListener(abdToggle);

		lv_words = (ListView) findViewById(id.scan_lv_words);
		wordsAdapter = new ScanWordsAdapter(this);
		lv_words.setAdapter(wordsAdapter);

		iv_loupe = (ImageView) findViewById(id.scan_loupe);
		iv_loupe.setOnTouchListener(loupeTouchListener);
		iv_margin_bottom = (ImageView) findViewById(id.scan_margin_bottom);
		iv_margin_left = (ImageView) findViewById(id.scan_margin_left);
		iv_margin_top = (ImageView) findViewById(id.scan_margin_top);
		iv_margin_right = (ImageView) findViewById(id.scan_margin_right);

		vsb = (VerticalSeekbar) findViewById(id.scan_vsb);
		vsb.setOnSeekBarChangeListener(vsbChangeListener);

		rlo_whole = (RelativeLayout) findViewById(id.scan_rlo_whole);
		rlo_main = (RelativeLayout) findViewById(id.scan_rlo_main);
		rlo_center = (RelativeLayout) findViewById(id.scan_rlo_center);
		rlo_vsb = (RelativeLayout) findViewById(id.scan_rlo_vsb);
		rlo_control = (RelativeLayout) findViewById(id.scan_rlo_control);
		llo_control_check = (LinearLayout) findViewById(id.scan_llo_control_check);
		flo_control = (FrameLayout) findViewById(id.scan_flo_control);

		bt_shot = (Button) findViewById(id.scan_bt_shot);
		bt_save = (Button) findViewById(id.scan_bt_save);
		bt_cancel = (Button) findViewById(id.scan_bt_cancel);
		bt_flash = (Button) findViewById(id.scan_bt_flash);
		bt_shot.setOnClickListener(btClickListener);
		bt_save.setOnClickListener(btClickListener);
		bt_cancel.setOnClickListener(btClickListener);
		bt_flash.setOnClickListener(btClickListener);

		rlo_whole.post(new Runnable() {
			@Override
			public void run() {
				getScreenPixels();
				getMarginSize();
				getBarSize();
				getProgressCalc();
				setReadyDrawLoupe();
			}
		});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		abdToggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scan_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (abdToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case id.scan_menu_forward:
			Intent intent = new Intent(ScanActivity.this, ListActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getScreenPixels() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;

		Log.e("screen width, height", screenWidth + ", " + screenHeight);
	}

	private void getMarginSize() {
		marginWidth = screenWidth / 60;
		marginHeight = screenHeight / 80;
		vsbWidth = vsb.getWidth();
		loupeWidth = screenWidth - 2 * marginWidth - vsbWidth;

		Log.e("loupe width, height", loupeWidth + ", " + loupeHeight);
		Log.e("margin width, height", marginWidth + "," + marginHeight);
		Log.e("vsb width", vsbWidth + "");
	}

	private void getBarSize() {
		Rect rectgle = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		statusBarHeight = rectgle.top;
		topViewHeight = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		titleBarHeight = this.getSupportActionBar().getHeight();

		Log.e("getHeight", "StatusBar Height= " + statusBarHeight
				+ " TitleBar Height = " + titleBarHeight);
	}

	private void getProgressCalc() {
		loupeMaxHeight = screenHeight - 2 * marginHeight - statusBarHeight
				- titleBarHeight - flo_control.getHeight();
		vsbProgressUnit = (float) loupeMaxHeight / vsb.getMax();
		loupeHeight = (int) (vsbProgressUnit * vsb.getProgress());
	}

	private void setReadyDrawLoupe() {
		iv_margin_top.getLayoutParams().height = marginHeight;
		iv_margin_bottom.getLayoutParams().height = marginHeight;
		iv_margin_left.getLayoutParams().width = marginWidth;
		iv_margin_left.getLayoutParams().height = loupeHeight;
		iv_margin_right.getLayoutParams().width = marginWidth;
		iv_margin_right.getLayoutParams().height = loupeHeight;
		rlo_center.getLayoutParams().height = loupeHeight;
	}

	@Override
	protected void onResume() {
		Log.e("ScanActivity", "onResume");
		super.onResume();

		if (mIsDroidDevice) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		try {
			vuforiaAppSession.resumeAR();

		} catch (AppException e) {
			Log.e(LOGTAG, e.getString());
		}

		if (mIsVuforiaStarted)
			postStartCamera();

		// Resume the GL view:
		if (mGlView != null) {
			mGlView.setVisibility(View.VISIBLE);
			mGlView.onResume();
		}

		initState();
	}

	private void initState() {
		wordsAdapter.notifyDataSetChanged();
		scanDAO.dbOpen();
		rlo_control.setVisibility(View.VISIBLE);
		llo_control_check.setVisibility(View.INVISIBLE);
		mFlash = false;
		bt_flash.setBackgroundResource(drawable.ic_action_flash_off);
	}

	@Override
	protected void onPause() {
		Log.e(LOGTAG, "onPause");
		super.onPause();

		if (mGlView != null) {
			mGlView.setVisibility(View.INVISIBLE);
			mGlView.onPause();
		}

		try {
			vuforiaAppSession.pauseAR();
		} catch (AppException e) {
			Log.e(LOGTAG, e.getString());
		}

		scanDAO.dbClose();
		stopCamera();
	}

	@Override
	protected void onDestroy() {
		Log.e(LOGTAG, "onDestroy");
		super.onDestroy();

		try {
			vuforiaAppSession.stopAR();
		} catch (AppException e) {
			Log.e(LOGTAG, e.getString());
		}

		System.gc();
	}

	@Override
	public boolean doInitTrackers() {
		TrackerManager tManager = TrackerManager.getInstance();
		Tracker tracker;

		// Indicate if the trackers were initialized correctly
		boolean result = true;

		tracker = tManager.initTracker(TextTracker.getClassType());
		if (tracker == null) {
			Log.e(LOGTAG,
					"Tracker not initialized. Tracker already initialized or the camera is already started");
			result = false;
		} else {
			Log.i(LOGTAG, "Tracker successfully initialized");
		}

		return result;
	}

	// Functions to load and destroy tracking data.
	@Override
	public boolean doLoadTrackersData() {
		TrackerManager tm = TrackerManager.getInstance();
		TextTracker tt = (TextTracker) tm
				.getTracker(TextTracker.getClassType());
		WordList wl = tt.getWordList();

		return wl.loadWordList("TextReco/Vuforia-English-word.vwl",
				STORAGE_TYPE.STORAGE_APPRESOURCE);
	}

	@Override
	public boolean doStartTrackers() {
		// Indicate if the trackers were started correctly
		boolean result = true;

		Tracker textTracker = TrackerManager.getInstance().getTracker(
				TextTracker.getClassType());
		if (textTracker != null)
			textTracker.start();

		return result;
	}

	@Override
	public boolean doStopTrackers() {
		// Indicate if the trackers were stopped correctly
		boolean result = true;

		Tracker textTracker = TrackerManager.getInstance().getTracker(
				TextTracker.getClassType());
		if (textTracker != null)
			textTracker.stop();

		return result;
	}

	@Override
	public boolean doUnloadTrackersData() {
		// Indicate if the trackers were unloaded correctly
		boolean result = true;
		TrackerManager tm = TrackerManager.getInstance();
		TextTracker tt = (TextTracker) tm
				.getTracker(TextTracker.getClassType());
		WordList wl = tt.getWordList();
		wl.unloadAllLists();

		return result;
	}

	@Override
	public boolean doDeinitTrackers() {
		// Indicate if the trackers were deinitialized correctly
		boolean result = true;
		Log.e(LOGTAG, "UnloadTrackersData");

		TrackerManager tManager = TrackerManager.getInstance();
		tManager.deinitTracker(TextTracker.getClassType());

		return result;
	}

	@Override
	public void onInitARDone(AppException exception) {
		if (exception == null) {
			initApplicationAR();

			// Hint to the virtual machine that it would be a good time to
			// run the garbage collector:
			//
			// NOTE: This is only a hint. There is no guarantee that the
			// garbage collector will actually be run.
			System.gc();

			// Activate the renderer:
			mRenderer.mIsActive = true;
			Log.e("ScanActivity", "onInitARDone");
			// Now add the GL surface view. It is important
			// that the OpenGL ES surface view gets added
			// BEFORE the camera is started and video
			// background is configured.
			addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			// Hides the Loading Dialog
			// loadingDialogHandler
			// .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
			showLoupe(true);

			// Sets the UILayout to be drawn in front of the camera
			rlo_whole.bringToFront();// dl

			try {
				vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
			} catch (AppException e) {
				Log.e(LOGTAG, e.getString());
			}

			mIsVuforiaStarted = true;

			postStartCamera();

		} else {
			Log.e(LOGTAG, exception.getString());
			finish();
		}

	}

	@Override
	public void onQCARUpdate(State state) {

	}

	// Callback for configuration changes the activity handles itself
	@Override
	public void onConfigurationChanged(Configuration config) {
		Log.d(LOGTAG, "onConfigurationChanged");
		super.onConfigurationChanged(config);

		vuforiaAppSession.onConfigurationChanged();

		if (mIsVuforiaStarted)
			configureVideoBackgroundROI();

		// Pass any configuration change to the drawer toggls
		abdToggle.onConfigurationChanged(config);
	}

	private final Handler autofocusHandler = new Handler();
	OnTouchListener loupeTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			autofocusHandler.post(new Runnable() {
				public void run() {
					boolean result = CameraDevice.getInstance().setFocusMode(
							CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

					if (!result)
						Log.e("SingleTapUp", "Unable to trigger focus");
				}
			});

			return false;
		}
	};

	// Initializes AR application components.
	private void initApplicationAR() {
		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = Vuforia.requiresAlpha();

		mGlView = new AppGLView(this);
		mGlView.init(translucent, depthSize, stencilSize);

		mRenderer = new ScanRender(this, vuforiaAppSession);
		mGlView.setRenderer(mRenderer);

		showLoupe(false);

	}

	private void postStartCamera() {
		// Sets the layout background to transparent
		rlo_whole.setBackgroundColor(Color.TRANSPARENT);

		// start the image tracker now that the camera is started
		Tracker t = TrackerManager.getInstance().getTracker(
				TextTracker.getClassType());
		if (t != null)
			t.start();

		configureVideoBackgroundROI();
	}

	void configureVideoBackgroundROI() {
		VideoMode vm = CameraDevice.getInstance().getVideoMode(
				CameraDevice.MODE.MODE_DEFAULT);
		VideoBackgroundConfig config = Renderer.getInstance()
				.getVideoBackgroundConfig();

		mRenderer.setROI(marginWidth + loupeWidth / 2, marginHeight
				+ loupeHeight / 2 + statusBarHeight / 2 + titleBarHeight,
				loupeWidth, loupeHeight);
		// Log.e("confROI ROI", (marginWidth + loupeWidth / 2) + ", "
		// + (marginHeight + loupeHeight / 2 + topViewHeight / 2) + ", "
		// + loupeWidth + ", " + loupeHeight);

		// convert into camera coords
		int[] loupeCenterX = { 0 };
		int[] loupeCenterY = { 0 };
		int[] loupeWidth = { 0 };
		int[] loupeHeight = { 0 };
		AppUtils.screenCoordToCameraCoord((int) mRenderer.ROICenterX,
				(int) mRenderer.ROICenterY, (int) mRenderer.ROIWidth,
				(int) mRenderer.ROIHeight, screenWidth, screenHeight,
				vm.getWidth(), vm.getHeight(), loupeCenterX, loupeCenterY,
				loupeWidth, loupeHeight);

		RectangleInt detROI = new RectangleInt(loupeCenterX[0]
				- (loupeWidth[0] / 2), loupeCenterY[0] - (loupeHeight[0] / 2),
				loupeCenterX[0] + (loupeWidth[0] / 2), loupeCenterY[0]
						+ (loupeHeight[0] / 2));

		TextTracker tt = (TextTracker) TrackerManager.getInstance().getTracker(
				TextTracker.getClassType());
		if (tt != null)
			tt.setRegionOfInterest(detROI, detROI,
					TextTracker.UP_DIRECTION.REGIONOFINTEREST_UP_IS_9_HRS);

		int[] size = config.getSize().getData();
		int[] pos = config.getPosition().getData();
		int offx = ((screenWidth - size[0]) / 2) + pos[0];
		int offy = ((screenHeight - size[1]) / 2) + pos[1];
		mRenderer.setViewport(offx, offy, size[0], size[1]);

		// Log.e("ScanActivity", "ConfigureROI");
	}

	private void stopCamera() {
		doStopTrackers();

		CameraDevice.getInstance().stop();
		CameraDevice.getInstance().deinit();
	}

	// SubTask subTask = new SubTask();
	void updateSubWordListUI(int position) {
		synchronized (scanData) {
			new SubTask().execute(position);
		}
	}

	void updateAddWordListUI(String engword) {
		synchronized (scanData) {
			new AddTask().execute(engword);
		}
	}

	private class SubTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... position) {
//			while (!scanData.wordInfoList.get(position[0]).) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
			scanData.remove(position[0]);
			uiHandler.sendEmptyMessage(0);
			return null;
		}

	}

	private class AddTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... engword) {
			if (engword[0] == null) {
				uiHandler.sendEmptyMessage(0);
			} else {
				String korword = scanDAO.wordSelect(engword[0]);
				scanData.addWord(engword[0], korword);
				uiHandler.sendEmptyMessage(0);
			}
			return null;
		}
	}

	private Handler uiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			wordsAdapter.notifyDataSetChanged();
//			lv_words.setSelection(wordsAdapter.getCount() - 1);
			showToast("단어개수: " + wordsAdapter.getCount());
		}
	};

	private void showLoupe(boolean isActive) {

		// Gets a reference to the loading dialog
		View loadingIndicator = rlo_whole.findViewById(R.id.scan_pb_loading);

		if (isActive) {
			loadingIndicator.setVisibility(View.GONE);
			Log.e("ScanActivity", "ShowLoupe - true");
		} else {
			loadingIndicator.setVisibility(View.VISIBLE);
			Log.e("ScanActivity", "ShowLoupe - false");
		}

	}

	private OnSeekBarChangeListener vsbChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// Log.e("seek bar progress", progress + "");

			loupeHeight = (int) (vsbProgressUnit * progress);

			LayoutParams params = rlo_center.getLayoutParams();
			params.height = loupeHeight;
			rlo_center.setLayoutParams(params);
			iv_margin_left.getLayoutParams().height = loupeHeight;
			iv_margin_right.getLayoutParams().height = loupeHeight;

			configureVideoBackgroundROI();
		}
	};

	private OnClickListener btClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case id.scan_bt_shot:
				clickBtShot();
				break;

			case id.scan_bt_save:
				clickBtSave();
				break;

			case id.scan_bt_cancel:
				clickBtCancel();
				break;

			case id.scan_bt_flash:
				clickBtFlash();
			default:
				break;
			}
		}
	};

	private void clickBtShot() {

		scanData.bitmapInfo.x = marginWidth;
		scanData.bitmapInfo.y = flo_control.getHeight() + marginHeight
				+ lv_words.getHeight();
		scanData.bitmapInfo.w = loupeWidth;
		scanData.bitmapInfo.h = loupeHeight;

		if (scanData.bitmapInfo.h > 0) {
			rlo_control.setVisibility(View.INVISIBLE);
			llo_control_check.setVisibility(View.VISIBLE);
			CameraDevice.getInstance().stop();
		} else {
			showToast("캡쳐할 화면이 없습니다.");
		}
	}

	private void clickBtSave() {

		// if (scanData.wordList.size() != 0) {
		//
		// } else {
		// showToast("단어를 먼저 스캔하세요.");
		// }

		intent = new Intent(ScanActivity.this, ListActivity.class);
		scanData.bitmapInfo.stateBitmap = true;

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (scanData.bitmapInfo.stateBitmap) {
					try {
						Log.e("state thread", "in thread");
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				listDAO.insert("No Title", scanData.wordInfoList,
						scanData.bitmapInfo.bitmap);
				startActivity(intent);
			}
		}).start();
	}

	private void clickBtCancel() {
		rlo_control.setVisibility(View.VISIBLE);
		llo_control_check.setVisibility(View.INVISIBLE);
		CameraDevice.getInstance().start();
	}

	private void showToast(String str) {
		if (toast == null) {
			toast = Toast.makeText(ScanActivity.this, "", Toast.LENGTH_SHORT);
		}
		toast.show();
		toast.setText(str);
	}

	private void clickBtFlash() {
		if (mFlash) {
			bt_flash.setBackgroundResource(drawable.ic_action_flash_off);
			CameraDevice.getInstance().setFlashTorchMode(false);
			mFlash = false;
		} else {
			bt_flash.setBackgroundResource(drawable.ic_action_flash_on);
			CameraDevice.getInstance().setFlashTorchMode(true);
			mFlash = true;
		}
	}
}
