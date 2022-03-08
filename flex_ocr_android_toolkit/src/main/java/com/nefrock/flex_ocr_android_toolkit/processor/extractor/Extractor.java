package com.nefrock.flex_ocr_android_toolkit.processor.extractor;


import com.nefrock.flex_ocr_android_toolkit.processor.result.BarcodeResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.ScanResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.TextResult;

public interface Extractor {
    TextResult extractTel(ScanResult scanResult);
    BarcodeResult extractBarcode(ScanResult scanResult);
}
