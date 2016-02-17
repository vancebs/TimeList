package com.hf.view;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hf.timelist.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ListView with time at the left side
 * Created by Fan on 2016/2/15.
 */
public class TimeListView extends ListView {
    public static final String TAG = "TimeListView";

    private float mVerticalDividerPos = 0.0f;
    private Drawable mDot;
    private float mDotWidth;
    private float mDotHeight;
    private float mDotLeftPos;
    private float mDotRightPos;
    private Paint mDividerPaint;
    private Paint mTimeTextPaint;

    public TimeListView(Context context) {
        this(context, null);
    }

    public TimeListView(Context context, AttributeSet attrs) {
        // this code used when this class is implemented in apps
        this(context, attrs, getResId("com.android.internal", "attr", "listViewStyle"));

        // this code used when this class is implemented in framework
        //this(context, attrs, R.attr.listViewStyle);
    }

    public TimeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TimeListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // load resources
        Resources res = getContext().getResources();
        mVerticalDividerPos = res.getDimension(R.dimen.vertical_divider_pos);

        mDividerPaint = new Paint();
        mDividerPaint.setColor(res.getColor(R.color.vertical_divider_color));
        mDividerPaint.setStrokeWidth(res.getDimensionPixelSize(R.dimen.vertical_divider_width));

        mDot = res.getDrawable(R.drawable.dot, null);
        mDotWidth = (mDot == null) ? 0.0f : mDot.getMinimumWidth();
        mDotHeight = (mDot == null) ? 0.0f : mDot.getMinimumHeight();
        mDotLeftPos = mVerticalDividerPos - mDotWidth / 2.0f;
        mDotRightPos = mVerticalDividerPos + mDotWidth / 2.0f;

        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setColor(res.getColor(R.color.time_text_color));
        mTimeTextPaint.setTextSize(res.getDimension(R.dimen.time_text_size));
        mTimeTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // draw vertical divider
        canvas.drawLine(mVerticalDividerPos, 0, mVerticalDividerPos, canvas.getHeight(), mDividerPaint);

        // draw others
        super.dispatchDraw(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result;

        // draw content
        result = drawContent(canvas, child, drawingTime);

        // draw time
        result &= drawTime(canvas, child);

        // draw dot
        result &= drawDot(canvas, child);

        return result;
    }

    private boolean drawContent(Canvas canvas, View child, long drawingTime) {
        canvas.save();
        canvas.translate(mDotRightPos, 0);

        boolean result = super.drawChild(canvas, child, drawingTime);

        canvas.restore();
        return result;
    }

    private boolean drawTime(Canvas canvas, View child) {
        Calendar date = getDate(child);
        Calendar today = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

        Paint.FontMetrics fm = mTimeTextPaint.getFontMetrics();
        float textBaseY = (child.getBottom() + child.getTop() - fm.bottom - fm.top) / 2.0f;
        float textX = mDotLeftPos / 2.0f;

        if (date.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.DATE) == today.get(Calendar.DATE)) {
            // It's today. Show hour and minute
            if (is24HourFormat()) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String timeText = sdf.format(date.getTime());
                canvas.drawText(timeText, textX, textBaseY, mTimeTextPaint);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                SimpleDateFormat sdf_ampm = new SimpleDateFormat("a", Locale.getDefault());

                String timeText = sdf.format(date.getTime());
                String ampm = sdf_ampm.format(date.getTime());
                float textBaseY1 = textBaseY - (fm.bottom - fm.top) / 2.0f;
                float textBaseY2 = textBaseY + (fm.bottom - fm.top) / 2.0f;
                canvas.drawText(timeText, textX, textBaseY1, mTimeTextPaint);
                canvas.drawText(ampm, textX, textBaseY2, mTimeTextPaint);
            }
        } else {
            // show month and date
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
            String timeText = sdf.format(date.getTime());
            canvas.drawText(timeText, textX, textBaseY, mTimeTextPaint);
        }
        return true;
    }

    private boolean drawDot(Canvas canvas, View child) {
        int dotTop = child.getTop() + (child.getMeasuredHeight() - (int)mDotHeight) / 2;
        mDot.setBounds((int) mDotLeftPos, dotTop, (int) mDotLeftPos + (int) mDotWidth, dotTop + (int) mDotHeight);
        mDot.draw(canvas);
        return true;
    }

    private boolean is24HourFormat() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }

    /**
     * Get resource ID.
     * @param pkg package name
     * @param type resource type
     * @param key resource type (name)
     * @return resource ID
     */
    private static int getResId( String pkg, String type, String key) {
        int resId = -1;
        try {
            String clazzName = pkg + ".R$" + type;
            Class<?> clazz = Class.forName(clazzName);
            Field field = clazz.getField(key);
            resId = field.getInt(null);
        } catch (ClassNotFoundException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
            Log.w(TAG, "getResId()# " + String.format("[pkg: %s, type: %s, key: %s]", pkg, type, key), e);
        }

        return resId;
    }

    /**
     * Set date into view. So that TimeListView can know the time should be displayed.
     * @param view View provided by adapter from method {@link android.widget.BaseAdapter#getView(int, View, ViewGroup)} or {@link android.widget.CursorAdapter#bindView(View, Context, Cursor)}
     * @param date date time stored by Date class
     */
    @SuppressWarnings("unused")
    public static void setDate(View view, Date date) {
        view.setTag(R.id.date_tag_key, date.getTime());
    }

    /**
     * Set date into view. So that TimeListView can know the time should be displayed.
     * @param view View provided by adapter from method {@link android.widget.BaseAdapter#getView(int, View, ViewGroup)} or {@link android.widget.CursorAdapter#bindView(View, Context, Cursor)}
     * @param timeInMillis time in millis. i.e. System.currentTimeMillis()
     */
    @SuppressWarnings("unused")
    public static void setDate(View view, long timeInMillis) {
        view.setTag(R.id.date_tag_key, timeInMillis);
    }

    /**
     * Set date into view. So that TimeListView can know the time should be displayed.
     * @param view View provided by adapter from method {@link android.widget.BaseAdapter#getView(int, View, ViewGroup)} or {@link android.widget.CursorAdapter#bindView(View, Context, Cursor)}
     * @param calendar date time stored by Calendar class
     */
    @SuppressWarnings("unused")
    public static void setDate(View view, Calendar calendar) {
        view.setTag(R.id.date_tag_key, calendar.getTimeInMillis());
    }

    private static Calendar getDate(View view) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        cal.setTimeInMillis((Long) view.getTag(R.id.date_tag_key));
        return  cal;
    }
}
