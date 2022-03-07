package com.nefrock.flex_ocr_android_toolkit;

public class FlexAPI {

    private static final FlexAPI singleton = new FlexAPI();

    private FlexAPI() {

    }

    public static FlexAPI shared(){
        return singleton;
    }

    public String hello() {
        return "hello";
    }
}
