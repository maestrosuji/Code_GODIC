package com.godic.d_ui.d_item;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.go.dic.R;
import com.godic.c_data.WordInfo;
import com.godic.c_data.c_item.ItemData;
import com.godic.c_data.d_bookmark.BookmarkDAO;
import com.godic.d_ui.a_start.StartActivity;

public class ItemAdapter extends
		RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

	Context mContext;
	WordInfo wordInfo;
	ItemData itemData = ItemData.getInstance();
	BookmarkDAO bmkDAO = BookmarkDAO.getInstance();
	Toast toast;

	CustomDialog dialog;

	public ItemAdapter(Context context) {
		mContext = context;
	}

	public class ItemViewHolder extends RecyclerView.ViewHolder {
		SwipeLayout slo_swipe;
		TextView tv_engword, tv_korword;
		StarCheckBox btn_star;
		int position;
		WordInfo winfo;

		Button bt_open, bt_cancel;

		public ItemViewHolder(View itemView) {
			super(itemView);
			slo_swipe = (SwipeLayout) itemView
					.findViewById(R.id.item_madapt_swipe);
			btn_star = (StarCheckBox) itemView
					.findViewById(R.id.item_madapt_starcheck);

			tv_engword = (TextView) itemView
					.findViewById(R.id.item_madapt_tv_eng);
			tv_korword = (TextView) itemView
					.findViewById(R.id.item_madapt_tv_kor);

			bt_open = (Button) itemView.findViewById(R.id.item_madapt_bt_open);
			bt_cancel = (Button) itemView
					.findViewById(R.id.item_madapt_bt_cancel);

			btn_star.setOnClickListener(clickListener);
			tv_korword.setOnClickListener(clickListener);
			bt_open.setOnClickListener(clickListener);
			bt_cancel.setOnClickListener(clickListener);

			slo_swipe.setShowMode(SwipeLayout.ShowMode.PullOut);
			slo_swipe.addSwipeListener(swipeListener);

		}

		SwipeLayout.SwipeListener swipeListener = new SwipeLayout.SwipeListener() {

			@Override
			public void onUpdate(SwipeLayout arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartOpen(SwipeLayout arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartClose(SwipeLayout arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onOpen(SwipeLayout arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onHandRelease(SwipeLayout arg0, float arg1, float arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClose(SwipeLayout arg0) {
				// TODO Auto-generated method stub

			}
		};

		// button click listener
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {

				case R.id.item_madapt_starcheck:
					clickStarcheck();
					break;
				// 단어 뜻 클릭
				case R.id.item_madapt_bt_open:
					clickBtMore();
					break;
				case R.id.item_madapt_bt_web:
					clickBtWeb();
					break;

				case R.id.item_madapt_bt_delete:

					break;

				case R.id.item_madapt_bt_cancel:
					clickBtCancel();
					break;
				case R.id.item_madapt_tv_kor:
					clickBtMore();
					break;

				default:
					break;
				}
			}
		};

		void clickStarcheck() {
			if (!winfo.bmkstate) {
				showToast("Add '" + winfo.engword + "' to Bookmark.");
				bmkDAO.insert(winfo);
				// Log.e("@@@Checked winfoState", winfo.bmkstate + "");

			} else {
				showToast("Delete '" + winfo.engword + "' to Bookmark.");
				bmkDAO.delete(winfo);
				// Log.e("@@@Checked winfoState", winfo.bmkstate + "");
			}
		}

		void clickBtMore() {

			dialog = new CustomDialog(mContext);
			//dialog.setTitle(tv_engword.getText().toString());
			dialog.tv_custom_eng.setText(tv_engword.getText().toString());
			dialog.tv_custom_kor.setText(tv_korword.getText());

			dialog.clickBtWeblistener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					clickBtWeb();
				}
			});
			dialog.clickBtBookmarklistener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickStarcheck();
				}
			});
			dialog.clickBtCloselistener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();

			// Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
			dialog.setCanceledOnTouchOutside(false);// 없어지지 않도록 설정
			// dialog.getWindow().setGravity(Gravity.CENTER); // 다이얼로그 위치
			dialog.setCancelable(true); // Back키 눌렀을 경우 다이얼로그 캔슬 여부

		}
		
		void clickBtWeb() {
			final String web_eng = tv_engword.getText().toString();
			StartActivity.value = web_eng;
			Intent intent = new Intent(mContext, WebSearch.class);
			mContext.startActivity(intent);
		}

		void clickBtCancel() {
			slo_swipe.close(true);
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
		holder.winfo = new WordInfo(wordInfo.engword, wordInfo.korword,
				wordInfo.bmkstate);
		bmkDAO.checkState(holder.winfo);
		holder.btn_star.setChecked(holder.winfo.bmkstate);
		holder.tv_engword.setText(holder.winfo.engword);
		holder.tv_korword.setText(holder.winfo.korword);

		// Log.e("@@@onBindView wordInfo", wordInfo.engword);
		// Log.e("@@@onBindView wordInfo.bmkstate", wordInfo.bmkstate + "");
		// Log.e("@@@onBindView winfo.bmkstate", holder.winfo.bmkstate + "");
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.item_main_adapt, parent, false);
		return new ItemViewHolder(v);
	}

	private void showToast(String str) {
		if (toast == null) {
			toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
		}
		toast.show();
		toast.setText(str);
	}
}
