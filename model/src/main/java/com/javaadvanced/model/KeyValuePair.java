package com.javaadvanced.model;

public class KeyValuePair {
    private String key;
    private String value;

    // Constructor
    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // Getters
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    // Setters
    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getConcatString() {
        return key.toLowerCase() + value.toLowerCase();
    }

    @Override
    public String toString() {
        return String.format("[%s = %s]", key, value);
    }

    
    
}
