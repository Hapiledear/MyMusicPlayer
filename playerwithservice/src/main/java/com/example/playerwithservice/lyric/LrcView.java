package com.example.playerwithservice.lyric;

import java.util.List;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.playerwithservice.R;
import com.example.playerwithservice.Service.playerService;

public class LrcView extends View implements ILrcView {

	public final static String TAG = "LrcView";

	/** normal display mode */
	public final static int DISPLAY_MODE_NORMAL = 0;
	/** seek display mode */
	public final static int DISPLAY_MODE_SEEK = 1;
	/** scale display mode ,scale font size */
	public final static int DISPLAY_MODE_SCALE = 2;

	/**
	 * 解析出来的每一行歌词
	 */
	private List<LrcRow> mLrcRows;
	private int mMinSeekFiredOffset = 10; // min offset for fire seek action,px;
	/**
	 *  当前播放的位置，其歌词需要高亮显示
	 */
	private int mHignlightRow = 0; //
	private int mHignlightRowColor = getResources()
			.getColor(R.color.lightgreen);
	private int mNormalRowColor = Color.WHITE;
	private int mSeekLineColor = Color.CYAN;
	private int mSeekLineTextColor = Color.CYAN;
	private int mSeekLineTextSize = 15;
	private int mMinSeekLineTextSize = 13;
	private int mMaxSeekLineTextSize = 18;
	private int mLrcFontSize = 23; // font size of lrc
	private int mMinLrcFontSize = 15;
	private int mMaxLrcFontSize = 35;
	private int mPaddingY = 10; // padding of each row
	private int mSeekLinePaddingX = 0; // Seek line padding x
	/**
	 * 歌词的展示模式，在滑动歌词窗口时改变
	 */
	private int mDisplayMode = DISPLAY_MODE_NORMAL;
	private LrcViewListener mLrcViewListener;

	private String mLoadingLrcTip = "暂无歌词";

	private Paint mPaint;

	public LrcView(Context context) {
		super(context);
	}

	public LrcView(Context context, AttributeSet attr) {
		super(context, attr);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(mLrcFontSize);
	}

	public void setListener(LrcViewListener l) {
		mLrcViewListener = l;
	}

	// 对外暴漏默认显示的文本
	public void setLoadingTipText(String text) {
		mLoadingLrcTip = text;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		final int height = getHeight(); // height of this view
		final int width = getWidth(); // width of this view

		// 默认显示
		if (mLrcRows == null || mLrcRows.size() == 0) {
			if (mLoadingLrcTip != null) {
				// draw tip when no lrc.
				mPaint.setColor(mHignlightRowColor);
				mPaint.setTextSize(mLrcFontSize);
				mPaint.setTextAlign(Align.CENTER);
				canvas.drawText(mLoadingLrcTip, width / 2, height / 2
						- mLrcFontSize, mPaint);
			}
			return;
		}

		int rowY = 0;
		final int rowX = width / 2;
		int rowNum = 0;

		// 高亮度
		String highlightText = mLrcRows.get(mHignlightRow).content;
		str = mLrcRows.get(mHignlightRow).strTime;
		int highlightRowY = height / 2 - mLrcFontSize;
		mPaint.setColor(mHignlightRowColor);
		mPaint.setTextSize(mLrcFontSize);
		mPaint.setTextAlign(Align.CENTER);
		canvas.drawText(highlightText, rowX, highlightRowY, mPaint);

		// 显示时间还有线
		if (mDisplayMode == DISPLAY_MODE_SEEK) {
			// draw Seek line and current time when moving.
			mPaint.setColor(mSeekLineColor);
			canvas.drawLine(mSeekLinePaddingX, highlightRowY, width
					- mSeekLinePaddingX, highlightRowY, mPaint);
			mPaint.setColor(mSeekLineTextColor);
			mPaint.setTextSize(mSeekLineTextSize);
			mPaint.setTextAlign(Align.LEFT);
			canvas.drawText(mLrcRows.get(mHignlightRow).strTime, 0,
					highlightRowY, mPaint);
		}

		mPaint.setColor(mNormalRowColor);
		mPaint.setTextSize(mLrcFontSize);
		mPaint.setTextAlign(Align.CENTER);

		/*绘制歌词布局的上半部分*/
		rowNum = mHignlightRow - 1;
		rowY = highlightRowY - mPaddingY - mLrcFontSize;
		while (rowY > -mLrcFontSize && rowNum >= 0) {
			String text = mLrcRows.get(rowNum).content;
			canvas.drawText(text, rowX, rowY, mPaint);
			rowY -= (mPaddingY + mLrcFontSize);
			rowNum--;
		}

		/*绘制歌词布局的下半部分*/
		rowNum = mHignlightRow + 1;
		rowY = highlightRowY + mPaddingY + mLrcFontSize;
		while (rowY < height && rowNum < mLrcRows.size()) {
			String text = mLrcRows.get(rowNum).content;
			canvas.drawText(text, rowX, rowY, mPaint);
			rowY += (mPaddingY + mLrcFontSize);
			rowNum++;
		}
	}

