package com.nefrock.flex_ocr_android_toolkit.api.v1;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.nefrock.flex_ocr_android_toolkit.api.FlexExitCode;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResult;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.processor.builder.ScannerBuilder;
import com.nefrock.flex_ocr_android_toolkit.processor.scanner.Scanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void init(@NonNull FlexConfig config) {
        ScannerBuilder builder = new ScannerBuilder(config);
        this.scanner = builder.build();
    }

    /**
     * アルファ版のI/F。シグニチャは今後よくよく考えること。
     * @param bitmap
     * @param listener
     */
    public void scan(@NonNull Bitmap bitmap, FlexScanOption option , @NonNull OnScanListener<FlexScanResults> listener) {
        scanner.scan(bitmap, option, listener);
    }
}
