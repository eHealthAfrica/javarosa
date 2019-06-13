package org.javarosa.benchmarks.utils;


import java.util.HashMap;
import java.util.Map;

public  class  XFormComplexity{

    private String title;
    private Map<String, String> namespaces;
    private int noOfQuestions;
    private int noOfInternalInstances;
    private int noOfExternalInstances;
    private int noOfItemSets;

    public XFormComplexity(
        String title,
        int noOfQuestions,
        int noOfInternalInstances,
        int noOfExternalInstances,
        Map<String, String> namespaces) {

        this.title = title;
        this.noOfQuestions = noOfQuestions;
        this.noOfInternalInstances = noOfInternalInstances;
        this.noOfExternalInstances = noOfExternalInstances;

        this.namespaces = new HashMap<>();
        for(String  key: namespaces.keySet()){
            if(key.isEmpty()){
                this.namespaces.put("xmlns" + key, namespaces.get(key));
            }else{
                this.namespaces.put("xmlns:"  + key, namespaces.get(key));
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNoOfQuestions() {
        return noOfQuestions;
    }

    public void setNoOfQuestions(int noOfQuestions) {
        this.noOfQuestions = noOfQuestions;
    }

    public int getNoOfInternalInstances() {
        return noOfInternalInstances;
    }

    public void setNoOfInternalInstances(int noOfInternalInstances) {
        this.noOfInternalInstances = noOfInternalInstances;
    }

    public int getNoOfExternalInstances() {
        return noOfExternalInstances;
    }

    public void setNoOfExternalInstances(int noOfExternalInstances) {
        this.noOfExternalInstances = noOfExternalInstances;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public int getNoOfItemSets() {
        return noOfItemSets;
    }

    public void setNoOfItemSets(int noOfItemSets) {
        this.noOfItemSets = noOfItemSets;
    }
}
