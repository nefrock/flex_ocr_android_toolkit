package com.nefrock.flex_ocr_android_toolkit.api.v1;

import android.content.Context;
import android.util.Size;

import java.util.HashMap;
import java.util.Map;

public class FlexConfig {
    private final Context context;

    private DetectorKind detectorKind;
    private String detectorModelPath;
    private Size detectorInputSize;
    private FlexModelSpecificConfig detectorConfig;

    private RecognizerKind recognizerKind;
    private String recognizerModelPath;
    private Size recognizerInputSize;
    private FlexModelSpecificConfig recognizerConfig;


    public FlexConfig(Context c) {
        this.context = c;
    }

    public void setDetector(DetectorKind kind,
                            Size inputSize,
                            FlexModelSpecificConfig detectorConfig,
                            String path) {
        this.detectorInputSize = inputSize;
        this.detectorKind = kind;
        this.detectorModelPath = path;
        this.detectorConfig = detectorConfig;
    }

    public void setRecognizer(RecognizerKind kind,
                              Size inputSize,
                              FlexModelSpecificConfig recognizerConfig,
                              String path) {
        this.recognizerInputSize = inputSize;
        this.recognizerKind = kind;
        this.recognizerModelPath = path;
        this.recognizerConfig = recognizerConfig;
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

    public Size getDetectorInputSize() {
        return detectorInputSize;
    }

    public FlexModelSpecificConfig getDetectorConfig() {
        return this.detectorConfig;
    }

    public RecognizerKind getRecognizerKind() {
        return recognizerKind;
    }

    public String getRecognizerModelPath() {
        return recognizerModelPath;
    }

    public Size getRecognizerInputSize() {
        return recognizerInputSize;
    }

    public FlexModelSpecificConfig getRecognizerConfig() {
        return this.recognizerConfig;
    }
}
