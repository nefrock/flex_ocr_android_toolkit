package com.nefrock.flex.app;

import java.util.List;

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
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;


public class OverlayView extends View {

    private static final float TEXT_SIZE = 20f;
    private static final float BIG_TEXT_SIZE = 48f;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint bboxBoxPaint;
    private final Paint textPaint;
    private final Paint bigTextPaint;
    private final Paint textBackgroundPaint;
    private Size resolutionSize;

    private FlexScanResults flexScanResults;

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        flexScanResults = null;

        bboxBoxPaint = new Paint();
        int greenColor = Color.argb(127, 0, 255, 0);
        bboxBoxPaint.setColor(greenColor);
        bboxBoxPaint.setStyle(Paint.Style.STROKE);
        bboxBoxPaint.setStrokeWidth(STROKE_WIDTH);

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
        double ratio = w / rw;
        c.drawText("latency(ms): " + this.flexScanResults.getElapsedTime() , 10, this.getTop() + 50, bigTextPaint);
        for(FlexScanResult result : this.flexScanResults.getResults()) {
            Rect bbox = result.getBoundingBox();
            Rect boundingBox = scaling(bbox, ratio);
            //draw bbox
            c.drawRect(boundingBox, bboxBoxPaint);
            String text = result.getText();
            //draw text
            if(text != null) {
                c.drawRect(boundingBox.left - 1, boundingBox.top - TEXT_SIZE, boundingBox.right + 1, boundingBox.top, textBackgroundPaint);
                c.drawText(text, boundingBox.left, boundingBox.top - 3, textPaint);
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
