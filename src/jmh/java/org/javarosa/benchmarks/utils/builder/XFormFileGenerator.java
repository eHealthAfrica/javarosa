package org.javarosa.benchmarks.utils.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class XFormFileGenerator {

    public File generate(int multiplier, int noOfQuestions,int noOfQuestionGroups, int noOfSecondaryInstances, File directory) throws IOException {

        Map<String, String> namespaces =
            XFormBuilder.buildMap(
                new String[]{"", "http://www.w3.org/2002/xforms"},
                new String[]{"h", "http://www.w3.org/1999/xhtml"},
                new String[]{"ev", "http://www.w3.org/2001/xml-events"},
                new String[]{"jr", "http://openrosa.org/javarosa"},
                new String[]{"orx", "http://openrosa.org/xforms"},
                new String[]{"xsd", "http://www.w3.org/2001/XMLSchema"}
            );

        XFormComplexity xFormComplexity =
            new XFormComplexity(
                "Dynamic form generated at " + new Date().toString(),
                noOfQuestions,
                generateQuestionGroups(multiplier, noOfQuestionGroups),
                generateSecondaryInstances(multiplier, noOfSecondaryInstances),
                null,
                namespaces);

        XFormBuilder xFormFileBuilder = new XFormBuilder(xFormComplexity);

        File file = File.createTempFile("x_form_" + System.currentTimeMillis(), ".xml", directory);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(xFormFileBuilder.build());
        fileWriter.close();
        return moveToDirectory(file, directory);
    }

    private List<QuestionGroup> generateQuestionGroups(int multiplier, int count){
        List<QuestionGroup> questionGroups = new ArrayList<>(count);
        while(count > 0){
            questionGroups.add(0, new QuestionGroup("group"+count,count * multiplier));
            count--;
        }
        return questionGroups;
    }

    private List<SecondaryInstanceDef> generateSecondaryInstances(int multiplier, int count){
        List<SecondaryInstanceDef> instances = new ArrayList<>(count);
        while(count > 0){
            instances.add(0, new SecondaryInstanceDef("first"+ count , count * multiplier));
            count--;
        }
        return instances;
    }

    private File moveToDirectory(File sourceFile, File destinationFolder) throws IOException {
        if (!destinationFolder.exists() || destinationFolder.mkdirs()){
            if (!sourceFile.exists() || !sourceFile.isDirectory())
                throw  new IOException("File " + sourceFile.getPath() + "does  not exist");
            File destinationFile = new File(destinationFolder + "\\" + sourceFile.getName());
            if(sourceFile.renameTo(destinationFile)){
                sourceFile.delete();
                return destinationFile;
            }
        }
        throw new IOException(String.format("Unable to create XForm(%s) in destination(%s)", sourceFile, destinationFolder));
    }

}
