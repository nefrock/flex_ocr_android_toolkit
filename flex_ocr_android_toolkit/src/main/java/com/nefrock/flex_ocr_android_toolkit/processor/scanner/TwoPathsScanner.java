package com.nefrock.flex_ocr_android_toolkit.processor.scanner;

import android.graphics.Bitmap;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.api.v1.OnScanListener;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.DetectorResult;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.Recognizer;

public class TwoPathsScanner implements Scanner{

    private final Detector detector;
    private final Recognizer recognizer;

    public TwoPathsScanner(Detector detector, Recognizer recognizer) {
        this.detector = detector;
        this.recognizer = recognizer;
    }

    public void init() {
        detector.init();
        recognizer.init();
    }

    public void scan(Bitmap bitmap, FlexScanOption option, OnScanListener<FlexScanResults> listener) {
        DetectorResult detectorResult = detector.process(bitmap, option);
        recognizer.process(bitmap, detectorResult, option, listener);
    }
}
