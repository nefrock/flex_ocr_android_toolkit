//package com.nefrock.flex_ocr_android_toolkit.processor.detector;
//
//import static java.lang.Math.min;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Rect;
//import android.os.SystemClock;
//import android.util.Log;
//
//import com.nefrock.flex_ocr_android_toolkit.util.TFUtil;
//
//import org.opencv.android.Utils;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfFloat;
//import org.opencv.core.MatOfInt;
//import org.opencv.core.MatOfRect2d;
//import org.opencv.core.Rect2d;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.tensorflow.lite.Interpreter;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.MappedByteBuffer;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TFLiteNumberPlateDetector implements Detector {
//
//    private Interpreter interpreter;
//    private final Context context;
//    private final String modelPath;
//    private final int inputX;
//    private final int inputY;
//    private ByteBuffer imgData;
//
//    private static final int NUM_DETECTIONS = 10;
//    //    private static final int NUM_DETECTIONS = 1001;
//    private int[] intValues;
//    // outputLocations: array of shape [Batchsize, NUM_DETECTIONS,4]
//    // contains the location of detected boxes
//    private float[][][] outputLocations;
//    // outputClasses: array of shape [Batchsize, NUM_DETECTIONS]
//    // contains the classes of detected boxes
//    private float[][] outputClasses;
//    // outputScores: array of shape [Batchsize, NUM_DETECTIONS]
//    // contains the scores of detected boxes
//    private float[][] outputScores;
//    // numDetections: array of shape [Batchsize]
//    // contains the number of detected boxes
//    private float[] numDetections;
//
//    private final float classPredThre;
//    private final float nmsThre;
//    private final double paddingW;
//    private final double paddingH;
//
//    public TFLiteNumberPlateDetector(Context context, String modelPath, double paddingH, double paddingW, int height, int width, float classPredThre, float nmsThre) {
//        this.context = context;
//        this.modelPath = modelPath;
//        this.paddingH = paddingH;
//        this.paddingW = paddingW;
//        this.inputX = width;
//        this.inputY = height;
//        this.classPredThre = classPredThre;
//        this.nmsThre = nmsThre;
//    }
//
//    @Override
//    public DetectorResult process(Mat image, int x0, int y0) {
//        Mat imgResized = new Mat();
//        Imgproc.resize(image, imgResized, new Size(inputX, inputY));
//        Bitmap bitmap = Bitmap.createBitmap((int) inputX, (int) inputY, Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(imgResized, bitmap);
//
//        // Preprocess the image data from 0-255 int to normalized float based
//        // on the provided parameters.
//        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        imgData.rewind();
//        for (int i = 0; i < inputY; ++i) {
//            for (int j = 0; j < inputX; ++j) {
//                int pixelValue = intValues[i * inputY + j];
////                    // Quantized model
//                imgData.put((byte) ((pixelValue >> 16) & 0xFF));
//                imgData.put((byte) ((pixelValue >> 8) & 0xFF));
//                imgData.put((byte) (pixelValue & 0xFF));
////                imgData.putFloat((float) (2 * ((pixelValue >> 16) & 0xFF) / 255.0) - 1.0f);
////                imgData.putFloat((float) (2 * ((pixelValue >> 8) & 0xFF) / 255.0) - 1.0f);
////                imgData.putFloat((float) (2 * (pixelValue & 0xFF) / 255.0) - 1.0f);
//
//            }
//        }
//
//
//        // Copy the input data into TensorFlow.
//        outputLocations = new float[1][NUM_DETECTIONS][4];
//        outputClasses = new float[1][NUM_DETECTIONS];
//        outputScores = new float[1][NUM_DETECTIONS];
//        numDetections = new float[1];
//        Map<Integer, Object> outputMap = new HashMap<>();
//        outputMap.put(0, outputLocations);
//        outputMap.put(1, outputClasses);
//        outputMap.put(2, outputScores);
//        outputMap.put(3, numDetections);
//
////        float[][] o1 = new float[1][1001];
////        Map<Integer, Object> outputMap = new HashMap<>();
////        outputMap.put(0, o1);
//
//        Object[] inputArray = {imgData};
//
//
//        // Run the inference call.
//        long t1 = SystemClock.uptimeMillis();
//        interpreter.runForMultipleInputsOutputs(inputArray, outputMap);
//        long t2 = SystemClock.uptimeMillis();
//        Log.d("TFLiteLabelTelDetector", "process=" + (t2 - t1));
//
//
//        // Show the best detections.
//        // after scaling them back to the input size.
//        // You need to use the number of detections from the output and not the NUM_DETECTONS variable
//        // declared on top
//        // because on some models, they don't always output the same total number of detections
//        // For example, your model's NUM_DETECTIONS = 20, but sometimes it only outputs 16 predictions
//        // If you don't use the output's numDetections, you'll get nonsensical data
//
//        // cast from float to integer, use min for safety
//        int numDetectionsOutput = min(NUM_DETECTIONS, (int) numDetections[0]);
//
//        //NMS
//        List<Rect2d> labelBboxes = new ArrayList<>();
//        List<Float> labelScores = new ArrayList<>();
//        List<Rect2d> telBBoxes = new ArrayList<>();
//        List<Float> telScores = new ArrayList<>();
//
//        int origWidth = image.width();
//        int origHeight = image.height();
//
//        for (int i = 0; i < numDetectionsOutput; ++i) {
//            int classId = (int) (outputClasses[0][i] + 0.1);
//            float score = outputScores[0][i];
//
//            int x = (int) (outputLocations[0][i][1] * origWidth);
//            int y = (int) (outputLocations[0][i][0] * origHeight);
//            int x1 = (int) (outputLocations[0][i][3] * origWidth);
//            int y1 = (int) (outputLocations[0][i][2] * origHeight);
//            x = Math.max(x, 0);
//            x1 = Math.min(x1, origWidth);
//            y = Math.max(y, 0);
//            y1 = Math.min(y1, origHeight);
//
//            final Rect2d bbox = new Rect2d(x, y, x1 - x, y1 - y);
//            telBBoxes.add(bbox);
//            telScores.add(score);
//        }
////        List<Detection> labels = this.nms(image, image, labelBboxes, labelScores, 0, x0, y0);
////        List<Detection> tels = this.nms(image, image, telBBoxes, telScores, 1, x0, y0);
////        return new DetectorResult(labels, tels);
//        return null;
//    }
//
//    private List<Detection> nms(Mat image, Mat orig, List<Rect2d> bboxes, List<Float> scores, int classId, int x0, int y0) {
//        MatOfRect2d matOfBboxes = new MatOfRect2d();
//        matOfBboxes.fromList(bboxes);
//
//        MatOfFloat matOfScores = new MatOfFloat();
//        matOfScores.fromList(scores);
//
//        MatOfInt matOfResultIdx = new MatOfInt();
//        org.opencv.dnn.Dnn.NMSBoxes(matOfBboxes, matOfScores, this.classPredThre, this.nmsThre, matOfResultIdx);
//
//        List<Detection> res = new ArrayList<>();
//        if (matOfResultIdx.empty()) {
//            return res;
//        }
//
//        int[] resultIndex = matOfResultIdx.toArray();
//        for (int index : resultIndex) {
//            Rect2d bbox = bboxes.get(index);
//            float score = scores.get(index);
//
//            int x = (int) bbox.x;
//            int y = (int) bbox.y;
//            int x1 = (int) (bbox.x + bbox.width);
//            int y1 = (int) (bbox.y + bbox.height);
//
//            x = x + x0;
//            y = y + y0;
//            x1 = x1 + x0;
//            y1 = y1 + y0;
//
//            int padX = (int) ((x1 - x) * paddingW);
//            int padY = (int) ((y1 - y) * paddingH);
//
//            x = Math.max(x - padX, 0);
//            x1 = Math.min(x1 + padX, orig.width());
//            y = Math.max(y - padY, 0);
//            y1 = Math.min(y1 + padY, orig.height());
//
//            Rect rect = new Rect(x, y, x1, y1);
//            res.add(new Detection(rect, score, classId));
//        }
//        return res;
//    }
//
//    @Override
//    public void init() {
//        try {
//            MappedByteBuffer modelFile = TFUtil.loadModelFile(context.getAssets(), this.modelPath);
//            Interpreter.Options options = new Interpreter.Options();
//            options.setUseXNNPACK(true);
//            options.setNumThreads(3);
//            interpreter = new Interpreter(modelFile, options);
////            int numBytesPerChannel = 4; //floating point
//            int numBytesPerChannel = 1; //quantized
//
//            imgData = ByteBuffer.allocateDirect(1 * inputX * inputY * 3 * numBytesPerChannel);
//            imgData.order(ByteOrder.nativeOrder());
//            intValues = new int[inputX * inputY];
//            outputLocations = new float[1][NUM_DETECTIONS][4];
//            outputClasses = new float[1][NUM_DETECTIONS];
//            outputScores = new float[1][NUM_DETECTIONS];
//            numDetections = new float[1];
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void close() {
//    }
//}
