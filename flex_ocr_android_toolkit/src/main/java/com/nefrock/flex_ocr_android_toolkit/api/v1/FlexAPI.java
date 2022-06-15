package com.nefrock.flex_ocr_android_toolkit.api.v1;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.processor.builder.ScannerBuilder;
import com.nefrock.flex_ocr_android_toolkit.processor.scanner.Scanner;
import com.nefrock.flex_ocr_android_toolkit.util.ImageUtils;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

public class FlexAPI {

    private static final FlexAPI singleton = new FlexAPI();
    private Scanner scanner;
    static {
        System.loadLibrary("opencv_java4");
    }

    private FlexAPI() {
    }

    public static FlexAPI shared() {
        return singleton;
    }

    public void init(@NonNull FlexConfig config) throws IOException {
        ScannerBuilder builder = new ScannerBuilder(config);
        this.scanner = builder.build();
        this.scanner.init();
    }

    public void scan(@NonNull ImageProxy image, @NonNull FlexScanOption option , @NonNull OnScanListener<FlexScanResults> listener) {
        Mat mat = ImageUtils.rgba(image);
        scanner.scan(mat, option, listener);
    }

    public void scan(@NonNull Bitmap image, @NonNull FlexScanOption option , @NonNull OnScanListener<FlexScanResults> listener) {
        Mat mat = new Mat();
        Utils.bitmapToMat(image, mat);
        scanner.scan(mat, option, listener);
    }
}
