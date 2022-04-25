package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import android.graphics.Bitmap;

import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;

import org.opencv.core.Mat;

public interface Detector {
    DetectorResult process(Bitmap image, FlexScanOption option);
    void init();
    void close();
}
