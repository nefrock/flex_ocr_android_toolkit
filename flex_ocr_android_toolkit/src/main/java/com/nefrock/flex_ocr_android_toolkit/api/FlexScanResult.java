package com.nefrock.flex_ocr_android_toolkit.api;
import android.graphics.Rect;

public class FlexScanResult {
    private final FlexScanResultType typ;
    private final String text;
    private final double confidence;
    private final Rect boundingBox;

    public FlexScanResult(FlexScanResultType typ, String text, double confidence, Rect boundingBox) {
        this.typ = typ;
        this.text = text;
        this.confidence = confidence;
        this.boundingBox = boundingBox;
    }

    public FlexScanResultType getType() {
        return typ;
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
}
