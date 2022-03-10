package com.nefrock.flex_ocr_android_toolkit;

import org.junit.Test;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.platform.app.InstrumentationRegistry;


import com.nefrock.flex_ocr_android_toolkit.api.v0.FlexAPI;
import com.nefrock.flex_ocr_android_toolkit.data.ImageKind;

import java.io.IOException;
import java.io.InputStream;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest  {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}