package com.godic.d_ui.d_item;

import java.util.HashMap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.go.dic.R;
import com.godic.c_data.a_scan.WordInfo;
import com.godic.c_data.c_item.ItemData;

public class ItemAdapter extends
		RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

	Context mContext;
	ItemData itemData = ItemData.getInstance();

	public ItemAdapter(Context context) {
		mContext = context;
	}

	public class ItemViewHolder extends RecyclerView.ViewHolder {
		TextView tv_engword, tv_korword;

		public ItemViewHolder(View itemView) {
			super(itemView);
			tv_engword = (TextView) itemView
					.findViewById(R.id.item_madapt_tv_eng);
			tv_korword = (TextView) itemView
					.findViewById(R.id.item_madapt_tv_kor);
		}
	}

	@Override
	public int getItemCount() {
		return itemData.wordInfoList.size();
	}

//	@Override
//	public void onBindViewHolder(ItemViewHolder holder, int position) {
//		HashMap<String, String> map = itemData.wordMapList.get(position);
//		holder.tv_engword.setText(map.get("engword"));
//		holder.tv_korword.setText(map.get("korword"));
//	}
	@Override
	public void onBindViewHolder(ItemViewHolder holder, int position) {
		WordInfo info = itemData.wordInfoList.get(position);
		holder.tv_engword.setText(info.engword);
		holder.tv_korword.setText(info.korword);
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.item_main_adapt, parent, false);
		return new ItemViewHolder(v);
	}
}
