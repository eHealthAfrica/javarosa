package org.javarosa.benchmarks.utils.builder;

import java.util.HashMap;
import java.util.Map;

public class Option implements IsNodeElement{
    String label;
    String value;
    Map<String, String> attributes;

    public Option(String label, String value) {
        this.label = label;
        this.value = value;
        attributes = new HashMap<>();
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTagName() {
        return "item";
    }
}
