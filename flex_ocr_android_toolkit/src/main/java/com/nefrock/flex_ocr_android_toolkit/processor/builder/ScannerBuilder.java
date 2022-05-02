package com.nefrock.flex_ocr_android_toolkit.processor.builder;

import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexConfig;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Identity;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.TFLiteNumberPlateDetector;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.CarNumberRecognizer;
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
        switch(config.getDetectorKind()) {
            case IDENTITY:
                return new Identity();
            case CAR_NUMBER_PLATE:
                return new TFLiteNumberPlateDetector(
                        config.getContext(),
                        config.getDetectorModelPath(),
                        config.getDetectorInputSize()
                );
            default:
                return new Identity();
        }
    }

    private Recognizer buildRecognizer() {
        switch(config.getRecognizerKind()) {
            case ALL_EN:
                return new NaiveGoogleRecognizer(false);
            case ALL_JP:
                return new NaiveGoogleRecognizer(true);
            case CAR_NUMBER_PLATE:
                return new CarNumberRecognizer();
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