	/**
	 * 对外接口，使歌词滚动，或者跳转到指定位置。
	 * @param position
     */
	public void seekLrc(int position) {
		if (mLrcRows == null || position < 0 || position > mLrcRows.size()) {
			return;
		}
		LrcRow lrcRow = mLrcRows.get(position);
		mHignlightRow = position;
		invalidate();
		if (mLrcViewListener != null) {
			mLrcViewListener.onLrcSeeked(position, lrcRow);
		}
	}

	private float mLastMotionY;
	private PointF mPointerOneLastMotion = new PointF();
	private PointF mPointerTwoLastMotion = new PointF();
	private boolean mIsFirstMove = false; // whether is first move , some events
											// can't not detected in touch down,
											// such as two pointer touch, so
											// it's good place to detect it in
											// first move

	/**
	 * 当前播放位置的歌词的开始时间，分：秒：毫秒 格式
	 */
	private String str;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mLrcRows == null || mLrcRows.size() == 0) {
			return super.onTouchEvent(event);
		}

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "down,mLastMotionY:" + mLastMotionY);
			mLastMotionY = event.getY();
			mIsFirstMove = true;
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:

			if (event.getPointerCount() == 2) {
				Log.d(TAG, "two move");
				doScale(event);
				return true;
			}
			Log.d(TAG, "one move");
			// single pointer mode ,seek
			if (mDisplayMode == DISPLAY_MODE_SCALE) {
				// if scaling but pointer become not two ,do nothing.
				return true;
			}

			doSeek(event);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mDisplayMode == DISPLAY_MODE_SEEK) {
				seekLrc(mHignlightRow);

				// 指定播放的位置（以毫秒为单位）
				playerService.player.seekToMusic(timeConvert(str));
			}
			mDisplayMode = DISPLAY_MODE_NORMAL;
			invalidate();
			break;
		}
		return true;
	}

	/**
	 * 辅助函数，将mm:ss:SS格式转换为毫秒格式
	 * @param timeString
	 * @return
     */
	private static int timeConvert(String timeString) {
		timeString = timeString.replace('.', ':');
		String[] times = timeString.split(":");
		// mm:ss:SS
		return Integer.valueOf(times[0]) * 60 * 1000
				+ Integer.valueOf(times[1]) * 1000 + Integer.valueOf(times[2]);
	}

	/**
	 *如果触摸点是两个，则变为查看模式
	 * @param event
     */
	private void doScale(MotionEvent event) {

		if (mDisplayMode == DISPLAY_MODE_SEEK) {
			// if Seeking but pointer become two, become to scale mode
			mDisplayMode = DISPLAY_MODE_SCALE;
			Log.d(TAG, "two move but teaking ...change mode");
			return;
		}
		// two pointer mode , scale font
		if (mIsFirstMove) {
			mDisplayMode = DISPLAY_MODE_SCALE;
			invalidate();
			mIsFirstMove = false;
			setTwoPointerLocation(event);
		}
		int scaleSize = getScale(event);
		Log.d(TAG, "scaleSize:" + scaleSize);
		if (scaleSize != 0) {
			setNewFontSize(scaleSize);
			invalidate();
		}
		setTwoPointerLocation(event);
	}

	/**
	 * 设置mHignlightRow来进行歌词的替换显示
	 * @param event
     */
	private void doSeek(MotionEvent event) {
		float y = event.getY();
		float offsetY = y - mLastMotionY; // touch offset.
		if (Math.abs(offsetY) < mMinSeekFiredOffset) {
			// move to short ,do not fire seek action
			return;
		}
		mDisplayMode = DISPLAY_MODE_SEEK;
		int rowOffset = Math.abs((int) offsetY / mLrcFontSize); // highlight row
																// offset.
		Log.d(TAG, "move new hightlightrow : " + mHignlightRow + " offsetY: "
				+ offsetY + " rowOffset:" + rowOffset);
		if (offsetY < 0) {
			// finger move up
			mHignlightRow += rowOffset;
		} else if (offsetY > 0) {
			// finger move down
			mHignlightRow -= rowOffset;
		}
		mHignlightRow = Math.max(0, mHignlightRow);
		mHignlightRow = Math.min(mHignlightRow, mLrcRows.size() - 1);

		if (rowOffset > 0) {
			mLastMotionY = y;
			invalidate();
		}
	}

	private void setTwoPointerLocation(MotionEvent event) {
		mPointerOneLastMotion.x = event.getX(0);
		mPointerOneLastMotion.y = event.getY(0);
		mPointerTwoLastMotion.x = event.getX(1);
		mPointerTwoLastMotion.y = event.getY(1);
	}

	private void setNewFontSize(int scaleSize) {
		mLrcFontSize += scaleSize;
		mSeekLineTextSize += scaleSize;
		mLrcFontSize = Math.max(mLrcFontSize, mMinLrcFontSize);
		mLrcFontSize = Math.min(mLrcFontSize, mMaxLrcFontSize);
		mSeekLineTextSize = Math.max(mSeekLineTextSize, mMinSeekLineTextSize);
		mSeekLineTextSize = Math.min(mSeekLineTextSize, mMaxSeekLineTextSize);
	}

	/**
	 * 获取滑动的距离
	 * @param event
	 * @return
     */
	private int getScale(MotionEvent event) {
		Log.d(TAG, "scaleSize getScale");
		float x0 = event.getX(0);
		float y0 = event.getY(0);
		float x1 = event.getX(1);
		float y1 = event.getY(1);
		float maxOffset = 0; // max offset between x or y axis,used to decide
								// scale size

		boolean zoomin = false;

		float oldXOffset = Math.abs(mPointerOneLastMotion.x
				- mPointerTwoLastMotion.x);
		float newXoffset = Math.abs(x1 - x0);

		float oldYOffset = Math.abs(mPointerOneLastMotion.y
				- mPointerTwoLastMotion.y);
		float newYoffset = Math.abs(y1 - y0);

		maxOffset = Math.max(Math.abs(newXoffset - oldXOffset),
				Math.abs(newYoffset - oldYOffset));
		if (maxOffset == Math.abs(newXoffset - oldXOffset)) {
			zoomin = newXoffset > oldXOffset ? true : false;
		} else {
			zoomin = newYoffset > oldYOffset ? true : false;
		}

		Log.d(TAG, "scaleSize maxOffset:" + maxOffset);

		if (zoomin)
			return (int) (maxOffset / 10);
		else
			return -(int) (maxOffset / 10);
	}

	/**
	 * 对外接口，传入已经解析好的歌词列表，同时更新界面
	 * @param lrcRows
     */
	public void setLrc(List<LrcRow> lrcRows) {
		mLrcRows = lrcRows;
		invalidate();
	}

	// 设置正在播放的时间段
	public void seekLrcToTime(long time) {
		if (mLrcRows == null || mLrcRows.size() == 0) {
			return;
		}

		if (mDisplayMode != DISPLAY_MODE_NORMAL) {
			// touching
			return;
		}

		Log.d(TAG, "seekLrcToTime:" + time);
		// find row
		for (int i = 0; i < mLrcRows.size(); i++) {
			LrcRow current = mLrcRows.get(i);
			LrcRow next = i + 1 == mLrcRows.size() ? null : mLrcRows.get(i + 1);

			if ((time >= current.time && next != null && time < next.time)
					|| (time > current.time && next == null)) {
				seekLrc(i);
				return;
			}
		}
	}
}
