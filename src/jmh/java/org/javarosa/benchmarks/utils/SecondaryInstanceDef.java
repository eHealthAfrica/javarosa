package org.javarosa.benchmarks.utils;

public class SecondaryInstanceDef{

    private String instanceId;
    private int noOfItems;

    public SecondaryInstanceDef(String instanceId, int noOfItems) {
        this.instanceId = instanceId;
        this.noOfItems = noOfItems;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }
}
