package com.nefrock.flex_ocr_android_toolkit.processor.detector;

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

    public boolean hasClass(int cls) {
        for(Detection d : this.detections) {
            if (d.getClassID() == cls) {
                return true;
            }
        }
        return false;
    }

    public Detection getLargest(int cls) {
        int area = -1;
        Detection res = null;
        for(Detection d : this.detections) {
            if(d.getClassID() != cls) {
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
