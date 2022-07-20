package com.nefrock.flex_ocr_android_toolkit.processor.builder;

import android.util.Size;

import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexConfig;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Center;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Identity;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.FastLabelTelDetector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.NumberPlateDetector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.TextDetector;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.CarNumberRecognizer;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.GeneralPurposeRecognizer;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.LabelTelRecognizer;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.NaiveGoogleRecognizer;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.Recognizer;
import com.nefrock.flex_ocr_android_toolkit.processor.scanner.Scanner;
import com.nefrock.flex_ocr_android_toolkit.processor.scanner.DetectorRecognizerScanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ScannerBuilder {

    private final FlexConfig config;

    public ScannerBuilder(FlexConfig config) {
        this.config = config;
    }

    public Scanner build() {
        Detector detector = buildDetector();
        Recognizer recognizer = buildRecognizer();
        Scanner scanner = new DetectorRecognizerScanner(detector, recognizer);
        return scanner;
    }

    private Detector buildDetector() {
        switch (config.getDetectorKind()) {
            case IDENTITY:
                return new Identity();
            case CENTER:
                return new Center((Size) config.getDetectorConfig().getHint("size"));
            case CAR_NUMBER_PLATE:
                TextDetector textDetector =  new TextDetector(config.getContext(),
                    config.getDetectorModelPaths().get(1),
                    config.getDetectorInputSizes().get(1));
                return new NumberPlateDetector(
                        config.getContext(),
                        config.getDetectorModelPaths().get(0),
                        config.getDetectorInputSizes().get(0),
                        textDetector
                );
            case INVOICE:
                return new FastLabelTelDetector(config.getContext(),
                        config.getDetectorModelPath(),
                        config.getDetectorInputSize());
            case TEXT:
                return new TextDetector(config.getContext(),
                        config.getDetectorModelPath(),
                        config.getDetectorInputSize());
            default:
                return new Identity();
        }
    }

    private Recognizer buildRecognizer() {
        switch (config.getRecognizerKind()) {
            case G_ALL_EN:
                return new NaiveGoogleRecognizer(false);
            case G_ALL_JP:
                return new NaiveGoogleRecognizer(true);
            case CAR_NUMBER_PLATE:
                return new CarNumberRecognizer();
            case FLEX_ALL_JP:
                return new GeneralPurposeRecognizer(config.getContext(), config.getRecognizerModelPath(), config.getRecognizerInputSize());
            case INVOICE:
                return new LabelTelRecognizer(config.getContext(), config.getRecognizerModelPath(), config.getRecognizerInputSize());
            default:
                return null;
        }
    }

//    public HybridScanner buildScanner() throws IOException {
//        String filePath = config.getDetectorModelPath();
//        File file = new File(filePath);
//        String modelPath = assetFilePath(filePath, file.getName());
//        Detector detector = new LabelTelDetector(modelPath,
//                0.05,
//                0.05,
//                config.getDetectorModelInputHeight(),
//                config.getDetectorModelInputWidth(),
//                0.05f, 0.05f);
//        return new HybridScanner(detector);
//    }

    private String assetFilePath(String assetName, String modelName) throws IOException {
        File file = new File(config.getContext().getFilesDir(), modelName);
        if (file.exists() && file.length() > 0) {
            file.delete();
        }

        try (InputStream is = config.getContext().getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}
