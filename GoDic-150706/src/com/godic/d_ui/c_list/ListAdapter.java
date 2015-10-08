package com.godic.d_ui.c_list;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.go.dic.R;
import com.godic.c_data.a_scan.ScanData;
import com.godic.c_data.a_scan.WordInfo;
import com.godic.c_data.b_list.ListDAO;
import com.godic.c_data.c_item.ItemData;
import com.godic.d_ui.d_item.ItemActivity;

public class ListAdapter extends
		RecyclerView.Adapter<ListAdapter.ListViewHolder> {

	ScanData scanData = ScanData.getInstance();
	ListDAO listDAO = ListDAO.getInstance();
	// CursorAdapter mCursorAdapter = null;
	Context mContext = null;
	Cursor mCursor;

	public ListAdapter(Context context, Cursor cursor) {
		mContext = context;
		mCursor = cursor;
	}

	public class ListViewHolder extends RecyclerView.ViewHolder {
		TextView tv_title, tv_date, tv_content;
		ImageView iv_capture;
		long ldate;
		// ArrayList<HashMap<String, String>> wordMapList;
		ArrayList<WordInfo> wordInfoList;

		public ListViewHolder(View itemView) {
			super(itemView);
			tv_title = (TextView) itemView
					.findViewById(R.id.list_madapt_tv_title);
			tv_date = (TextView) itemView
					.findViewById(R.id.list_madapt_tv_date);
			tv_content = (TextView) itemView
					.findViewById(R.id.list_madapt_tv_content);
			iv_capture = (ImageView) itemView.findViewById(R.id.list_madapt_iv);

			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String title = tv_title.getText().toString();
					String date = tv_date.getText().toString();
					ItemData itemData = ItemData.getInstance();
					itemData.setData(title, date, wordInfoList,
							iv_capture.getDrawable());
					Intent intent = new Intent(mContext, ItemActivity.class);
					mContext.startActivity(intent);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mCursor.getCount();
	}

	@Override
	public void onBindViewHolder(ListViewHolder holder, int postion) {
		mCursor.moveToPosition(getItemCount() - postion - 1);

		holder.tv_title.setText(mCursor.getString(1));

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy.MM.dd(EEE) HH:mm:ss");
		long writedate = mCursor.getLong(2);
		holder.ldate = writedate;
		cal.setTimeInMillis(writedate);
		holder.tv_date.setText(dateFormat.format(cal.getTime()));

		String imagePath = mContext.getFilesDir().getPath() + "/capture/"
				+ writedate;
		Log.e("open image path", imagePath);
		// BitmapFactory.Options bfo = new BitmapFactory.Options();
		// bfo.inSampleSize = 2;
		Bitmap captureImage = BitmapFactory.decodeFile(imagePath);
		holder.iv_capture.setImageBitmap(captureImage);

		byte[] buf = mCursor.getBlob(3);
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(buf);
			ois = new ObjectInputStream(bais);
			// holder.wordMapList = (ArrayList<HashMap<String, String>>) ois
			// .readObject();
			holder.wordInfoList = (ArrayList<WordInfo>) ois.readObject();
			StringBuilder sb = new StringBuilder();
			// for (HashMap<String, String> hashMap : holder.wordMapList) {
			// sb.append(hashMap.get("engword"));
			// sb.append(' ');
			// }
			for (WordInfo info : holder.wordInfoList) {
				sb.append(info.engword);
				sb.append(' ');
			}
			holder.tv_content.setText(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (ois != null) {
				ois.close();
			}
			if (bais != null) {
				bais.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.list_main_adapt, parent, false);
		return new ListViewHolder(v);
	}
}
