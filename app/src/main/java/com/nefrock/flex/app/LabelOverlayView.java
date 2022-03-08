package com.nefrock.flex.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class LabelOverlayView extends View {

    private static final float STROKE_WIDTH = 3.0f;
    private final Paint labelPaint;
    private final Paint labelBackgroundPaint;
    private Rect rect;

    public LabelOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int whiteColor = Color.argb(50, 255, 255, 255);
        int whiteEdgeColor = Color.argb(255, 255, 255, 255);
        labelPaint = new Paint();
        labelPaint.setColor(whiteEdgeColor);
        labelPaint.setStyle(Paint.Style.STROKE);
        labelPaint.setStrokeWidth(STROKE_WIDTH);

        labelBackgroundPaint = new Paint();
        labelBackgroundPaint.setColor(whiteColor);
        labelBackgroundPaint.setStyle(Paint.Style.FILL);
        rect = new Rect(100, 100, 300, 300);
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawRect(rect, labelBackgroundPaint);
        c.drawRect(rect, labelPaint);
    }

    public void drawLabelRectangle(Rect rect) {
        this.rect = rect;
        this.invalidate();
    }
}
