package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import com.nefrock.flex_ocr_android_toolkit.processor.result.DetectorResult;

import org.opencv.core.Mat;

public interface Detector {

    //TODO: 非同期処理にする？
    DetectorResult process(Mat image, int x0, int y0);
    void init();
    void close();
}
