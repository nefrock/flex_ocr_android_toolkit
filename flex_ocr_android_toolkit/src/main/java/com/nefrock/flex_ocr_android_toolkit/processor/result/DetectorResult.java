package com.nefrock.flex_ocr_android_toolkit.processor.result;

import java.util.ArrayList;
import java.util.List;

public class DetectorResult {

    public final int CLS_SHIPPING_LABEL = 0;
    public final int CLS_TEL_1 = 0;

    private final List<Detection> labels;
    private final List<Detection> tels;
    private final List<Detection> all;

    public DetectorResult(List<Detection> labels, List<Detection> tels) {
        this.labels = labels;
        this.tels = tels;
        this.all = new ArrayList<>();
        this.all.addAll(labels);
        this.all.addAll(tels);
    }

    public List<Detection> getTels() {
        return this.tels;
    }

    public List<Detection> getLabels() { return this.labels;}

    public boolean hasLabel() {
        return this.labels.size() > 0;
    }

    public Detection getLargestLabel() {
        assert hasLabel();
        int area = -1;
        Detection res = null;
        for(Detection label : this.labels) {
            int labelArea = label.area();
            if( labelArea > area) {
                area = labelArea;
                res = label;
            }
        }
        return res;
    }
}
