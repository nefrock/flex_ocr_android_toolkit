package com.nefrock.flex_ocr_android_toolkit.api.v0;

import android.content.Context;

public class FlexConfig {
    private final String detectorModelPath;
    private int detectorModelInputHeight;
    private int detectorModelInputWidth;
    private final Context context;

    public FlexConfig(Context c, String detectorModelPath) {
        this.context = c;
        this.detectorModelPath = detectorModelPath;
        this.detectorModelInputWidth = 512;
        this.detectorModelInputHeight = 384;
    }

    public void setDetectorModelInputSize(int width, int height) {
        this.detectorModelInputWidth = width;
        this.detectorModelInputHeight = height;
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

    public Context getContext() {
        return context;
    }
}
