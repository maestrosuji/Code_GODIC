package com.godic.d_ui.d_item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.go.dic.R;
import com.godic.c_data.a_scan.WordInfo;
import com.godic.c_data.c_item.ItemData;
import com.godic.d_ui.e_bookmark.BookmarkDAO;

public class ItemAdapter extends
		RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

	Context mContext;
	WordInfo wordInfo;
	ItemData itemData = ItemData.getInstance();
	BookmarkDAO bmkDAO = BookmarkDAO.getInstance();
	Toast toast;

	public ItemAdapter(Context context) {
		mContext = context;
		bmkDAO.bmkInit(mContext);
	}

	public class ItemViewHolder extends RecyclerView.ViewHolder {
		TextView tv_engword, tv_korword;
		StarCheckBox btn_star;
		int position;
		WordInfo winfo;		

		public ItemViewHolder(View itemView) {
			super(itemView);
			tv_engword = (TextView) itemView
					.findViewById(R.id.item_madapt_tv_eng);
			tv_korword = (TextView) itemView
					.findViewById(R.id.item_madapt_tv_kor);
			btn_star = (StarCheckBox) itemView
					.findViewById(R.id.btn_star);	
			btn_star.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!winfo.bmkstate) {
						toast = Toast.makeText(mContext, "Add '"+winfo.engword+"' to Bookmark.", Toast.LENGTH_LONG);
						toast.show();
						bmkDAO.insert(winfo);
						bmkDAO.bmkClose();
						Log.e("@@@Checked winfoState",winfo.bmkstate+"");
						
					}else{
						toast = Toast.makeText(mContext, "Delete '"+winfo.engword+"' to Bookmark.", Toast.LENGTH_LONG);
						toast.show();
						bmkDAO.delete(winfo);
						bmkDAO.bmkClose();
						Log.e("@@@Checked winfoState",winfo.bmkstate+"");
					}
				}
			});
		}	
		
	}

	@Override
	public int getItemCount() {
		return itemData.wordInfoList.size();
	}

	@Override
	public void onBindViewHolder(ItemViewHolder holder, int position) {
		holder.position = position;
		wordInfo = itemData.wordInfoList.get(position);			
		holder.winfo = new WordInfo(wordInfo.engword, wordInfo.korword);
		holder.winfo.bmkstate = wordInfo.bmkstate;
		bmkDAO.checkState(holder.winfo);
		holder.btn_star.setChecked(holder.winfo.bmkstate);
		holder.tv_engword.setText(holder.winfo.engword);
		holder.tv_korword.setText(holder.winfo.korword);
		
		Log.e("@@@onBindView wordInfo",wordInfo.engword);
		Log.e("@@@onBindView wordInfo.bmkstate",wordInfo.bmkstate+"");
		Log.e("@@@onBindView winfo.bmkstate",holder.winfo.bmkstate+"");
		
	
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.item_main_adapt, parent, false);
		return new ItemViewHolder(v);
	}
}
