package com.nefrock.flex_ocr_android_toolkit.processor.result;

import android.graphics.Bitmap;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;


public class ScanResult {

    private DetectorResult detectorResult;
    private Detection label;
    private List<TextResult> textResults;
    private List<BarcodeResult> barcodeResults;
    private String debugText;
    private final long startTime;
    private long endTime;
    private Bitmap bitmap;

    public ScanResult() {
        barcodeResults = new ArrayList<>();
        label = null;
        textResults = new ArrayList<>();
        detectorResult = new DetectorResult(new ArrayList<>(), new ArrayList<>());
        startTime = SystemClock.uptimeMillis();
        endTime = 0;
        debugText = "";
        bitmap = null;
    }

    public Detection getLabelDetection() {
        return label;
    }

    public boolean hasLabelDetection() {
        return this.detectorResult.hasLabel();
    }

    public boolean hasTelDetections() {
        return this.detectorResult.getTels().size() > 0;
    }

    public void setDetectorResult(DetectorResult detectorResult) {
        this.detectorResult = detectorResult;
        if (this.detectorResult.hasLabel()) {
            this.label = detectorResult.getLargestLabel();
        }
    }

    public List<Detection> getTelDetections() {
        ArrayList<Detection> res = new ArrayList<>();
        if (label == null) {
            return res;
        }
        Detection label = this.getLabelDetection();

        for (Detection r : this.detectorResult.getTels()) {
            if (label.getBoundingBox().contains(r.getBoundingBox())) {
                res.add(r);
            }
        }
        return res;
    }

    public List<TextResult> getTextResults() {
        if (label == null) {
            return  new ArrayList<>();
        }
        return this.textResults;
    }

    public void addTextResult(TextResult textResult) {
        this.textResults.add(textResult);
    }

    public void addTextResults(List<TextResult> textResults) {
        this.textResults.addAll(textResults);
    }

    public List<BarcodeResult> getBarcodeResults() {
        return this.barcodeResults;
    }

    public void setBarcodeResults(List<BarcodeResult> barcodeResults) {
        this.barcodeResults = barcodeResults;
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public String getElapsedTimeString() {
        return String.valueOf(getElapsedTime());
    }

    public void end() {
        this.endTime = SystemClock.uptimeMillis();
    }

    public String getDebugText() {
        return debugText;
    }

    public void setDebugText(String debugText) {
        this.debugText = debugText;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
