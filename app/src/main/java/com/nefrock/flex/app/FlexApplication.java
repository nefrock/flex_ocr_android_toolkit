package com.nefrock.flex.app;

import android.app.Application;

import com.nefrock.flex_ocr_android_toolkit.api.v0.FlexAPI;
import com.nefrock.flex_ocr_android_toolkit.api.v0.FlexConfig;

public class FlexApplication extends Application {

    private final static String TAG = FlexApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        // モデルファイルはassets以下にあらかじめ配置しておく。pathにはassetsからの相対パスを指定する。
        // モデルの初期化は一度だけ行えばよい
        FlexConfig flexConfig = new FlexConfig(this, "custom_models/sample.ptl");
        flexConfig.setDetectorModelInputSize(512, 384); //モデルのインプットサイズに合わせる
        FlexAPI.shared().init(flexConfig);
    }
}
