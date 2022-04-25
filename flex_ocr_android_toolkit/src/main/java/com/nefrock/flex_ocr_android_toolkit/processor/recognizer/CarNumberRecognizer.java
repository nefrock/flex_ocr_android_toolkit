package com.nefrock.flex_ocr_android_toolkit.processor.recognizer;

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
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.nefrock.flex_ocr_android_toolkit.api.FlexExitCode;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResult;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.api.v1.OnScanListener;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detection;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.DetectorResult;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class CarNumberRecognizer implements Recognizer {

    private final TextRecognizer googleTextRecognizer;

    public CarNumberRecognizer() {
        this.googleTextRecognizer =
                TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
    }

    @Override
    public void process(Mat mat, Detector detector, FlexScanOption option, OnScanListener<FlexScanResults> listener) {
        DetectorResult detectorResult = detector.process(mat, option);
        List<Detection> detections = detectorResult.getDetections();
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        InputImage img = InputImage.fromBitmap(bitmap, 0);
        long t1 = SystemClock.uptimeMillis();
        this.googleTextRecognizer.process(img)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        long t2 = SystemClock.uptimeMillis();
                        List<FlexScanResult> results = new ArrayList<>();
                        List<Text.TextBlock> blocks = visionText.getTextBlocks();
                        for (Text.TextBlock block : blocks) {
                            List<Text.Line> lines = block.getLines();
                            for (Text.Line line : lines) {
                                List<Text.Element> elements = line.getElements();
                                for (Text.Element element : elements) {
                                    String text = element.getText();
                                    Rect bbox = element.getBoundingBox();
                                    for (Detection detection : detections) {
                                        if (bbox.intersect(detection.getBoundingBox())) {
                                            FlexScanResultType typ = FlexScanResultType.SOMETHING_NICE;
                                            results.add(new FlexScanResult(typ, text, 1.0, bbox));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        long elapsed = t2 - t1;
                        for(Detection d : detections)  {
                            results.add(new FlexScanResult(
                                    FlexScanResultType.CAR_NUMBER_PLATE,
                                    null,
                                    1.0,
                                    d.getBoundingBox()
                            ));
                        }
                        listener.onScan(new FlexScanResults(results, elapsed));
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                listener.onScan(new FlexScanResults(FlexExitCode.MODEL_ERROR, e));
                            }
                        });
    }

    @Override
    public void init() {
    }

    @Override
    public void close() {

    }
}
