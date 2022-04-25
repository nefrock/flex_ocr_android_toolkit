package com.nefrock.flex_ocr_android_toolkit.api.v1;

import android.content.Context;
import android.util.Size;

public class FlexConfig {
    private final Context context;

    private DetectorKind detectorKind;
    private String detectorModelPath;
    private Size detectorInputSize;

    private RecognizerKind recognizerKind;
    private String recognizerModelPath;
    private Size recognizerInputSize;

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
    }

    public void setRecognizer(RecognizerKind kind,
                              Size inputSize,
                              FlexModelSpecificConfig recognizerConfig,
                              String path) {
        this.recognizerInputSize = inputSize;
        this.recognizerKind = kind;
        this.recognizerModelPath = path;
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

    public RecognizerKind getRecognizerKind() {
        return recognizerKind;
    }

    public String getRecognizerModelPath() {
        return recognizerModelPath;
    }

    public Size getRecognizerInputSize() {
        return recognizerInputSize;
    }
}
