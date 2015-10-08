package com.godic.d_ui.c_list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.go.dic.R;
import com.godic.c_data.a_scan.ScanData;
import com.godic.c_data.b_list.ListDAO;
import com.godic.c_data.c_item.ItemData;
import com.godic.d_ui.d_item.ItemActivity;

public class ListAdapter extends
		RecyclerView.Adapter<ListAdapter.ListViewHolder> {

	ScanData scanData = ScanData.getInstance();
	ListDAO listDAO = ListDAO.getInstance();
	Context mContext = null;

	public ListAdapter(Context context) {
		mContext = context;
	}

	public class ListViewHolder extends RecyclerView.ViewHolder {
		SwipeLayout slo_swipe;
		TextView tv_title, tv_date, tv_content;
		EditText et_editTitle;
		Button bt_edit, bt_delete, bt_cancel;
		ImageView iv_capture;
		int listPosition, dbPosition;

		public ListViewHolder(View itemView) {
			super(itemView);
			slo_swipe = (SwipeLayout) itemView
					.findViewById(R.id.list_madapt_swipe);
			tv_title = (TextView) itemView
					.findViewById(R.id.list_madapt_tv_title);
			tv_date = (TextView) itemView
					.findViewById(R.id.list_madapt_tv_date);
			tv_content = (TextView) itemView
					.findViewById(R.id.list_madapt_tv_content);
			iv_capture = (ImageView) itemView.findViewById(R.id.list_madapt_iv);
			bt_edit = (Button) itemView.findViewById(R.id.list_madapt_bt_edit);
			bt_delete = (Button) itemView
					.findViewById(R.id.list_madapt_bt_delete);
			bt_cancel = (Button) itemView
					.findViewById(R.id.list_madapt_bt_cancel);

			itemView.setOnTouchListener(touchListener);
			slo_swipe.setShowMode(SwipeLayout.ShowMode.PullOut);
			slo_swipe.addSwipeListener(swipeListener);
			bt_edit.setOnClickListener(btClickListener);
			bt_delete.setOnClickListener(btClickListener);
			bt_cancel.setOnClickListener(btClickListener);
		}

		OnClickListener btClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.list_madapt_bt_edit:
					clickBtEdit();
					break;
				case R.id.list_madapt_bt_delete:
					clickBtDelete();
					break;
				case R.id.list_madapt_bt_cancel:
					cliclBtOpen();
					break;
				default:
					break;
				}
			}
		};

		void clickBtEdit() {
			et_editTitle = new EditText(mContext);
			AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
			ab.setTitle("제목 변경");
			et_editTitle.setHint(tv_title.getText());
			ab.setView(et_editTitle);
			ab.setPositiveButton("확인", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					listDAO.update(et_editTitle.getText().toString(),
							listDAO.getIdInCursor(dbPosition));
					notifyItemChanged(listPosition);
//					listDAO.changeCursor();
					notifyDataSetChanged();
				}
			});
			ab.setNegativeButton("취소", null);
			ab.show();
		}

		void clickBtDelete() {
			listDAO.delete(listDAO.getDateInCursor(dbPosition));
			notifyItemRemoved(listPosition);
//			listDAO.changeCursor();
			notifyDataSetChanged();
		}

		void cliclBtOpen() {

		}

		OnTouchListener touchListener = new OnTouchListener() {
			int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
			int xd, yd;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					x1 = (int) event.getX();
					y1 = (int) event.getY();
					break;
				case MotionEvent.ACTION_UP:
					x2 = (int) event.getX();
					y2 = (int) event.getY();
					break;
				}

				xd = Math.abs(x2 - x1);
				yd = Math.abs(y2 - y1);

				Log.e("x1", x1 + "");
				Log.e("x2", x2 + "");
				Log.e("xd", xd + "");
				Log.e("yd", yd + "");

				if (xd < 4 && yd < 4) {
					String title = tv_title.getText().toString();
					String date = tv_date.getText().toString();
					ItemData itemData = ItemData.getInstance();
					itemData.setData(title, date,
							listDAO.getWlistInCursor(dbPosition),
							iv_capture.getDrawable());
					Intent intent = new Intent(mContext, ItemActivity.class);
					mContext.startActivity(intent);
					return true;
				}
				return false;
			}
		};

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
				Log.e("listadapter listposition", listPosition + "");
				Log.e("listadapter dbposition", dbPosition + "");
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
	}

	@Override
	public int getItemCount() {
		return listDAO.getCursorCount();
	}

	@Override
	public void onBindViewHolder(ListViewHolder holder, int position) {
		int cPosition = getItemCount() - position - 1;
		holder.dbPosition = cPosition;
		holder.listPosition = position;
		listDAO.setCursorOnPosition(cPosition);
		holder.tv_title.setText(listDAO.getTitleInCursor());
		holder.tv_date.setText(listDAO.getFormattingDateInCursor());
		holder.iv_capture.setImageBitmap(listDAO.getImageInCursor());
		holder.tv_content.setText(listDAO.getWlistStrInCursor());
	}

	@Override
	public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.list_main_adapt, parent, false);
		return new ListViewHolder(v);
	}
}
