package com.nefrock.flex_ocr_android_toolkit.api.v1;

import android.content.Context;

import java.util.HashMap;

public class ModelConfig implements FlexModelSpecificConfig{

    private final HashMap<String, Object> hints;

    public ModelConfig() {
        this.hints = new HashMap<>();
    }

    @Override
    public void setHint(String name, Object value) {
        hints.put(name, value);
    }

    @Override
    public Object getHint(String name) {
        return hints.get(name);
    }
}
