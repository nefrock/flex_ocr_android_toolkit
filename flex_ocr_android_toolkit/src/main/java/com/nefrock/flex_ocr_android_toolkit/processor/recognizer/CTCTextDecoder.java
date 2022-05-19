package com.nefrock.flex_ocr_android_toolkit.processor.recognizer;

public class CTCTextDecoder {
    private final char[] chars;

    public CTCTextDecoder(String str) {
        this.chars = new char[str.length() + 1];
        this.chars[str.length()] = 'E'; // empty
        for (int i = 0; i < str.length(); ++i) {
            this.chars[i] = str.charAt(i);
        }
    }

    public String decode(float[][] preds) {
        int dim1 = preds.length;
        int dim2 = preds[0].length;
        float[] ps = new float[dim1];
        int[] index = new int[dim1];
        for (int i = 0; i < dim1; ++i) {
            int maxIndex = 0;
            float maxValue = Float.MIN_VALUE;
            for (int j = 0; j < dim2; ++j) {
                float p = preds[i][j];
                if (p > maxValue) {
                    maxIndex = j;
                    maxValue = p;
                }
            }
            ps[i] = maxValue;
            index[i] = maxIndex;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dim1; ++i) {
            if (index[i] == 0) {
                continue;
            }
            if (i == 0) {
                sb.append(chars[index[i]]);
                continue;
            }
            if (index[i - 1] != index[i]) {
                sb.append(chars[index[i]]);
            }
        }
        return sb.toString().replaceAll("E", "");
    }
}
