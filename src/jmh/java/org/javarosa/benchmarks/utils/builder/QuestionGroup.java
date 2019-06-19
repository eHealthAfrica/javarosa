package org.javarosa.benchmarks.utils.builder;

public  class QuestionGroup{

    private String name;
    private int noOfQuestions;

    public QuestionGroup(String name, int noOfQuestions) {
        this.name = name;
        this.noOfQuestions = noOfQuestions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNoOfQuestions() {
        return noOfQuestions;
    }

    public void setNoOfQuestions(int noOfQuestions) {
        this.noOfQuestions = noOfQuestions;
    }

}
