package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;

import org.opencv.core.Mat;

public class Identity implements Detector {

    @Override
    public DetectorResult process(Mat rgb, FlexScanOption option) {
        Rect bbox = new Rect(0,0, rgb.width(), rgb.height());
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
