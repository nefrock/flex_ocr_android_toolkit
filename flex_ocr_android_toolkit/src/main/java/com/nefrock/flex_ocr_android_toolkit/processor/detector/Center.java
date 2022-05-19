package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import android.graphics.Rect;
import android.util.Size;

import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class Center implements Detector {

    private final Size size;
    public Center(Size size) {
        this.size = size;
    }

    @Override
    public DetectorResult process(Mat rgb, FlexScanOption option) {
        final int imageWidth = rgb.width();
        final int imageHeight = rgb.height();
        final int imageCenterX = imageWidth / 2;
        final int imageCenterY = imageHeight / 2;

        final int height = size.getHeight();
        final int width = size.getWidth();

        final int left = imageCenterX - width / 2;
        final int top = imageCenterY - height / 2;

        Rect bbox = new Rect(left,top,left + width, top + height);
        Detection one = new Detection(bbox, 1.0, -1);
        List<Detection> lst = new ArrayList<>();
        lst.add(one);
        DetectorResult result = new DetectorResult(lst);
        return result;
    }

    @Override
    public void init() {
    }

    @Override
    public void close() {
    }
}
