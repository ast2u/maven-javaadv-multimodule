package com.javaadvanced.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KeyValuePair {
    private String key;
    private String value;

    public String getConcatString() {
        return key.toLowerCase() + value.toLowerCase();
    }

    @Override
    public String toString() {
        return String.format("[%s = %s]", key, value);
    }
}
