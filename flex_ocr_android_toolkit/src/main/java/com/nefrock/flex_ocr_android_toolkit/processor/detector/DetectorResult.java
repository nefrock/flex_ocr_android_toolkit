package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;

import java.util.Collections;
import java.util.List;

public class DetectorResult {

    public final int CLS_SHIPPING_LABEL = 0;
    public final int CLS_TEL_1 = 0;

    private final List<Detection> detections;

    public DetectorResult(List<Detection> detections) {
       this.detections = detections;
    }

    public DetectorResult() {
        this.detections = Collections.emptyList();
    }

    public List<Detection> getDetections() {
        return this.detections;
    }

    public boolean hasClass(FlexScanResultType kind) {
        for(Detection d : this.detections) {
            if (d.getDetectionKind() == kind) {
                return true;
            }
        }
        return false;
    }

    public Detection getLargest(FlexScanResultType kind) {
        int area = -1;
        Detection res = null;
        for(Detection d : this.detections) {
            if(d.getDetectionKind() != kind) {
                continue;
            }
            int labelArea = d.area();
            if( labelArea > area) {
                area = labelArea;
                res = d;
            }
        }
        return res;
    }
}
