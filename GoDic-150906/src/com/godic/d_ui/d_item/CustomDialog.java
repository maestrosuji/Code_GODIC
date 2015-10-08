package com.godic.d_ui.d_item;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.go.dic.R;
import com.go.dic.R.id;

public class CustomDialog extends Dialog {
	TextView tv_custom_eng, tv_custom_kor;
	Button bt_web, bt_bookmark, bt_close;

	public CustomDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.item_customdialog);
		
		tv_custom_eng = (TextView) findViewById(id.item_custom_tv_eng);
		tv_custom_kor = (TextView) findViewById(id.item_custom_tv_kor);
		bt_web = (Button) findViewById(id.item_madapt_bt_web);
		bt_bookmark = (Button) findViewById(id.item_madapt_bt_bookmark);
		bt_close = (Button) findViewById(id.item_madapt_bt_close);
	}

	public void clickBtWeblistener(View.OnClickListener clickBtWeblistener) {
		bt_web.setOnClickListener(clickBtWeblistener);
	}

	public void clickBtBookmarklistener(View.OnClickListener clickBtBookmarklistener) {
		bt_bookmark.setOnClickListener(clickBtBookmarklistener);
	}
	public void clickBtCloselistener(View.OnClickListener clickBtCloselistener) {
		bt_close.setOnClickListener(clickBtCloselistener);
	}
}
