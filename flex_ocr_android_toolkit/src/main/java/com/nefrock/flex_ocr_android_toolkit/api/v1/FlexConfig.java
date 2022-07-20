package com.nefrock.flex_ocr_android_toolkit.api.v1;

import android.content.Context;
import android.util.Size;

import java.util.ArrayList;
import java.util.List;

public class FlexConfig {
    private final Context context;

    private DetectorKind detectorKind;
    private List<String> detectorModelPaths;
    private List<Size> detectorInputSizes;
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
        this.detectorInputSizes = new ArrayList<>();
        this.detectorInputSizes.add(inputSize);
        this.detectorKind = kind;
        this.detectorModelPaths =  new ArrayList<>();
        this.detectorModelPaths.add(path);
        this.detectorConfig = detectorConfig;
    }

    public void setDetector(DetectorKind kind,
                            List<Size> inputSizes,
                            FlexModelSpecificConfig detectorConfig,
                            List<String> paths) {
        this.detectorInputSizes = inputSizes;
        this.detectorKind = kind;
        this.detectorModelPaths = paths;
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
        return detectorModelPaths.get(0);
    }

    public Size getDetectorInputSize() {
        return detectorInputSizes.get(0);
    }

    public List<String> getDetectorModelPaths() {
        return detectorModelPaths;
    }

    public List<Size> getDetectorInputSizes() {
        return detectorInputSizes;
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
