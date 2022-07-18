package com.nefrock.flex.app;

import android.app.Application;
import android.util.Size;

import com.nefrock.flex_ocr_android_toolkit.api.v1.DetectorKind;
import com.nefrock.flex_ocr_android_toolkit.api.v1.ModelConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexAPI;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.RecognizerKind;

import java.io.IOException;

public class FlexApplication extends Application {

    private final static String TAG = FlexApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        // モデルファイルはassets以下にあらかじめ配置しておく。pathにはassetsからの相対パスを指定する。
        // モデルの初期化は一度だけ行えばよい
        FlexConfig config = new FlexConfig(this);

        //一般的なテキスト

        ModelConfig detectorConfig = new ModelConfig();

        config.setDetector(DetectorKind.TEXT,
                new Size(320, 320),
                detectorConfig,
                "custom_models/general_spaghettinet_edgetpu_s_320x320.tflite");

        config.setRecognizer(RecognizerKind.FLEX_ALL_JP,
                new Size(200, 31),
                new ModelConfig(),
                "custom_models/flex-crnn-bilstm-handwritten-0620-int8.tflite");
        try {
            FlexAPI.shared().init(config);
        } catch (IOException e) {
            e.printStackTrace();
        }


//
//        //送り状
//        ModelConfig detectorConfig = new ModelConfig();
//
//        config.setDetector(DetectorKind.INVOICE_FAST,
//                new Size(320,320),
//                detectorConfig,
//                "custom_models/spaghettinet_edgetpu_s_320x320.tflite");
//
//        config.setRecognizer(RecognizerKind.INVOICE,
//                new Size(200,31),
//                new ModelConfig(),
//                "custom_models/flex-crnn.tflite");
//        try {
//            FlexAPI.shared().init(config);
//        }catch(IOException e){
//            e.printStackTrace();
//        }

//
        //日本語(画面中央)
//        ModelConfig detectorConfig = new ModelConfig();
//        detectorConfig.setHint("size", new Size(200 * 2, 31 * 2));
//        config.setDetector(DetectorKind.CENTER,
//                new Size(-1,-1),
//                detectorConfig,
//                null);
//        config.setRecognizer(RecognizerKind.FLEX_ALL_JP,
//                new Size(200,31),
//                new ModelConfig(),
//                "custom_models/flex-crnn.tflite");
//        FlexAPI.shared().init(config);

//        日本語(画面全体)
//        config.setDetector(DetectorKind.IDENTITY,
//                new Size(-1,-1),
//                new ModelConfig(),
//                null);
//        config.setRecognizer(RecognizerKind.ALL_JP,
//                new Size(-1, -1),
//                new ModelConfig(),
//                null);
//        FlexAPI.shared().init(config);

        //number plate
//        config.setDetector(DetectorKind.CAR_NUMBER_PLATE,
//                new Size(300, 300),
//                new ModelConfig(),
//                "custom_models/tf-number-plate-detector.tflite");
//        config.setRecognizer(RecognizerKind.CAR_NUMBER_PLATE,
//                new Size(-1,-1),
//                new ModelConfig(), null);
//        try {
//            FlexAPI.shared().init(config);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
