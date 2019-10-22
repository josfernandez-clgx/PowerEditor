package com.mindbox.pe.common;

public class PE_InputContextWhiteListSet {
    private String whiteListKey;
    private String whiteListValue;

    public PE_InputContextWhiteListSet(String whiteListKey, String whiteListValue) {
        this.whiteListKey = whiteListKey;
        this.whiteListValue = whiteListValue;
    }

    public String getWhiteListKey() {
        return whiteListKey;
    }

    public String getWhiteListValue() {
        return whiteListValue;
    }
}
