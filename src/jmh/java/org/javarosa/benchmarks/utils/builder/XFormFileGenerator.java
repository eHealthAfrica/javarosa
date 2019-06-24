package org.javarosa.benchmarks.utils.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class XFormFileGenerator {

    public File generate(int noOfQuestions,int noOfQuestionGroups, int noOfInternalSecondaryInstances,  int noOfExternalSecondaryInstances,int noOf2ndryInstanceElements,  Path workingDirectory) throws IOException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OptionSelector> internalSecondaryInstances = generateOptionSelectors(OptionSelector.Type.INTERNAL, noOfInternalSecondaryInstances, noOf2ndryInstanceElements);
        List<OptionSelector> externalSecondaryInstances = generateOptionSelectors( OptionSelector.Type.EXTERNAL, noOfExternalSecondaryInstances, noOf2ndryInstanceElements);
        DummyXForm dummyXForm =
            new DummyXForm(
                "Dynamic form generated at " + simpleDateFormat.format(new Date()),
                generateQuestionGroups(noOfQuestionGroups, internalSecondaryInstances),
                generateQuestions(noOfQuestions, internalSecondaryInstances),
                internalSecondaryInstances,
                externalSecondaryInstances);

        XFormBuilder xFormFileBuilder = new XFormBuilder(dummyXForm, workingDirectory);

        File file = File.createTempFile("x_form_" + System.currentTimeMillis(), ".xml", null);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(xFormFileBuilder.build());
        fileWriter.close();
        return moveToDirectory(file, workingDirectory);
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
        int randomOptionSelector = random.nextInt(optionSelectorList.size());
        OptionSelector optionSelector = optionSelectorList.get(randomOptionSelector);
        if (index % 9 == 0) {
            question = new Question(QuestionType.STRING,"enter_input_" + index,"Enter the answer to the question " + index + "?", "Hint to question " + index, optionSelector, RenderMode.INPUT );
        } else if (index % 8 == 0) {
            question = new Question(QuestionType.SELECT_ONE,"select_option_" + index,"Choose one answer to question " + index + "?", "Hint to question " + index, optionSelector, RenderMode.LIST );
        } else if (index % 7 == 0) {
            question = new Question(QuestionType.SELECT_ONE,"select_option_" + index,"Choose one answer to question " + index + "?", "Hint to question " + index, optionSelector, RenderMode.LIST );
        } else if (index % 6 == 0) {
            question = new Question(QuestionType.DATE,"select_date_" + index,"Select a Date to question " + index + "?", "Hint to question " + index, optionSelector, RenderMode.DATE );
        } else if (index % 5 == 0) {
            question = new Question(QuestionType.INTEGER,"enter_integer_" + index,"Enter Integer to question " + index + "?", "Hint to question " + index, optionSelector, RenderMode.INPUT );
        } else if (index % 3 == 0) {
            question = new Question(QuestionType.DECIMAL,"enter_decimal_" + index,"Enter Decimal value to question " + index + "?", "Hint to question " + index, optionSelector, RenderMode.INPUT );
        } else if (index % 2 == 0) {
            question = new Question(QuestionType.RANGE,"select_range_" + index,"What is answer to question" + index + "?", "Hint to question " + index, optionSelector, RenderMode.LIST );
        } else if (index % 1 == 0) {
            question = new Question(QuestionType.RANK,"select_rank_" + index,"What is answer to question" + index + "?", "Hint to question " + index, optionSelector, RenderMode.LIST );
        } else {
            question = new Question(QuestionType.STRING,"enter_input_" + index,"What is answer to question" + index + "?", "Hint to question " + index, optionSelector, RenderMode.INPUT );
        }
        return question;
    }

    private List<QuestionGroup> generateQuestionGroups(int count, List<OptionSelector> optionSelectorList){
        List<QuestionGroup> questionGroups = new ArrayList<>(count);
        while(count > 0){
            questionGroups.add(0, new QuestionGroup("group"+ count, generateQuestions(count, optionSelectorList)));
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
            options.add(new Option("item " + (i + 1), "option " + (i + 1)));
        }
        return options;
    }

    private File moveToDirectory(File sourceFile, Path destinationPath) throws IOException {
        File destinationFolder = destinationPath.toFile();
        if (destinationFolder.exists() || destinationFolder.mkdirs()){
            if (!sourceFile.exists())
                throw  new IOException("File " + sourceFile.getPath() + "does  not exist");
            File destinationFile = new File(destinationFolder + File.separator + sourceFile.getName());
            if(sourceFile.renameTo(destinationFile)){
                sourceFile.delete();
                return destinationFile;
            }
        }
        throw new IOException(String.format("Unable to create XForm(%s) in destination(%s)", sourceFile, destinationFolder));
    }



}
