package org.javarosa.benchmarks.utils.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XFormFileGenerator {

    public File generate(int multiplier, int noOfQuestions,int noOfQuestionGroups, int noOfInternalSecondaryInstances,  int noOfExternalSecondaryInstances, Path workingDirectory) throws IOException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DummyXForm dummyXForm =
            new DummyXForm(
                "Dynamic form generated at " + simpleDateFormat.format(new Date()),
                noOfQuestions,
                generateQuestionGroups(multiplier, noOfQuestionGroups),
                generateSecondaryInstances(multiplier, noOfInternalSecondaryInstances, true),
                generateSecondaryInstances(multiplier, noOfExternalSecondaryInstances, false));

        XFormBuilder xFormFileBuilder = new XFormBuilder(dummyXForm, workingDirectory);

        File file = File.createTempFile("x_form_" + System.currentTimeMillis(), ".xml", null);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(xFormFileBuilder.build());
        fileWriter.close();
        return moveToDirectory(file, workingDirectory);
    }

    private List<QuestionGroup> generateQuestionGroups(int multiplier, int count){
        List<QuestionGroup> questionGroups = new ArrayList<>(count);
        while(count > 0){
            questionGroups.add(0, new QuestionGroup("group"+count,count * multiplier));
            count--;
        }
        return questionGroups;
    }

    private List<SecondaryInstanceDef> generateSecondaryInstances(int multiplier, int count, boolean internal){
        List<SecondaryInstanceDef> instances = new ArrayList<>(count);
        String secondaryInstanceType = internal ? "internal" : "external";
        String instanceNameTemplate = secondaryInstanceType +"_secondary_instance_%0" + (count + "").length() +"d";
        while(count > 0){
            instances.add(0, new SecondaryInstanceDef(String.format(instanceNameTemplate, count) , count * multiplier));
            count--;
        }
        return instances;
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
