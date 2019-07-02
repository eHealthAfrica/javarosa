package org.javarosa.benchmarks.utils.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class XFormFileGenerator {

    public File generateXFormFile(String title, int noOfQuestions, int noOfQuestionGroups, int noOfInternalSecondaryInstances, int noOfExternalSecondaryInstances, int noOf2ndryInstanceElements, Path workingDirectory) throws IOException {
        File file = new File(workingDirectory.resolve(title + ".xml").toString());
        FileWriter fileWriter = new FileWriter(file);
        XFormBuilder xFormBuilder = generateXFormBuilder(title, noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements,workingDirectory);
        fileWriter.write(xFormBuilder.build());
        fileWriter.close();
        return file;
    }

    public XFormBuilder generateXFormBuilder(String title, int noOfQuestions, int noOfQuestionGroups, int noOfInternalSecondaryInstances, int noOfExternalSecondaryInstances, int noOf2ndryInstanceElements, Path workingDirectory) throws IOException {
        List<OptionSelector> internalSecondaryInstances = generateOptionSelectors(OptionSelector.Type.INTERNAL, noOfInternalSecondaryInstances, noOf2ndryInstanceElements);
        List<OptionSelector> externalSecondaryInstances = generateOptionSelectors( OptionSelector.Type.EXTERNAL, noOfExternalSecondaryInstances, noOf2ndryInstanceElements);
        List<OptionSelector> secondaryInstances = new ArrayList<>();
        secondaryInstances.addAll(internalSecondaryInstances);
        secondaryInstances.addAll(externalSecondaryInstances);
        DummyXForm dummyXForm =
            new DummyXForm(
                title,
                generateQuestionGroups(noOfQuestionGroups, secondaryInstances),
                generateQuestions(noOfQuestions, secondaryInstances),
                internalSecondaryInstances,
                externalSecondaryInstances);

        XFormBuilder xFormFileBuilder = new XFormBuilder(dummyXForm, workingDirectory);
        return xFormFileBuilder;
    }

    private List<Question> generateQuestions(int noOfQuestions, List<OptionSelector> optionSelectorList){
        List<Question> questions = new ArrayList<>(noOfQuestions);
        while(noOfQuestions > 0){
            questions.add(0, autoGenerate(noOfQuestions, optionSelectorList));
            noOfQuestions--;
        }
        return questions;
    }

    private Question autoGenerate(int index, List<OptionSelector> optionSelectorList) {
        Random random = new Random();
        Question question;
        OptionSelector optionSelector = null;
        if(!optionSelectorList.isEmpty()){
            int randomOptionSelector;
            randomOptionSelector = random.nextInt(optionSelectorList.size());
            optionSelector = optionSelectorList.get(randomOptionSelector);
        }
        if (index % 2 == 0 && optionSelectorList != null && !optionSelectorList.isEmpty()) {
            question = new Question(QuestionType.SELECT_ONE,"select_option_" + index,"Select the answer to the question " + index + "?", "Hint to question " + index, optionSelector, RenderMode.LIST );
        } else {
            question = new Question(QuestionType.STRING,"enter_input_" + index,"What is answer to question" + index + "?", "Hint to question " + index, optionSelector, RenderMode.INPUT );
        }
        return question;
    }

    private List<QuestionGroup> generateQuestionGroups(int count, List<OptionSelector> optionSelectorList){
        List<QuestionGroup> questionGroups = new ArrayList<>(count);
        while(count > 0){
            questionGroups.add(0, new QuestionGroup("group"+ count, generateQuestions(4, optionSelectorList)));
            count--;
        }
        return questionGroups;
    }

    private List<OptionSelector> generateOptionSelectors(OptionSelector.Type type, int noOfOptionSelectors, int noOf2ndryInstanceElements){
        List<OptionSelector> instances = new ArrayList<>(noOfOptionSelectors);
        String secondaryInstanceType = type.toString().toLowerCase();
        String instanceIdTemplate = secondaryInstanceType +"_secondary_instance_%0" + (noOfOptionSelectors + "").length() +"d";
        while(noOfOptionSelectors > 0){
            instances.add(0, generateOptionSelector(String.format(instanceIdTemplate, noOfOptionSelectors), noOf2ndryInstanceElements));
            noOfOptionSelectors--;
        }
        return instances;
    }

    private OptionSelector generateOptionSelector(String instanceId, int noOfOptions){
        return new OptionSelector(instanceId, generateOptions(noOfOptions));
    }

    private List<Option> generateOptions(int noOfOptions){
        List<Option>  options = new ArrayList<>();
        for(int i = 0; i < noOfOptions; i++){
            options.add(new Option("item " + (i + 1), "option" + (i + 1)));
        }
        return options;
    }

}
