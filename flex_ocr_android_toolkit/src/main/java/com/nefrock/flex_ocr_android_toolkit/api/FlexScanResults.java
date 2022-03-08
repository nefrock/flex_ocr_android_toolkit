package com.nefrock.flex_ocr_android_toolkit.api;

import java.util.List;

public class FlexScanResults {
    private final List<FlexScanResult> results;
    private final FlexExitCode code;
    private final long elapsedTime;

    public FlexScanResults(List<FlexScanResult> results, FlexExitCode code, long elapsedTime) {
        this.results = results;
        this.code = code;
        this.elapsedTime = elapsedTime;
    }

    public List<FlexScanResult> getResults() {
        return results;
    }

    public FlexExitCode getExitCode() {
        return code;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}
