package com.nefrock.flex_ocr_android_toolkit.processor.extractor;

import java.util.Locale;

public class ExtractorUtil {

    public static boolean isTelFormat(String candidate) {
        candidate = candidate.toLowerCase(Locale.ROOT);
        if(candidate.contains("tel")) {
            return true;
        }
        candidate = candidate.replaceAll("O", "0");
        candidate = candidate.replaceAll("[^0-9]", "");
        return candidate.length() >= 10;
    }
}
