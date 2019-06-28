package org.javarosa.benchmarks.utils.builder;

import java.util.List;

public  class QuestionGroup implements IsNodeElement{

    private String name;
    private List<Question> questions;

    QuestionGroup(String name, List<Question> questions) {
        this.name = name;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    List<Question> getQuestions() {
        return questions;
    }

    @Override
    public String getTagName() {
        return "group";
    }
}
