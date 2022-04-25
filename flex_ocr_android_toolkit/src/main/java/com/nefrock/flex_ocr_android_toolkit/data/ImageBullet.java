package com.nefrock.flex_ocr_android_toolkit.data;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import androidx.camera.core.ImageProxy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static com.nefrock.flex_ocr_android_toolkit.util.ImageUtils.imageToToBitmap;

public class ImageBullet {
    private final String fileName;
    private final String dirName;
    private final String key1;
    private final String key2;
    private final Bitmap bitmap;
    static final String ROOT_BUCKET_PREFIX="flex-repository";

    public ImageBullet(String name, ImageKind kind, String key1, String key2, Bitmap bitmap) {
        this.key1 = key1;
        this.key2 = key2;
        this.bitmap = bitmap;
        final Date now = new Date(System.currentTimeMillis());
        Locale japan = new Locale("ja", "JP", "JP");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", japan);
        UUID uuid = UUID.randomUUID();
        this.dirName = ROOT_BUCKET_PREFIX + "/" + name + "/" + kind.toString() + "/" + df.format(now);
        this.fileName =  uuid.toString() + ".jpg";
    }

    @SuppressLint("UnsafeOptInUsageError")
    public ImageBullet(String name, ImageKind kind, String key1, String key2, ImageProxy imageProxy) {
        this(name, kind, key1, key2, imageToToBitmap(imageProxy.getImage(), 0));
    }

    String getFileName() {
        return fileName;
    }

    String getBucketName() {
        return dirName;
    }

    String getKey1 () {
        return key1;
    }

    String getKey2() {
        return key2;
    }

    Bitmap getImage() {
        return bitmap;
    }
}
