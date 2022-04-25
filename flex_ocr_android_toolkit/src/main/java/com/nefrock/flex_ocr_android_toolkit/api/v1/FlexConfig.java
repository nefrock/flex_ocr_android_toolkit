package com.nefrock.flex_ocr_android_toolkit.api.v1;

import android.content.Context;

public class FlexConfig {
    private final Context context;

    private DetectorKind detectorKind;
    private String detectorModelPath;
    private int detectorModelInputHeight;
    private int detectorModelInputWidth;

    private RecognizerKind recognizerKind;
    private String recognizerModelPath;

    public FlexConfig(Context c) {
        this.context = c;
        this.detectorModelInputWidth = 512;
        this.detectorModelInputHeight = 384;
    }

    public void setDetector(DetectorKind kind, FlexModelSpecificConfig detectorConfig, String path) {
        this.detectorKind = kind;
        this.detectorModelPath = path;
    }

    public void setRecognizer(RecognizerKind kind, FlexModelSpecificConfig recognizerConfig, String path) {
        this.recognizerKind = kind;
        this.recognizerModelPath = path;
    }

    public void setDetectorModelInputSize(int width, int height) {
        this.detectorModelInputWidth = width;
        this.detectorModelInputHeight = height;
    }

    public Context getContext() {
        return context;
    }

    public DetectorKind getDetectorKind() {
        return detectorKind;
    }

    public String getDetectorModelPath() {
        return detectorModelPath;
    }

    public int getDetectorModelInputHeight() {
        return detectorModelInputHeight;
    }

    public int getDetectorModelInputWidth() {
        return detectorModelInputWidth;
    }

    public RecognizerKind getRecognizerKind() {
        return recognizerKind;
    }

    public String getRecognizerModelPath() {
        return recognizerModelPath;
    }
}
