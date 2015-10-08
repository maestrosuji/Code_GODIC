package com.godic.d_ui.f_setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.go.dic.R;
import com.godic.c_data.a_scan.ScanData;

public class SettingActivity extends AppCompatActivity {

	Toolbar toolbar;
	TextView tv_init, tv_webDicSelect, tv_scanWordLength, tv_themeChange,
			tv_wordSize, tv_picOnOff;
	LinearLayout llo_init, llo_picOnOff, llo_scanWordLength, llo_themeChange,
			llo_wordDicSelect, llo_wordSize;
	LayoutInflater inflater;
	RelativeLayout.LayoutParams rloParams;
	LinearLayout.LayoutParams lloParams;
	boolean flag_init, flag_picOnOff, flag_scanWordLength, flag_themeChange,
			flag_wordDicSelect, flag_wordSize;
	SharedPreferences pref;
	SharedPreferences.Editor prefEditor;
	public static final String PREF_KEY_WORD_LENGTH = "scanWordLength";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_main);

		toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_action_back);
		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		llo_init = (LinearLayout) findViewById(R.id.setting_llo_init);
		llo_picOnOff = (LinearLayout) findViewById(R.id.setting_llo_picOnOff);
		llo_scanWordLength = (LinearLayout) findViewById(R.id.setting_llo_scanWordLength);
		llo_themeChange = (LinearLayout) findViewById(R.id.setting_llo_themeChange);
		llo_wordDicSelect = (LinearLayout) findViewById(R.id.setting_llo_webDicSelect);
		llo_wordSize = (LinearLayout) findViewById(R.id.setting_llo_wordSize);

		tv_init = (TextView) findViewById(R.id.setting_tv_init);
		tv_picOnOff = (TextView) findViewById(R.id.setting_tv_picOnOff);
		tv_scanWordLength = (TextView) findViewById(R.id.setting_tv_scanWordLength);
		tv_themeChange = (TextView) findViewById(R.id.setting_tv_themeChange);
		tv_webDicSelect = (TextView) findViewById(R.id.setting_tv_webDicSelect);
		tv_wordSize = (TextView) findViewById(R.id.setting_tv_wordSize);

		tv_init.setOnClickListener(tvClickListener);
		tv_picOnOff.setOnClickListener(tvClickListener);
		tv_scanWordLength.setOnClickListener(tvClickListener);
		tv_themeChange.setOnClickListener(tvClickListener);
		tv_webDicSelect.setOnClickListener(tvClickListener);
		tv_wordSize.setOnClickListener(tvClickListener);

		inflater = LayoutInflater.from(getApplicationContext());
		rloParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lloParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		initState();
		pref = getSharedPreferences(getPackageName(), Activity.MODE_PRIVATE);
		prefEditor = pref.edit();
		

	}

	public void initState() {
		flag_init = false;
		flag_picOnOff = false;
		flag_scanWordLength = false;
		flag_themeChange = false;
		flag_wordDicSelect = false;
		flag_wordSize = false;
	}

	OnClickListener tvClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_tv_init:
				if (!flag_init) {
					View view = inflater.inflate(R.layout.setting_item_init,
							null, false);
					llo_init.addView(view, rloParams);
					flag_init = true;
				} else {
					llo_init.removeViewAt(1);
					flag_init = false;
				}
				break;
			case R.id.setting_tv_picOnOff:
				if (!flag_picOnOff) {
					View view = inflater.inflate(
							R.layout.setting_item_piconoff, null, false);
					llo_picOnOff.addView(view, rloParams);
					flag_picOnOff = true;
				} else {
					llo_picOnOff.removeViewAt(1);
					flag_picOnOff = false;
				}
				break;
			case R.id.setting_tv_scanWordLength:
				addViewScanWordLength();
				break;
			case R.id.setting_tv_themeChange:
				if (!flag_themeChange) {
					View view = inflater.inflate(
							R.layout.setting_item_themechange, null, false);
					llo_themeChange.addView(view, rloParams);
					flag_themeChange = true;
				} else {
					llo_themeChange.removeViewAt(1);
					flag_themeChange = false;
				}
				break;
			case R.id.setting_tv_webDicSelect:
				if (!flag_wordDicSelect) {
					View view = inflater.inflate(
							R.layout.setting_item_worddicselect, null, false);
					llo_wordDicSelect.addView(view, rloParams);
					flag_wordDicSelect = true;
				} else {
					llo_wordDicSelect.removeViewAt(1);
					flag_wordDicSelect = false;
				}
				break;
			case R.id.setting_tv_wordSize:
				if (!flag_wordSize) {
					View view = inflater.inflate(
							R.layout.setting_item_wordsize, null, false);
					llo_wordSize.addView(view, rloParams);
					flag_wordSize = true;
				} else {
					llo_wordSize.removeViewAt(1);
					flag_wordSize = false;
				}
				break;
			default:
				break;
			}
		}
	};

	private void addViewScanWordLength() {
		if (!flag_scanWordLength) {
			View view = inflater.inflate(R.layout.setting_item_scanwordlenth,
					null, false);
			final TextView tv_displayNumber = (TextView) view
					.findViewById(R.id.settingitem_wordscanlength_tv_displaynumber);
			Button bt_increase = (Button) view
					.findViewById(R.id.settingitem_wordscanlength_bt_increase);
			Button bt_decrease = (Button) view
					.findViewById(R.id.settingitem_wordscanlength_bt_decrease);
			
			tv_displayNumber.setText(ScanData.getInstance().scanWordLength+"");
			
			OnClickListener btClickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					int n;
					switch (v.getId()) {
					case R.id.settingitem_wordscanlength_bt_increase:
						n = Integer.parseInt(tv_displayNumber.getText()
								.toString());
						if (n < 10) {
							tv_displayNumber.setText((n + 1) + "");
							prefEditor.remove(PREF_KEY_WORD_LENGTH);
							prefEditor.putInt(PREF_KEY_WORD_LENGTH, n + 1);
						}
						break;

					case R.id.settingitem_wordscanlength_bt_decrease:
						n = Integer.parseInt(tv_displayNumber.getText()
								.toString());
						if (n > 1) {
							tv_displayNumber.setText((n - 1) + "");
							prefEditor.remove(PREF_KEY_WORD_LENGTH);
							prefEditor.putInt(PREF_KEY_WORD_LENGTH, n - 1);
							
						}
						break;
					default:
						break;
					}
				}
			};

			bt_increase.setOnClickListener(btClickListener);
			bt_decrease.setOnClickListener(btClickListener);

			llo_scanWordLength.addView(view, rloParams);
			flag_scanWordLength = true;
		} else {
			llo_scanWordLength.removeViewAt(1);
			flag_scanWordLength = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		prefEditor.commit();
		ScanData.getInstance().scanWordLength = pref.getInt(PREF_KEY_WORD_LENGTH, 3);
	}
}
