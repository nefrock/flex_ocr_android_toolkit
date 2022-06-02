package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;

import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.util.TFUtil;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Rect2d;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFLiteNumberPlateDetector implements Detector {

    private Interpreter interpreter;
    private final Context context;
    private final String modelPath;
    private final int inputX;
    private final int inputY;
    private ByteBuffer imgData;

    private static final int NUM_DETECTIONS = 10;
    //    private static final int NUM_DETECTIONS = 1001;
    private int[] intValues;
    // outputLocations: array of shape [Batchsize, NUM_DETECTIONS,4]
    // contains the location of detected boxes
    private float[][][] outputLocations;
    // outputClasses: array of shape [Batchsize, NUM_DETECTIONS]
    // contains the classes of detected boxes
    private float[][] outputClasses;
    // outputScores: array of shape [Batchsize, NUM_DETECTIONS]
    // contains the scores of detected boxes
    private float[][] outputScores;
    // numDetections: array of shape [Batchsize]
    // contains the number of detected boxes
    private float[] numDetections;

    private final float classPredThre;
    private final float nmsThre;
    private final double paddingW;
    private final double paddingH;

    public TFLiteNumberPlateDetector(Context context,
                                     String modelPath,
                                     Size inputSize) {
        this.context = context;
        this.modelPath = modelPath;
        this.paddingH = 0;
        this.paddingW = 0;
        this.inputX = inputSize.getWidth();
        this.inputY = inputSize.getHeight();
        this.classPredThre = 0.1f;
        this.nmsThre = 0.3f;
    }

    @Override
    public DetectorResult process(Mat mat, FlexScanOption option) {
        Mat imgResized = new Mat();
        Imgproc.resize(mat, imgResized, new org.opencv.core.Size(inputX, inputY));
        Bitmap bitmap = Bitmap.createBitmap(inputX, inputY, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgResized, bitmap);
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imgData.rewind();
        for (int i = 0; i < inputY; ++i) {
            for (int j = 0; j < inputX; ++j) {
                int pixelValue = intValues[i * inputX + j];
                imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                imgData.put((byte) (pixelValue & 0xFF));
            }
        }

        // Copy the input data into TensorFlow.
        outputLocations = new float[1][NUM_DETECTIONS][4];
        outputClasses = new float[1][NUM_DETECTIONS];
        outputScores = new float[1][NUM_DETECTIONS];
        numDetections = new float[1];
        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, outputLocations);
        outputMap.put(1, outputClasses);
        outputMap.put(2, outputScores);
        outputMap.put(3, numDetections);
        Object[] inputArray = {imgData};
        // Run the inference call.
        long t1 = SystemClock.uptimeMillis();
        interpreter.runForMultipleInputsOutputs(inputArray, outputMap);
        long t2 = SystemClock.uptimeMillis();
        Log.d("TFLiteLabelTelDetector", "process=" + (t2 - t1));

        int numDetectionsOutput = min(NUM_DETECTIONS, (int) numDetections[0]);

        //NMS
        List<Rect2d> bboxes = new ArrayList<>();
        List<Float> scores = new ArrayList<>();

        int origWidth = mat.width();
        int origHeight = mat.height();

        for (int i = 0; i < numDetectionsOutput; ++i) {
            float score = outputScores[0][i];
            int x = (int) (outputLocations[0][i][1] * origWidth);
            int y = (int) (outputLocations[0][i][0] * origHeight);
            int x1 = (int) (outputLocations[0][i][3] * origWidth);
            int y1 = (int) (outputLocations[0][i][2] * origHeight);
            x = Math.max(x, 0);
            x1 = Math.min(x1, origWidth);
            y = Math.max(y, 0);
            y1 = Math.min(y1, origHeight);

            final Rect2d bbox = new Rect2d(x, y, x1 - x, y1 - y);
            bboxes.add(bbox);
            scores.add(score);
        }
        List<Detection> detections = NMS.nms(mat, bboxes, scores, this.classPredThre, this.nmsThre, 0);
        return new DetectorResult(detections);
    }

    @Override
    public void init() {
        try {
            MappedByteBuffer modelFile = TFUtil.loadModelFile(context.getAssets(), this.modelPath);
            Interpreter.Options options = new Interpreter.Options();
            options.setUseXNNPACK(true);
            options.setNumThreads(3);
            interpreter = new Interpreter(modelFile, options);
            int numBytesPerChannel = 1; //quantized

            imgData = ByteBuffer.allocateDirect(1 * inputX * inputY * 3 * numBytesPerChannel);
            imgData.order(ByteOrder.nativeOrder());
            intValues = new int[inputX * inputY];
            outputLocations = new float[1][NUM_DETECTIONS][4];
            outputClasses = new float[1][NUM_DETECTIONS];
            outputScores = new float[1][NUM_DETECTIONS];
            numDetections = new float[1];

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
    }
}
