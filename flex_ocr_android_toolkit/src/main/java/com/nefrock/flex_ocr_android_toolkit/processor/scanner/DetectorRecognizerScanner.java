package com.nefrock.flex_ocr_android_toolkit.processor.scanner;

import android.graphics.Bitmap;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.api.v1.OnScanListener;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.DetectorResult;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.Recognizer;

import org.opencv.core.Mat;

public class DetectorRecognizerScanner implements Scanner{

    private final Detector detector;
    private final Recognizer recognizer;

    public DetectorRecognizerScanner(Detector detector, Recognizer recognizer) {
        this.detector = detector;
        this.recognizer = recognizer;
    }

    @Override
    public void init() {
        detector.init();
        recognizer.init();
    }

    public void scan(Mat rgb, FlexScanOption option, OnScanListener<FlexScanResults> listener) {
        recognizer.process(rgb, detector, option, listener);
    }
}
