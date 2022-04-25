package com.nefrock.flex.app;

import android.app.Application;
import android.util.Size;

import com.nefrock.flex_ocr_android_toolkit.api.v1.DetectorKind;
import com.nefrock.flex_ocr_android_toolkit.api.v1.EmptyModelConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexAPI;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.RecognizerKind;

public class FlexApplication extends Application {

    private final static String TAG = FlexApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        // モデルファイルはassets以下にあらかじめ配置しておく。pathにはassetsからの相対パスを指定する。
        // モデルの初期化は一度だけ行えばよい
        FlexConfig config = new FlexConfig(this);

        //日本語
        config.setDetector(DetectorKind.IDENTITY,
                new Size(-1,-1),
                new EmptyModelConfig(),
                null);
        config.setRecognizer(RecognizerKind.ALL_JP,
                new Size(-1, -1),
                new EmptyModelConfig(),
                null);
        FlexAPI.shared().init(config);

        //number plate
//        config.setDetector(DetectorKind.CAR_NUMBER_PLATE,
//                new Size(300, 300),
//                new EmptyModelConfig(),
//                "custom_models/tf-number-plate-detector.tflite");
//        config.setRecognizer(RecognizerKind.CAR_NUMBER_PLATE,
//                new Size(-1,-1),
//                new EmptyModelConfig(), null);
//        FlexAPI.shared().init(config);
    }
}
