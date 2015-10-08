package com.godic.d_ui.c_list;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.go.dic.R;
import com.godic.c_data.WordInfo;
import com.godic.c_data.a_scan.ScanData;
import com.godic.c_data.b_list.ListDAO;

public class ListEditAdapter extends
		RecyclerView.Adapter<ListEditAdapter.ListEditViewHolder> {

	ScanData scanData = ScanData.getInstance();
	ListDAO listDAO = ListDAO.getInstance();
	Context mContext = null;
	boolean[] mState;

	public ListEditAdapter(Context context) {
		mContext = context;
		mState = new boolean[getItemCount()];
	}

	public class ListEditViewHolder extends RecyclerView.ViewHolder {

		TextView tv_title, tv_date, tv_content;
		ImageView iv_capture;
		CheckBox cb_select;
		long ldate;
		ArrayList<WordInfo> wordInfoList;
//		boolean isSelected = false;
		int position;
		
		public ListEditViewHolder(View itemView) {
			super(itemView);
			tv_title = (TextView) itemView
					.findViewById(R.id.list_eadapt_tv_title);
			tv_date = (TextView) itemView
					.findViewById(R.id.list_eadapt_tv_date);
			tv_content = (TextView) itemView
					.findViewById(R.id.list_eadapt_tv_content);
			iv_capture = (ImageView) itemView.findViewById(R.id.list_eadapt_iv);
			cb_select = (CheckBox) itemView.findViewById(R.id.list_eadapt_cb);
//			cb_select.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView,
//						boolean isChecked) {
//					isSelected = isChecked;
//				}
//			});
			cb_select.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mState[position] = mState[position]? false:true;
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return listDAO.getCursorCount();
	}

	@Override
	public void onBindViewHolder(ListEditViewHolder holder, int position) {
		listDAO.setCursorOnPosition(position);
		holder.position = position;
		 
		holder.cb_select.setChecked(mState[position]);
		
	}

	@Override
	public ListEditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.list_edit_adapt, parent, false);
		return new ListEditViewHolder(v);
	}

}
