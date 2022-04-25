package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import android.graphics.Rect;


public class Detection {
    private final double confidence;
    private final Rect boundingBox;
    private final int classID;

    public Detection(Rect boundingBox, double confidence, int classId) {
        this.boundingBox = boundingBox;
        this.confidence = confidence;
        this.classID = classId;
    }

    public double getConfidence() {
        return confidence;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public int getClassID() { return this.classID; }

    public org.opencv.core.Rect getCvBoundingBox() {
        return new org.opencv.core.Rect(boundingBox.left, boundingBox.top, boundingBox.width(), boundingBox.height());
    }

    public int area() {
        return boundingBox.width() * boundingBox.height();
    }
}
