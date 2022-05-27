package com.nefrock.flex_ocr_android_toolkit.processor.detector;

import android.graphics.Rect;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Rect2d;

import java.util.ArrayList;
import java.util.List;

public class NMS {

    public static List<Detection> nms(Mat orig, List<Rect2d> bboxes, List<Float> scores, float classPredThre, float nmsThre, int classId) {
        return nms(orig, bboxes, scores, classPredThre, nmsThre, classId, 0, 0);
    }

    public static List<Detection> nms(Mat orig, List<Rect2d> bboxes, List<Float> scores, float classPredThre, float nmsThre, int classId, double paddingH, double paddingW) {
        MatOfRect2d matOfBboxes = new MatOfRect2d();
        matOfBboxes.fromList(bboxes);

        MatOfFloat matOfScores = new MatOfFloat();
        matOfScores.fromList(scores);

        MatOfInt matOfResultIdx = new MatOfInt();
        org.opencv.dnn.Dnn.NMSBoxes(matOfBboxes, matOfScores, classPredThre, nmsThre, matOfResultIdx);

        List<Detection> res = new ArrayList<>();
        if (matOfResultIdx.empty()) {
            return res;
        }

        int[] resultIndex = matOfResultIdx.toArray();
        for (int index : resultIndex) {
            Rect2d bbox = bboxes.get(index);
            float score = scores.get(index);

            int x = (int) bbox.x;
            int y = (int) bbox.y;
            int x1 = (int) (bbox.x + bbox.width);
            int y1 = (int) (bbox.y + bbox.height);

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
}
