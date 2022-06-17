package com.nefrock.flex_ocr_android_toolkit.api;


import java.util.Collections;
import java.util.List;

public class FlexScanResults {
    private final List<FlexScanResult> results;
    private final FlexExitCode code;
    private final long elapsedTime;
    private final Exception exception;

    public FlexScanResults(List<FlexScanResult> results, FlexExitCode code, long elapsedTime, Exception e) {
        this.results = results;
        this.code = code;
        this.elapsedTime = elapsedTime;
        this.exception = e;
    }

    public FlexScanResults(List<FlexScanResult> results, FlexExitCode code, long elapsedTime) {
        this.results = results;
        this.code = code;
        this.elapsedTime = elapsedTime;
        this.exception = null;
    }

    public FlexScanResults(List<FlexScanResult> results,  long elapsedTime) {
        this.results = results;
        this.code = FlexExitCode.DONE;
        this.elapsedTime = elapsedTime;
        this.exception = null;
    }

    public FlexScanResults(FlexExitCode code, Exception e) {
        this.results = Collections.emptyList();
        this.code = code;
        this.elapsedTime = 0;
        this.exception = e;
    }

    public boolean hasResult(FlexScanResultType typ) {
        for (FlexScanResult result: results) {
            if(result.getType() == typ) {
                return true;
            }
        }
        return false;
    }

    public List<FlexScanResult> getResults() {
        return results;
    }

    public FlexExitCode getExitCode() {
        return code;
    }

    public Exception getException() { return this.exception; };

    public long getElapsedTime() {
        return elapsedTime;
    }
}
