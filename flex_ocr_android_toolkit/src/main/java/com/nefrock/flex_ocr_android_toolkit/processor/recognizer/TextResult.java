package com.nefrock.flex_ocr_android_toolkit.processor.recognizer;

import android.graphics.Rect;


public class TextResult {
    private final String text;
    private final double confidence;
    private Rect boundingBox;

    public TextResult(String text, double confidence, Rect boundingBox) {
        this.text = text;
        this.confidence = confidence;
        this.boundingBox = boundingBox;
    }

    public String getText() {
        return text;
    }

    public double getConfidence() {
        return confidence;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public org.opencv.core.Rect getCvBoundingBox() {
        return new org.opencv.core.Rect(boundingBox.left, boundingBox.top, boundingBox.width(), boundingBox.height());
    }
}
