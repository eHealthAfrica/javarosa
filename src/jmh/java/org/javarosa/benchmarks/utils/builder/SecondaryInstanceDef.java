package org.javarosa.benchmarks.utils.builder;

public class SecondaryInstanceDef{

    public static String ITEM_TAG = "option";

    private String instanceId;
    private int noOfItems;

    public SecondaryInstanceDef(String instanceId, int noOfItems) {
        this.instanceId = instanceId;
        this.noOfItems = noOfItems;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

}
