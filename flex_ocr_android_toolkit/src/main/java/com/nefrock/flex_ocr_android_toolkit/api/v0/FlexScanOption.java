package com.nefrock.flex_ocr_android_toolkit.api.v0;

import java.util.Set;

public class FlexScanOption {
    private final Set<String> whiteList;

    public FlexScanOption(Set<String> whiteList) {
        this.whiteList = whiteList;
    }

    public FlexScanOption() {
        this.whiteList = null;
    }

    public Set<String> getWhiteList() {
        return whiteList;
    }
}
