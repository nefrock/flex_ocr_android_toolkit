package com.nefrock.flex.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;

import androidx.annotation.Nullable;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResult;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;


public class OverlayView extends View {

//    private static final float TEXT_SIZE = 40f;
//    private static final float BIG_TEXT_SIZE = 48f;
//    private static final float STROKE_WIDTH = 4.0f;

    private static final float TEXT_SIZE = 62f;
    private static final float BIG_TEXT_SIZE = 78f;
    private static final float STROKE_WIDTH = 8.0f;
    private static final float WIDE_STROKE_WIDTH = 16.0f;

    private final Paint textBboxPaint;
    private final Paint bboxPaint;

    private final Paint textPaint;
    private final Paint bigTextPaint;
    private final Paint textBackgroundPaint;
    private Size resolutionSize;

    private FlexScanResults flexScanResults;

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        flexScanResults = null;

        textBboxPaint = new Paint();
        int greenColor = Color.argb(127, 0, 255, 0);
        textBboxPaint.setColor(greenColor);
        textBboxPaint.setStyle(Paint.Style.STROKE);
        textBboxPaint.setStrokeWidth(STROKE_WIDTH);

        bboxPaint = new Paint();
        int redColor = Color.argb(127, 255, 255, 0);
        bboxPaint.setColor(redColor);
        bboxPaint.setStyle(Paint.Style.STROKE);
        bboxPaint.setStrokeWidth(WIDE_STROKE_WIDTH);

        textBackgroundPaint = new Paint();
        textBackgroundPaint.setColor(greenColor);
        textBackgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        int darkGreenColor = Color.argb(255, 0, 100, 0);
        textPaint.setColor(darkGreenColor);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setAntiAlias(true);

        bigTextPaint = new Paint();
        bigTextPaint.setColor(darkGreenColor);
        bigTextPaint.setTextSize(BIG_TEXT_SIZE);
        bigTextPaint.setAntiAlias(true);
    }

    public void setResolutionSize(Size size) {
        this.resolutionSize = size;
    }

    public void drawScanResult(FlexScanResults flexScanResults) {
        this.flexScanResults = flexScanResults;
        this.invalidate();
    }

    @Override
    public void onDraw(Canvas c) {
        if (this.flexScanResults == null) {
            return;
        }
        double w = this.getWidth();
        double rw = this.resolutionSize.getWidth();
        double rRatio = w / rw;

        double h = this.getHeight();
        double rh = this.resolutionSize.getHeight();
        double hRatio = h / rh;

        double minRatio = Math.min(rRatio, hRatio);

        c.drawText("latency(ms): " + this.flexScanResults.getElapsedTime() , 50, this.getTop() + 100, bigTextPaint);
        for(FlexScanResult result : this.flexScanResults.getResults()) {
            Rect bbox = result.getBoundingBox();
            Rect boundingBox = scaling(bbox, minRatio);

            String text = result.getText();
            if(text != null) {
                c.drawRect(boundingBox, textBboxPaint);
                c.drawRect(boundingBox.left - 1, boundingBox.top - TEXT_SIZE, boundingBox.right + 1, boundingBox.top, textBackgroundPaint);
                c.drawText(text, boundingBox.left, boundingBox.top - 3, textPaint);
            } else {
                c.drawRect(boundingBox, bboxPaint);
            }
        }
    }

    private Rect rotate90(Rect boundingBox) {
        int h = this.resolutionSize.getWidth();
        int left = h - boundingBox.top;
        int top = boundingBox.left;
        int right = h - boundingBox.bottom;
        int bottom = boundingBox.right;
        return new Rect(left, top, right, bottom);
    }

    private Rect scaling(Rect boundingBox, double ratio) {
        int left = boundingBox.left;
        int top = boundingBox.top;
        int right = boundingBox.right;
        int bottom = boundingBox.bottom;
        return new Rect((int) (left * ratio), (int) (top * ratio), (int) (right * ratio), (int) (bottom * ratio));
    }
}
