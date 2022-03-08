package com.nefrock.flex_ocr_android_toolkit.processor.scanner;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.result.BarcodeResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.Detection;
import com.nefrock.flex_ocr_android_toolkit.processor.result.DetectorResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.ScanResult;
import com.nefrock.flex_ocr_android_toolkit.processor.result.TextResult;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HybridScanner {

    private final Detector labelDetector;
    private final TextRecognizer googleTextRecognizer;
    private final BarcodeScanner barcodeScanner;

    public HybridScanner(Detector detector) {
        this.labelDetector = detector;
        this.googleTextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        BarcodeScannerOptions barcodeScannerOptions =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39, Barcode.FORMAT_CODABAR)
                        .build();
        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);
    }

    public void init() {
        labelDetector.init();
    }

    public ScanResult process(Mat rgb) {
            ScanResult scanResult = new ScanResult();
            DetectorResult detectorResult = labelDetector.process(rgb, 0, 0);
            scanResult.setDetectorResult(detectorResult);

            if (!scanResult.hasLabelDetection() || !scanResult.hasTelDetections()) {
                scanResult.end();
                return scanResult;
            }

            Detection d = scanResult.getLabelDetection();
            Rect bbox = d.getCvBoundingBox();
            Mat cropped = new Mat(rgb, bbox);

            Bitmap bitmap = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cropped, bitmap);
            InputImage labelImage = InputImage.fromBitmap(bitmap, 0);

            List<Detection> tels = scanResult.getTelDetections();
            CountDownLatch latch = new CountDownLatch(tels.size() + 1); //nb tels + barcode
            processBarcodes(labelImage, bbox, scanResult, latch);
            processTexts(rgb, scanResult, latch);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                scanResult.end();
            }
            return scanResult;
    }

    private void processBarcodes(InputImage labelImage, Rect labelRoi, ScanResult scanResult, CountDownLatch latch) {
        barcodeScanner.process(labelImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        List<BarcodeResult> barcodeResults = extractBarcodeResult(barcodes, labelRoi);
                        scanResult.setBarcodeResults(barcodeResults);
                        latch.countDown();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        latch.countDown();
                    }
                });
    }

    private void processTexts(Mat fullImage, ScanResult scanResult, CountDownLatch latch) {
        List<Detection> tels = scanResult.getTelDetections();
        for (Detection tel : tels) {
            Rect bbox = tel.getCvBoundingBox();
            Mat cropped = new Mat(fullImage, bbox);
            Bitmap bitmap = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cropped, bitmap);
            InputImage img = InputImage.fromBitmap(bitmap, 0);
            googleTextRecognizer.process(img)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            TextResult textResult = extractTextResult(tel, visionText);
                            scanResult.addTextResult(textResult);
                            latch.countDown();
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    latch.countDown();
                                }
                            });
        }
    }

    private static List<BarcodeResult> extractBarcodeResult(List<Barcode> barcodes, Rect roi) {
        ArrayList<BarcodeResult> res = new ArrayList<>();
        for (Barcode barcode : barcodes) {
            android.graphics.Rect bbox = barcode.getBoundingBox();
            int x = roi.x + bbox.left;
            int y = roi.y + bbox.top;
            bbox.set(x, y, x + bbox.width(), y + bbox.height());
            String rawValue = barcode.getDisplayValue();
            BarcodeResult result = new BarcodeResult(rawValue, 1.0, bbox);
            res.add(result);
        }
        return res;
    }

    private static TextResult extractTextResult(Detection tel, Text visionText) {
        StringBuilder sb = new StringBuilder();
        for (Text.TextBlock block : visionText.getTextBlocks()) {
            sb.append(block.getText());
        }
        return new TextResult(sb.toString(), 1.0, tel.getBoundingBox());
    }
}
