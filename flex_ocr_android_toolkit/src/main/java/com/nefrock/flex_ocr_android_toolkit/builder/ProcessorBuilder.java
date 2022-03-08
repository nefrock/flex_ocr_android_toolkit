package com.nefrock.flex_ocr_android_toolkit.builder;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nefrock.flex_ocr_android_toolkit.api.v0.FlexConfig;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.LabelTelDetector;
import com.nefrock.flex_ocr_android_toolkit.processor.scanner.HybridScanner;

public class ProcessorBuilder {

    private final FlexConfig config;

    public ProcessorBuilder(FlexConfig config) {
        this.config = config;
    }

    public HybridScanner buildScanner() throws IOException {
        String filePath = config.getDetectorModelPath();
        File file = new File(filePath);
        String modelPath = assetFilePath(filePath, file.getName());
        Detector detector = new LabelTelDetector(modelPath,
                0.05,
                0.05,
                config.getDetectorModelInputHeight(),
                config.getDetectorModelInputWidth(),
                0.05f, 0.05f);
        return new HybridScanner(detector);
    }

    String assetFilePath(String assetName, String modelName) throws IOException {
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
