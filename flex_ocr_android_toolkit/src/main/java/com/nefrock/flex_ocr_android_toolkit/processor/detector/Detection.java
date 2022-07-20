package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import android.graphics.Rect;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;


public class Detection {
    private final double confidence;
    private final Rect boundingBox;
    private final FlexScanResultType detectionKind;
    private final boolean canOCR;

    public Detection(Rect boundingBox, double confidence, FlexScanResultType detectionKind, boolean canOCR) {
        this.boundingBox = boundingBox;
        this.confidence = confidence;
        this.detectionKind = detectionKind;
        this.canOCR = canOCR;
    }

    public double getConfidence() {
        return confidence;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public FlexScanResultType getDetectionKind() { return this.detectionKind; }

    public boolean canOCR() { return this.canOCR;}

    public org.opencv.core.Rect getCvBoundingBox() {
        return new org.opencv.core.Rect(boundingBox.left, boundingBox.top, boundingBox.width(), boundingBox.height());
    }

    public int area() {
        return boundingBox.width() * boundingBox.height();
    }
}
