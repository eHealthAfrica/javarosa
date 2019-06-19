package org.javarosa.benchmarks.utils.builder;

import java.util.List;
import java.util.Map;

public  class DummyXForm {

    static String QUESTION_TEMPLATE = "<label>What is the answer to Question %s?</label>";
    static String GROUP_QUESTION_TEMPLATE = "<label>What is the answer to Question %s?</label>";
    static final Map<String, String> DEFAULT_NAMESPACES =
        XFormBuilder.buildMap(
            new String[]{"xmlns", "http://www.w3.org/2002/xforms"},
            new String[]{"xmlns:h", "http://www.w3.org/1999/xhtml"},
            new String[]{"xmlns:ev", "http://www.w3.org/2001/xml-events"},
            new String[]{"xmlns:jr", "http://openrosa.org/javarosa"},
            new String[]{"xmlns:orx", "http://openrosa.org/xforms"},
            new String[]{"xmlns:xsd", "http://www.w3.org/2001/XMLSchema"}
        );

    private String title;
    private String formId;
    private int noOfQuestions;
    private List<QuestionGroup> questionGroups;
    private List<SecondaryInstanceDef> internalSecondaryInstanceDefList;
    private List<SecondaryInstanceDef> externalSecondaryInstanceDefList;

    DummyXForm(String title, int noOfQuestions, List<QuestionGroup> questionGroups,
                      List<SecondaryInstanceDef> internalSecondaryInstanceDefList, List<SecondaryInstanceDef> externalSecondaryInstanceDefList) {
        this.title = title;
        this.noOfQuestions = noOfQuestions;
        this.questionGroups = questionGroups;
        this.internalSecondaryInstanceDefList = internalSecondaryInstanceDefList;
        this.externalSecondaryInstanceDefList = externalSecondaryInstanceDefList;
    }

    private String getFormId() {
        if(formId == null){
            formId = "form_" + System.currentTimeMillis();
        }
        return formId;
    }

    String getTitle() {
        return title;
    }

    int getNoOfQuestions() {
        return noOfQuestions;
    }

    List<QuestionGroup> getQuestionGroups() {
        return questionGroups;
    }

    List<SecondaryInstanceDef> getExternalSecondaryInstanceDefList() {
        return externalSecondaryInstanceDefList;
    }

    Map<String, String> getNamespaces() {
        return DEFAULT_NAMESPACES;
    }

    List<SecondaryInstanceDef> getInternalSecondaryInstanceDefList() {
        return internalSecondaryInstanceDefList;
    }

    //Non POJO methods
    String getMainInstanceTagName(){
        return getFormId().toLowerCase()
            .replace(" ", "_")
            .replace("-", "_");
    }

    String[] getIdAttribute(){
        return new String[]{"id", getFormId()};
    }
}
