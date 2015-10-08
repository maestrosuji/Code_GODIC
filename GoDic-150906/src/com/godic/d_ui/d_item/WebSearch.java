package com.godic.d_ui.d_item;

import com.go.dic.R;
import com.godic.d_ui.a_start.StartActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebSearch extends AppCompatActivity {

	Toolbar tb_list;

	private String url = "http://endic.naver.com/search.nhn?sLn=kr&searchOption=all&query=";

	// private TextView word, mEditText;
	private WebView mWebView;
	private WebSettings mWebSettings;
	private ProgressBar mProgressBar;
	private InputMethodManager mInputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.websearch);

		mWebView = (WebView) findViewById(R.id.webview);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mWebView.setWebChromeClient(new webViewChrome());
		mWebView.setWebViewClient(new webViewClient());
		mWebSettings = mWebView.getSettings();
		mWebSettings.setBuiltInZoomControls(true);

		tb_list = (Toolbar) findViewById(R.id.web_toolbar);
		setSupportActionBar(tb_list);
		tb_list.setNavigationIcon(R.drawable.ic_action_back);
		tb_list.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		String word = StartActivity.value;
		
		mWebView.loadUrl(url+word);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if (mWebView.canGoBack()) {

				mWebView.goBack();

			} else {

				mWebView.clearCache(false);
				Toast.makeText(this, "웹 페이지를 종료합니다.", Toast.LENGTH_SHORT)
						.show();
				this.finish();
				Log.e("WebSearch", "close");

			}

			return true;

		}

		return super.onKeyDown(keyCode, event);

	}

	class webViewChrome extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// 현제 페이지 진행사항을 ProgressBar를 통해 알린다.
			if (newProgress < 100) {
				mProgressBar.setProgress(newProgress);
			} else {
				mProgressBar.setVisibility(View.INVISIBLE);
				mProgressBar
						.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
			}
		}
	}

	class webViewClient extends WebViewClient {

		// Loading이 시작되면 ProgressBar처리를 한다.
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 15));
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			mWebSettings.setJavaScriptEnabled(true);
			// mEditText.setHint(url);
			super.onPageFinished(view, url);
		}
	}
}