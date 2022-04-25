package com.nefrock.flex_ocr_android_toolkit.processor.recognizer;

import android.graphics.Bitmap;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.api.v1.OnScanListener;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.DetectorResult;

import org.opencv.core.Mat;

public interface Recognizer {
    void process(Mat mat,
                 Detector detector,
                 FlexScanOption option,
                 OnScanListener<FlexScanResults> listener);
    void init();
    void close();
}
