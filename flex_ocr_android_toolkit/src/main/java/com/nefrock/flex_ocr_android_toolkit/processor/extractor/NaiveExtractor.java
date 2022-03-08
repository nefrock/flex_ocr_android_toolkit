package com.nefrock.flex_ocr_android_toolkit.processor.extractor;

import com.nefrock.flex_ocr_android_toolkit.processor.result.BarcodeResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.ScanResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.TextResult;

import java.util.HashSet;
import java.util.Set;

/**
 * 電話番号: 1番大きいもの、もしくは与えられたリストの中にあるもの
 * バーコード: 10文字以上で、1番大きいもの
 */
public class NaiveExtractor implements Extractor {

    private final boolean useTelCandidates;
    private final Set<String> telCandidates;

    public NaiveExtractor() {
        this.useTelCandidates = false;
        this.telCandidates = new HashSet<>();
    }

    public NaiveExtractor(Set<String> telCandidates) {
        this.useTelCandidates = true;
        this.telCandidates = telCandidates;
    }

    @Override
    public TextResult extractTel(ScanResult scanResult) {
        if (useTelCandidates) {
            for (TextResult r : scanResult.getTextResults()) {
                String text = r.getText();
                text = text.replaceAll("O", "0");
                text = text.replaceAll("[^0-9]", "");
                if (telCandidates.contains(text)) {
                    return new TextResult(text, 1.0, r.getBoundingBox());
                }
            }
            return null;
        } else {
            TextResult res = null;
            double maxArea = 0;
            for (TextResult r : scanResult.getTextResults()) {
                double area = r.getCvBoundingBox().area();
                if (maxArea < area) {
                    maxArea = area;
                    res = r;
                }
            }
            if (res == null) {
                return null;
            }
            String text = res.getText().replaceAll("[^0-9]", "");
            return new TextResult(text, 1.0, res.getBoundingBox());
        }
    }

    @Override
    public BarcodeResult extractBarcode(ScanResult scanResult) {
        BarcodeResult res = null;
        double maxArea = 0;
        for (BarcodeResult r : scanResult.getBarcodeResults()) {
            if (r.getText().length() < 10) {
                continue;
            }
            double area = r.getCvBoundingBox().area();
            if (maxArea < area) {
                maxArea = area;
                res = r;
            }
        }

        if (res == null) {
            return null;
        }
        return res;
    }
}
