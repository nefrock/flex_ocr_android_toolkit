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
import org.opencv.core.Rect2d;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TFLiteFastLabelTelDetector implements Detector {

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

    private final float labelClassPredThre = 0.5f;;
    private final float telClassPredThre = 0.05f;
    private final float nmsThre = 0.05f;
    private final double paddingW;
    private final double paddingH;

    public TFLiteFastLabelTelDetector(Context context, String modelPath, Size size) {
        this.context = context;
        this.modelPath = modelPath;
        this.paddingH = 0;
        this.paddingW = 0;
        this.inputX = size.getWidth();
        this.inputY = size.getHeight();
    }

    @Override
    public DetectorResult process(Mat image, FlexScanOption option) {
        Mat imgResized = new Mat();
        Imgproc.resize(image, imgResized, new org.opencv.core.Size(inputX, inputY));
        Bitmap bitmap = Bitmap.createBitmap((int) inputX, (int) inputY, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgResized, bitmap);

        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();
        for (int i = 0; i < inputY; ++i) {
            for (int j = 0; j < inputX; ++j) {
                int idx = i * inputX + j;
                int pixelValue = intValues[idx];
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

        // Show the best detections.
        // after scaling them back to the input size.
        // You need to use the number of detections from the output and not the NUM_DETECTONS variable
        // declared on top
        // because on some models, they don't always output the same total number of detections
        // For example, your model's NUM_DETECTIONS = 20, but sometimes it only outputs 16 predictions
        // If you don't use the output's numDetections, you'll get nonsensical data

        // cast from float to integer, use min for safety
        int numDetectionsOutput = min(NUM_DETECTIONS, (int) numDetections[0]);

        //NMS
        List<Rect2d> labelBboxes = new ArrayList<>();
        List<Float> labelScores = new ArrayList<>();
        List<Rect2d> telBBoxes = new ArrayList<>();
        List<Float> telScores = new ArrayList<>();

        int origWidth = image.width();
        int origHeight = image.height();

        for (int i = 0; i < numDetectionsOutput; ++i) {
            int classId = (int) (outputClasses[0][i] + 0.1);
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

            if (classId == 0) {
                //label
                labelBboxes.add(bbox);
                labelScores.add(score);
            } else {
                //tel
                telBBoxes.add(bbox);
                telScores.add(score);
            }
        }
        List<Detection> labels = NMS.nms(image, labelBboxes, labelScores, this.labelClassPredThre, this.nmsThre, 0);
        List<Detection> tels = NMS.nms(image, telBBoxes, telScores, this.telClassPredThre, this.nmsThre, 1);
        labels.addAll(tels);
        return new DetectorResult(labels);
    }

    @Override
    public void init() throws IOException {
        try {
            MappedByteBuffer modelFile = TFUtil.loadModelFile(context.getAssets(), this.modelPath);
            Interpreter.Options options = new Interpreter.Options();
            options.setUseNNAPI(true);
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
            throw e;
        }
    }

    @Override
    public void close() {
    }
}
