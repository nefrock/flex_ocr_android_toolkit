package com.nefrock.flex_ocr_android_toolkit.util;

import android.graphics.Bitmap;
import android.media.Image;

import org.pytorch.MemoryFormat;
import org.pytorch.Tensor;

import java.nio.FloatBuffer;
import java.util.List;


/**
 * org.pytorch.torchvision.TensorImageUtilsからコピーして改変した
 * <p>
 * Contains utility functions for {@link Tensor} creation from {@link
 * Bitmap} or {@link Image} source.
 */
public final class TensorImageUtils {

    public static Tensor bitmapToFloat32Tensor(final List<Bitmap> bitmaps, int width, int height, float mean, float stddev, final MemoryFormat memoryFormat) {
        int batchDim = bitmaps.size();
        int imageSize = width * height * 3;
        final FloatBuffer floatBuffer = Tensor.allocateFloatBuffer(batchDim * imageSize);
        for (int i = 0; i < batchDim; ++i) {
            Bitmap bitmap = bitmaps.get(i);
            bitmapToFloatBuffer(bitmap, 0, 0, width, height, mean, stddev, floatBuffer, imageSize * i, memoryFormat);
        }
        return Tensor.fromBlob(floatBuffer, new long[]{batchDim, 3, height, width}, memoryFormat);
    }

    /**
     * Creates new {@link Tensor} from full {@link Bitmap}, normalized
     * with specified in parameters mean and std.
     */
    public static Tensor bitmapToFloat32Tensor(
            final Bitmap bitmap,
            final MemoryFormat memoryFormat) {
        return bitmapToFloat32Tensor(
                bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), memoryFormat);
    }

    /**
     * Creates new {@link Tensor} from specified area of {@link Bitmap},
     * normalized with specified in parameters mean and std.
     *
     * @param bitmap {@link Bitmap} as a source for Tensor data
     * @param x      - x coordinate of top left corner of bitmap's area
     * @param y      - y coordinate of top left corner of bitmap's area
     * @param width  - width of bitmap's area
     * @param height - height of bitmap's area
     */
    public static Tensor bitmapToFloat32Tensor(
            final Bitmap bitmap,
            int x,
            int y,
            int width,
            int height,
            MemoryFormat memoryFormat) {

        final FloatBuffer floatBuffer = Tensor.allocateFloatBuffer(3 * width * height);
        bitmapToFloatBuffer(
                bitmap, x, y, width, height, floatBuffer, 0, memoryFormat);
        return Tensor.fromBlob(floatBuffer, new long[]{1, 3, height, width}, memoryFormat);
    }

    /**
     * 正規化をやめた
     * <p>
     * Writes tensor content from specified {@link Bitmap}, normalized with specified
     * in parameters mean and std to specified {@link FloatBuffer} with specified offset.
     *
     * @param bitmap {@link Bitmap} as a source for Tensor data
     * @param x      - x coordinate of top left corner of bitmap's area
     * @param y      - y coordinate of top left corner of bitmap's area
     * @param width  - width of bitmap's area
     * @param height - height of bitmap's area
     */
    public static void bitmapToFloatBuffer(
            final Bitmap bitmap,
            final int x,
            final int y,
            final int width,
            final int height,
            final FloatBuffer outBuffer,
            final int outBufferOffset,
            final MemoryFormat memoryFormat) {

        if (memoryFormat != MemoryFormat.CONTIGUOUS && memoryFormat != MemoryFormat.CHANNELS_LAST) {
            throw new IllegalArgumentException("Unsupported memory format " + memoryFormat);
        }

        final int pixelsCount = height * width;
        final int[] pixels = new int[pixelsCount];
        bitmap.getPixels(pixels, 0, width, x, y, width, height);
        if (MemoryFormat.CONTIGUOUS == memoryFormat) {
            final int offset_g = pixelsCount;
            final int offset_b = 2 * pixelsCount;
            for (int i = 0; i < pixelsCount; i++) {
                final int c = pixels[i];
                float r = ((c >> 16) & 0xff);
                float g = ((c >> 8) & 0xff);
                float b = ((c) & 0xff);
                outBuffer.put(outBufferOffset + i, r);
                outBuffer.put(outBufferOffset + offset_g + i, g);
                outBuffer.put(outBufferOffset + offset_b + i, b);
            }
        } else {
            for (int i = 0; i < pixelsCount; i++) {
                final int c = pixels[i];
                float r = ((c >> 16) & 0xff);
                float g = ((c >> 8) & 0xff);
                float b = ((c) & 0xff);
                outBuffer.put(outBufferOffset + 3 * i + 0, r);
                outBuffer.put(outBufferOffset + 3 * i + 1, g);
                outBuffer.put(outBufferOffset + 3 * i + 2, b);
            }
        }
    }

    /**
     * 正規化バージョン
     * <p>
     * Writes tensor content from specified {@link Bitmap}, normalized with specified
     * in parameters mean and std to specified {@link FloatBuffer} with specified offset.
     *
     * @param bitmap {@link Bitmap} as a source for Tensor data
     * @param x      - x coordinate of top left corner of bitmap's area
     * @param y      - y coordinate of top left corner of bitmap's area
     * @param width  - width of bitmap's area
     * @param height - height of bitmap's area
     */
    public static void bitmapToFloatBuffer(
            final Bitmap bitmap,
            final int x,
            final int y,
            final int width,
            final int height,
            final float mean,
            final float stddev,
            final FloatBuffer outBuffer,
            final int outBufferOffset,
            final MemoryFormat memoryFormat) {

        if (memoryFormat != MemoryFormat.CONTIGUOUS && memoryFormat != MemoryFormat.CHANNELS_LAST) {
            throw new IllegalArgumentException("Unsupported memory format " + memoryFormat);
        }

        final int pixelsCount = height * width;
        final int[] pixels = new int[pixelsCount];
        bitmap.getPixels(pixels, 0, width, x, y, width, height);
        if (MemoryFormat.CONTIGUOUS == memoryFormat) {
            final int offset_g = pixelsCount;
            final int offset_b = 2 * pixelsCount;
            for (int i = 0; i < pixelsCount; i++) {
                final int c = pixels[i];
                float r = (((c >> 16) & 0xff) / 255.f - mean) / stddev;
                float g = (((c >> 8) & 0xff) / 255.f - mean) / stddev;
                float b = (((c) & 0xff) / 255.f - mean) / stddev;
                outBuffer.put(outBufferOffset + i, r);
                outBuffer.put(outBufferOffset + offset_g + i, g);
                outBuffer.put(outBufferOffset + offset_b + i, b);
            }
        } else {
            for (int i = 0; i < pixelsCount; i++) {
                final int c = pixels[i];
                float r = (((c >> 16) & 0xff) / 255.f - mean) / stddev;
                float g = (((c >> 8) & 0xff) / 255.f - mean) / stddev;
                float b = (((c) & 0xff) / 255.f - mean) / stddev;
                outBuffer.put(outBufferOffset + 3 * i + 0, r);
                outBuffer.put(outBufferOffset + 3 * i + 1, g);
                outBuffer.put(outBufferOffset + 3 * i + 2, b);
            }
        }
    }
}
