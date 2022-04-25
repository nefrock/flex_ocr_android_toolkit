package com.nefrock.flex.app;

import android.app.Application;

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
        config.setDetector(DetectorKind.IDENTITY, new EmptyModelConfig(), null);
        config.setRecognizer(RecognizerKind.ALL_JP, new EmptyModelConfig(), null);
        FlexAPI.shared().init(config);
    }
}
