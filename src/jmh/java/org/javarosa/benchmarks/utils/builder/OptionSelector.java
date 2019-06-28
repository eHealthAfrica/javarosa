package org.javarosa.benchmarks.utils.builder;

import java.util.List;

public class OptionSelector {

    public enum Type {
        INTERNAL,
        EXTERNAL
    }

    private String instanceId;
    private List<Option> items;

    public OptionSelector(String instanceId, List<Option>  items) {
        this.instanceId = instanceId;
        this.items = items;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public List<Option> getItems() {
        return items;
    }

}
