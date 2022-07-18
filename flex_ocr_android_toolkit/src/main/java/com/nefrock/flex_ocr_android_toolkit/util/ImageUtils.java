package com.nefrock.flex_ocr_android_toolkit.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import androidx.camera.core.ImageProxy;
import androidx.camera.core.internal.utils.ImageUtil;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageUtils {

    public static Mat rgba(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        int w = image.getWidth();
        int h = image.getHeight();
        int chromaPixelStride = planes[1].getPixelStride();
        Mat mRgba = new Mat();

        if (chromaPixelStride == 2) { // Chroma channels are interleaved
            assert (planes[0].getPixelStride() == 1);
            assert (planes[2].getPixelStride() == 2);
            ByteBuffer y_plane = planes[0].getBuffer();
            int y_plane_step = planes[0].getRowStride();
            ByteBuffer uv_plane1 = planes[1].getBuffer();
            int uv_plane1_step = planes[1].getRowStride();
            ByteBuffer uv_plane2 = planes[2].getBuffer();
            int uv_plane2_step = planes[2].getRowStride();
            Mat y_mat = new Mat(h, w, CvType.CV_8UC1, y_plane, y_plane_step);
            Mat uv_mat1 = new Mat(h / 2, w / 2, CvType.CV_8UC2, uv_plane1, uv_plane1_step);
            Mat uv_mat2 = new Mat(h / 2, w / 2, CvType.CV_8UC2, uv_plane2, uv_plane2_step);
            long addr_diff = uv_mat2.dataAddr() - uv_mat1.dataAddr();
            if (addr_diff > 0) {
                assert (addr_diff == 1);
                Imgproc.cvtColorTwoPlane(y_mat, uv_mat1, mRgba, Imgproc.COLOR_YUV2RGBA_NV12);
            } else {
                assert (addr_diff == -1);
                Imgproc.cvtColorTwoPlane(y_mat, uv_mat2, mRgba, Imgproc.COLOR_YUV2RGBA_NV21);
            }
            return mRgba;
        } else { // Chroma channels are not interleaved
            byte[] yuv_bytes = new byte[w * (h + h / 2)];
            ByteBuffer y_plane = planes[0].getBuffer();
            ByteBuffer u_plane = planes[1].getBuffer();
            ByteBuffer v_plane = planes[2].getBuffer();

            int yuv_bytes_offset = 0;

            int y_plane_step = planes[0].getRowStride();
            if (y_plane_step == w) {
                y_plane.get(yuv_bytes, 0, w * h);
                yuv_bytes_offset = w * h;
            } else {
                int padding = y_plane_step - w;
                for (int i = 0; i < h; i++) {
                    y_plane.get(yuv_bytes, yuv_bytes_offset, w);
                    yuv_bytes_offset += w;
                    if (i < h - 1) {
                        y_plane.position(y_plane.position() + padding);
                    }
                }
                assert (yuv_bytes_offset == w * h);
            }

            int chromaRowStride = planes[1].getRowStride();
            int chromaRowPadding = chromaRowStride - w / 2;

            if (chromaRowPadding == 0) {
                // When the row stride of the chroma channels equals their width, we can copy
                // the entire channels in one go
                u_plane.get(yuv_bytes, yuv_bytes_offset, w * h / 4);
                yuv_bytes_offset += w * h / 4;
                v_plane.get(yuv_bytes, yuv_bytes_offset, w * h / 4);
            } else {
                // When not equal, we need to copy the channels row by row
                for (int i = 0; i < h / 2; i++) {
                    u_plane.get(yuv_bytes, yuv_bytes_offset, w / 2);
                    yuv_bytes_offset += w / 2;
                    if (i < h / 2 - 1) {
                        u_plane.position(u_plane.position() + chromaRowPadding);
                    }
                }
                for (int i = 0; i < h / 2; i++) {
                    v_plane.get(yuv_bytes, yuv_bytes_offset, w / 2);
                    yuv_bytes_offset += w / 2;
                    if (i < h / 2 - 1) {
                        v_plane.position(v_plane.position() + chromaRowPadding);
                    }
                }
            }
            Mat yuv_mat = new Mat(h + h / 2, w, CvType.CV_8UC1);
            yuv_mat.put(0, 0, yuv_bytes);
            Imgproc.cvtColor(yuv_mat, mRgba, Imgproc.COLOR_YUV2RGBA_I420, 4);
            return mRgba;
        }
    }

    // アスペクト比を保ってリサイズする。
    // 残った部分は0でパディングする。
    public static Mat resizeWithPad(Mat image, Size size) {
        double imgH = image.height();
        double imgW = image.width();
        double imgRatio = imgH / imgW;
        double targetH = size.height;
        double targetW = size.width;
        double targetRatio = targetH / targetW;
        Mat res = new Mat();
        if (imgRatio > targetRatio) {
            //縦長の画像
            int resizeH = (int) targetH;
            int resizeW = (int) ((targetH / imgH) * imgW);

            int padding = (int) (targetW - resizeW);
            int paddingLeft = padding / 2;
            int paddingRight = padding / 2;

            if (padding % 2 != 0) {
                //奇数の場合は右に1つパディングを増やす
                paddingRight += 1;
            }
            Mat resized = new Mat();
            Imgproc.resize(image, resized, new Size(resizeW, resizeH));

            Core.copyMakeBorder(resized, res, 0, 0, paddingLeft, paddingRight, Core.BORDER_CONSTANT, new Scalar(0,0,0, 255));
        } else {
            //横長の画像
            int resizeW = (int) targetW;
            int resizeH = (int) ((targetW / imgW) * imgH);

            int padding = (int) (targetH - resizeH);
            int paddingTop = padding / 2;
            int paddingBottom = padding / 2;
            if (padding % 2 != 0) {
                paddingTop += 1;
            }
            Mat resized = new Mat();
            Imgproc.resize(image, resized, new Size(resizeW, resizeH));
            Core.copyMakeBorder(resized, res, paddingTop, paddingBottom, 0, 0, Core.BORDER_CONSTANT, new Scalar(0,0,0, 255));
        }
        // just for debug;
        // Bitmap bitmap = Bitmap.createBitmap((int) size.width, (int) size.height, Bitmap.Config.ARGB_8888);
        // Utils.matToBitmap(res, bitmap);
        return res;
    }

    // ImageProxy → Bitmap
    public static Bitmap imageToToBitmap(Image image, int rotationDegrees) {
        byte[] data = imageToByteArray(image);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (rotationDegrees == 0) {
            return bitmap;
        } else {
            return rotateBitmap(bitmap, rotationDegrees);
        }
    }

    // Bitmapの回転
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        Matrix mat = new Matrix();
        mat.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), mat, true);
    }

    // Image → JPEGのバイト配列
    private static byte[] imageToByteArray(Image image) {
        byte[] data = null;
        if (image.getFormat() == ImageFormat.JPEG) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            data = new byte[buffer.capacity()];
            buffer.get(data);
            return data;
        } else if (image.getFormat() == ImageFormat.YUV_420_888) {
            data = NV21toJPEG(YUV_420_888toNV21(image),
                    image.getWidth(), image.getHeight());
        }
        return data;
    }

    // YUV_420_888 → NV21
    private static byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();
        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();
        nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);
        return nv21;
    }

    // NV21 → JPEG
    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }
}