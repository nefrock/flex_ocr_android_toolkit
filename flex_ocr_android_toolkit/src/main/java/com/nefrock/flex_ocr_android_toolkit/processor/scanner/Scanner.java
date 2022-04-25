package com.nefrock.flex_ocr_android_toolkit.processor.scanner;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.api.v1.OnScanListener;

import org.opencv.core.Mat;

public interface Scanner {
    void scan(Bitmap bitmap, FlexScanOption option, OnScanListener<FlexScanResults> listener);
}
