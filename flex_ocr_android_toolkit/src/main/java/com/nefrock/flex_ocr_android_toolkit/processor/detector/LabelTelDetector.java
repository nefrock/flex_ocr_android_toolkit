package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

import com.nefrock.flex_ocr_android_toolkit.processor.result.Detection;
import com.nefrock.flex_ocr_android_toolkit.processor.result.DetectorResult;
import com.nefrock.flex_ocr_android_toolkit.util.TensorImageUtils;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Rect2d;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.util.ArrayList;
import java.util.List;

//import org.pytorch.LiteModuleLoader;

public class LabelTelDetector implements Detector {

    private Module module;
    private final String modelPath;
    private final double inputX;
    private final double inputY;
    private final float classPredThre;
    private final float nmsThre;
    private final double paddingW;
    private final double paddingH;

    public LabelTelDetector(String modelPath,  double paddingH, double paddingW, double height, double width, float classPredThre, float nmsThre) {
        this.modelPath = modelPath;
        this.paddingH = paddingH;
        this.paddingW = paddingW;
        this.inputX = width;
        this.inputY = height;
        this.classPredThre = classPredThre;
        this.nmsThre = nmsThre;
    }

    @Override
    public DetectorResult process(Mat image, int x0, int y0) {

        long t1  = SystemClock.uptimeMillis();
        final Tensor imageTensor = getTensorWithResize(image, (int) inputX, (int)inputY);
        final Tensor classConfThre = Tensor.fromBlob(new float[]{classPredThre}, new long[]{1});

        final IValue classConfThreValue = IValue.from(classConfThre);
        final IValue imageValue = IValue.from(imageTensor);

        long t2  = SystemClock.uptimeMillis();

        Log.d("LabelTelDetector", "preprocess=" + (t2-t1));

        final Tensor outputTensor = module.forward(imageValue, classConfThreValue).toTensor();
        final float[] detections = outputTensor.getDataAsFloatArray();

        //NMS
        List<Rect2d> labelBboxes = new ArrayList<>();
        List<Float> labelScores = new ArrayList<>();
        List<Rect2d> telBBoxes = new ArrayList<>();
        List<Float> telScores = new ArrayList<>();

        // detections ordered as (x1, y1, x2, y2, obj_conf, class_conf, class_pred)
        int nbDetected = detections.length / 7;
        for (int i = 0; i < nbDetected; ++i) {
            int base = 7 * i;
            float x1 = detections[base];
            float y1 = detections[base + 1];

            float x2 = detections[base + 2];
            float y2 = detections[base + 3];

            float width = x2 - x1;
            float height = y2 - y1;

            Rect2d rect2d = new Rect2d(x1, y1, width, height);
            float score = detections[base + 4] * detections[base + 5];
            int classId = (int) (detections[base + 6] + 0.1);
            if (classId == 0) {
                //label
                labelBboxes.add(rect2d);
                labelScores.add(score);
            } else {
                //tel
                telBBoxes.add(rect2d);
                telScores.add(score);
            }
        }
        List<Detection> labels = this.nms(image, image, labelBboxes, labelScores, 0, x0, y0);
        List<Detection> tels = this.nms(image, image, telBBoxes, telScores, 1, x0, y0);
        return new DetectorResult(labels, tels);
    }


    private List<Detection> nms(Mat image, Mat orig, List<Rect2d> bboxes, List<Float> scores, int classId, int x0, int y0) {
        MatOfRect2d matOfBboxes = new MatOfRect2d();
        matOfBboxes.fromList(bboxes);

        MatOfFloat matOfScores = new MatOfFloat();
        matOfScores.fromList(scores);

        MatOfInt matOfResultIdx = new MatOfInt();
        org.opencv.dnn.Dnn.NMSBoxes(matOfBboxes, matOfScores, this.classPredThre, this.nmsThre, matOfResultIdx);

        List<Detection> res = new ArrayList<>();
        if (matOfResultIdx.empty()) {
            return res;
        }
        double ratioX = inputX / image.width();
        double ratioY = inputY / image.height();

        int[] resultIndex = matOfResultIdx.toArray();
        for (int index : resultIndex) {
            Rect2d bbox = bboxes.get(index);
            float score = scores.get(index);

            int x = (int) (bbox.x / ratioX);
            int y = (int) (bbox.y / ratioY);
            int x1 = (int) ((bbox.x + bbox.width) / ratioX);
            int y1 = (int) ((bbox.y + bbox.height) / ratioY);

            x = x + x0;
            y = y + y0;
            x1 = x1 + x0;
            y1 = y1 + y0;

            int padX = (int) ((x1 - x) * paddingW);
            int padY = (int) ((y1 - y) * paddingH);

            x = Math.max(x - padX, 0);
            x1 = Math.min(x1 + padX, orig.width());
            y = Math.max(y - padY, 0);
            y1 = Math.min(y1 + padY, orig.height());

            Rect rect = new Rect(x, y, x1, y1);
            res.add(new Detection(rect, score, classId));
        }
        return res;
    }

    @Override
    public void init() {
        module = LiteModuleLoader.load(this.modelPath);
//        module = Module.load(this.modelPath);
    }

    @Override
    public void close() {
    }

    public static Tensor getTensorWithResize(Mat image, int x, int y) {
        Size newImageSize = new Size(x, y);
        Mat imgResized = new Mat();
        Imgproc.resize(image, imgResized, newImageSize);
        Bitmap bitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgResized, bitmap);
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, MemoryFormat.CHANNELS_LAST);
    }
}
