package org.javarosa.benchmarks.utils.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class  XFormComplexity{

    public static String QUESTION_TEMPLATE = "<label>What is the answer to Question %s?</label>";
    public static String QUESTION_GROUP_TEMPLATE = "<label>What is the answer to Question %s?</label>";

    private Map<String, String> namespaces;
    private String title;
    private String formId;
    private int noOfQuestions;
    private List<QuestionGroup> questionGroups;
    private List<SecondaryInstanceDef> internalSecondaryInstanceDefList;
    private List<SecondaryInstanceDef> externalSecondaryInstanceDefList;

    public XFormComplexity(

        String title,
        int noOfQuestions,
        List<QuestionGroup> questionGroups,
        List<SecondaryInstanceDef> internalSecondaryInstanceDefList,
        List<SecondaryInstanceDef> externalSecondaryInstanceDefList,
        Map<String, String> namespaces) {

        this.title = title;
        this.noOfQuestions = noOfQuestions;
        this.questionGroups = questionGroups;
        this.internalSecondaryInstanceDefList = internalSecondaryInstanceDefList;
        this.externalSecondaryInstanceDefList = externalSecondaryInstanceDefList;

        this.namespaces = new HashMap<>();
        for(String  key: namespaces.keySet()){
            if(key.isEmpty()){
                this.namespaces.put("xmlns" + key, namespaces.get(key));
            }else{
                this.namespaces.put("xmlns:"  + key, namespaces.get(key));
            }
        }
    }

    public String getFormId() {
        if(formId == null){
            formId = "ref_" + System.currentTimeMillis();
        }
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
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

    public List<QuestionGroup> getQuestionGroups() {
        return questionGroups;
    }

    public void setQuestionGroups(List<QuestionGroup> questionGroups) {
        this.questionGroups = questionGroups;
    }

    public List<SecondaryInstanceDef> getexternalSecondaryInstanceDefList() {
        return externalSecondaryInstanceDefList;
    }

    public void setexternalSecondaryInstanceDefList(List<SecondaryInstanceDef> externalSecondaryInstanceDefList) {
        this.externalSecondaryInstanceDefList = externalSecondaryInstanceDefList;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public List<SecondaryInstanceDef> getInternalSecondaryInstanceDefList() {
        return internalSecondaryInstanceDefList;
    }

    public void setInternalSecondaryInstanceDefList(List<SecondaryInstanceDef> internalSecondaryInstanceDefList) {
        this.internalSecondaryInstanceDefList = internalSecondaryInstanceDefList;
    }

    //Non POJO methods
    public String getMainInstanceTagName(){
        String tagName = getFormId().toLowerCase()
            .replace(" ", "_")
            .replace("-", "_");
        return tagName;
    }

    public String[] getIdAttribute(){
        final String[] id = {"id", getFormId()};
        return  id;
    }
}
