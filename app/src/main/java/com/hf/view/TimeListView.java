package com.hf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Fan on 2016/2/15.
 */
public class TimeListView extends ListView {

    public TimeListView(Context context) {
        super(context);
    }

    public TimeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimeListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }
}
