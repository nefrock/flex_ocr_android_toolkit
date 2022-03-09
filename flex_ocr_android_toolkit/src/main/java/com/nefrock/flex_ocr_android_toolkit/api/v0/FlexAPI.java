package com.nefrock.flex_ocr_android_toolkit.api.v0;

import android.graphics.Bitmap;

import androidx.camera.core.ImageProxy;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.nefrock.flex_ocr_android_toolkit.api.FlexExitCode;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResult;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.builder.ProcessorBuilder;
import com.nefrock.flex_ocr_android_toolkit.processor.extractor.NaiveExtractor;
import com.nefrock.flex_ocr_android_toolkit.processor.result.BarcodeResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.Detection;
import com.nefrock.flex_ocr_android_toolkit.processor.result.ScanResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.TextResult;
import com.nefrock.flex_ocr_android_toolkit.processor.scanner.HybridScanner;
import com.nefrock.flex_ocr_android_toolkit.util.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlexAPI {

    private static final FlexAPI singleton = new FlexAPI();
    private HybridScanner scanner;

    static {
        System.loadLibrary("opencv_java4");
    }

    private FlexAPI() {
    }

    public static FlexAPI shared() {
        return singleton;
    }

    public void init(FlexConfig config) {
        ProcessorBuilder builder = new ProcessorBuilder(config);
        try {
            this.scanner = builder.buildScanner();
            this.scanner.init();
        }catch (IOException e) {
            e.printStackTrace();
            this.scanner = null;
        }
    }

    public FlexScanResults scan(ImageProxy imageProxy, FlexScanOption option) {
        Mat rgb = ImageUtils.rgba(imageProxy);
        if (scanner == null) {
            return new FlexScanResults(new ArrayList<>(), FlexExitCode.NOT_INITIALIZED, 0);
        }
        ScanResult rawResult = scanner.process(rgb);
        if(rawResult == null) {
            return new FlexScanResults(new ArrayList<>(), FlexExitCode.MODEL_ERROR, 0);
        }
        return buildFlexScanResults(rawResult, option);
    }

    public FlexScanResults scan(Bitmap bitmap, FlexScanOption option) {
        Mat rgb = new Mat();
        //bitMapToMat should return rgb
        Utils.bitmapToMat(bitmap, rgb);

        double[] ones = rgb.get(0,0);

        if (scanner == null) {
            return new FlexScanResults(new ArrayList<>(), FlexExitCode.NOT_INITIALIZED, 0);
        }
        ScanResult rawResult = scanner.process(rgb);
        if(rawResult == null) {
            return new FlexScanResults(new ArrayList<>(), FlexExitCode.MODEL_ERROR, 0);
        }
        return buildFlexScanResults(rawResult, option);
    }

    private static  FlexScanResults buildFlexScanResults(ScanResult result, FlexScanOption option) {
        NaiveExtractor extractor;
        if(option.getWhiteList() == null) {
            extractor = new NaiveExtractor();
        } else {
            extractor = new NaiveExtractor(option.getWhiteList());
        }
        ArrayList<FlexScanResult> results = new ArrayList<>();

        //tel
        TextResult telResult = extractor.extractTel(result);
        if (telResult != null) {
            FlexScanResult r = new FlexScanResult(FlexScanResultType.TELEPHONE_NUMBER,
                    telResult.getText(),
                    telResult.getConfidence(),
                    telResult.getBoundingBox());
            results.add(r);
        }

        //barcode
        List<BarcodeResult> barcodeResults = result.getBarcodeResults();
        for(BarcodeResult barcodeResult:barcodeResults) {
            FlexScanResult r = new FlexScanResult(FlexScanResultType.BARCODE,
                    barcodeResult.getText(),
                    barcodeResult.getConfidence(),
                    barcodeResult.getBoundingBox());
            results.add(r);
        }

        //label
        Detection labelDetection = result.getLabelDetection();
        if(labelDetection != null) {
            FlexScanResult r = new FlexScanResult(FlexScanResultType.INVOICE_LABEL,
                    null,
                    labelDetection.getConfidence(),
                    labelDetection.getBoundingBox());
            results.add(r);
        }
        return new FlexScanResults(results, FlexExitCode.DONE, result.getElapsedTime());
    }
}
