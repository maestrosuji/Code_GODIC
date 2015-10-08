package com.godic.d_ui.b_scan;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekbar extends SeekBar {

	public VerticalSeekbar(Context context) {
		super(context);
	}

	public VerticalSeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VerticalSeekbar(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	protected void onDraw(Canvas c) {
		c.rotate(90);
		c.translate(0, -getWidth());

		super.onDraw(c);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int max = getMax();

		if (!isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			int i=0;
			i=max - (int) (max * event.getY() / getHeight());
			setProgress(max-i);
//			Log.i("Progress",getProgress()+"");
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}
}
