package com.nefrock.flex.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Size;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResult;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.DetectorKind;
import com.nefrock.flex_ocr_android_toolkit.api.v1.EmptyModelConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexAPI;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.api.v1.OnScanListener;
import com.nefrock.flex_ocr_android_toolkit.api.v1.RecognizerKind;
import com.nefrock.flex_ocr_android_toolkit.processor.recognizer.Recognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Before
    public void setupResources() throws IOException {
        Context appCtx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FlexConfig config = new FlexConfig(appCtx);
        config.setDetector(DetectorKind.IDENTITY,
                new Size(-1,-1), //dummy
                new EmptyModelConfig(),
                null);
        config.setRecognizer(RecognizerKind.ALL_JP,
                new Size(300,300),
                new EmptyModelConfig(),
                null);
        FlexAPI.shared().init(config);
    }

    @Test
    public void scanJapaneseTextsTest() throws IOException {
        Bitmap bitmap = this.readAssetImage("test_images/japanese-sample.jpg");

        //スキャンは非同期で行われる。
        //ここではlatchを使って、スキャンが終わるまで、テストメソッド内でまつ。
        CountDownLatch latch = new CountDownLatch(1);
        FlexScanOption option = new FlexScanOption();
        FlexAPI.shared().scan(bitmap, option, new OnScanListener<FlexScanResults>() {
            @Override
            public void onScan(FlexScanResults results) {
                List<FlexScanResult> details = results.getResults();
                for(FlexScanResult detail : details) {
                    Rect bbox = detail.getBoundingBox();
                    String text = detail.getText();
                    FlexScanResultType typ = detail.getType();
                }
                latch.countDown();
            }
        });
        try {
            latch.await(); //待つ
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Bitmap readAssetImage(String path) throws IOException {
        Context appCtx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        InputStream is = appCtx.getAssets().open(path);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}