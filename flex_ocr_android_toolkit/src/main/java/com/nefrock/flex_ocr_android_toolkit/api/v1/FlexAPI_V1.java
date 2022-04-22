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
import com.nefrock.flex_ocr_android_toolkit.api.v0.FlexConfig;

import java.util.ArrayList;
import java.util.List;

public class FlexAPI_V1 {

    private static final FlexAPI_V1 singleton = new FlexAPI_V1();
    private TextRecognizer googleTextRecognizer;

    static {
        System.loadLibrary("opencv_java4");
    }

    private FlexAPI_V1() {
    }

    public static FlexAPI_V1 shared() {
        return singleton;
    }

    public void init(@NonNull FlexConfig config) {
        this.googleTextRecognizer =
                TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
    }

    /**
     * アルファ版のI/F。シグニチャは今後よくよく考えること。
     * @param bitmap
     * @param listener
     */
    public void scan(@NonNull Bitmap bitmap, OnScanListener<FlexScanResults> listener) {
        long t1 = SystemClock.uptimeMillis();
        InputImage img = InputImage.fromBitmap(bitmap, 0);
        this.googleTextRecognizer.process(img)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        long t2 = SystemClock.uptimeMillis();
                        List<FlexScanResult> results = new ArrayList<>();
                        List<Text.TextBlock> blocks = visionText.getTextBlocks();
                        for(Text.TextBlock block : blocks) {
                            List<Text.Line> lines = block.getLines();
                            for(Text.Line line : lines){
                                List<Text.Element> elements = line.getElements();
                                for(Text.Element element: elements) {
                                    String text = element.getText();
                                    Rect bbox = element.getBoundingBox();
                                    FlexScanResultType typ = FlexScanResultType.SOMETHING_NICE;
                                    results.add(new FlexScanResult(typ, text, 1.0, bbox));
                                }
                            }
                        }
                        long elapsed = t2 - t1;
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


}
