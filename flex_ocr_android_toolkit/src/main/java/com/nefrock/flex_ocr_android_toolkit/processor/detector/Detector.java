package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import android.graphics.Bitmap;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResult;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;

import org.opencv.core.Mat;

import java.util.List;

public interface Detector {
    DetectorResult process(Mat mat, FlexScanOption option);
    void init();
    void close();
}
