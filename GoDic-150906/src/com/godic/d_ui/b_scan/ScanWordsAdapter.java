package com.godic.d_ui.b_scan;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.go.dic.R.id;
import com.go.dic.R.layout;
import com.godic.c_data.WordInfo;
import com.godic.c_data.a_scan.ScanData;

public class ScanWordsAdapter extends BaseAdapter{

//	ArrayList<HashMap<String, String>> mapList = ScanData.getInstance().wordMapList;
	ArrayList<WordInfo> infoList = ScanData.getInstance().wordInfoList;
//	Context mContext;
	LayoutInflater inflater;
	
	public ScanWordsAdapter(Context context) {
//		mContext = context;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return infoList.size();
	}

	@Override
	public Object getItem(int position) {
		return infoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		convertView = inflater.inflate(layout.scan_main_adapt, null);
//		TextView tv1 = (TextView) convertView.findViewById(id.scan_adapt_tv1);
//		TextView tv2 = (TextView) convertView.findViewById(id.scan_adapt_tv2);
//		tv1.setText(mapList.get(position).get("engword"));
//		tv2.setText(mapList.get(position).get("korword"));
//		return convertView;
//	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(layout.scan_main_adapt, null);
		TextView tv1 = (TextView) convertView.findViewById(id.scan_adapt_tv1);
		TextView tv2 = (TextView) convertView.findViewById(id.scan_adapt_tv2);
		tv1.setText(infoList.get(position).engword);
		tv2.setText(infoList.get(position).korword);
		return convertView;
	}

}
