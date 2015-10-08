package com.godic.d_ui.c_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.go.dic.R;
import com.godic.c_data.a_scan.ScanData;

public class ListEditAdapter  extends RecyclerView.Adapter<ListEditAdapter.ListEditViewHolder>{
	
	ScanData scanData = ScanData.getInstance();

	public class ListEditViewHolder extends RecyclerView.ViewHolder{

		TextView tv_title, tv_date, tv_content;
		public ListEditViewHolder(View itemView) {
			super(itemView);
			tv_title = (TextView) itemView.findViewById(R.id.list_eadapt_tv_title);
			tv_date = (TextView) itemView.findViewById(R.id.list_eadapt_tv_date);
			tv_content = (TextView) itemView.findViewById(R.id.list_eadapt_tv_content);
		}
	}
	
	@Override
	public int getItemCount() {
		return scanData.getLength();
	}

	@Override
	public void onBindViewHolder(ListEditViewHolder lHolder, int position) {
		lHolder.tv_title.setText(scanData.getEngword(position));
	}

	@Override
	public ListEditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_edit_adapt, parent, false);
		ListEditViewHolder lvh = new ListEditViewHolder(v);
		return lvh;
	}

}

