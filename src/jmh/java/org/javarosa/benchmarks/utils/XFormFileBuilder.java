package org.javarosa.benchmarks.utils;


import org.javarosa.benchmarks.BenchmarkUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public class XFormFileBuilder{

    String OPEN_TOKEN = "<";
    String CLOSE_TOKEN = ">";
    String CLOSE_BRACE = "/";
    String HTML = "html";
    String HEAD = "head";


    StringBuilder stringBuilder;

    public XFormFileBuilder(){
        stringBuilder = new StringBuilder();
    }

    public XFormFileBuilder buildHtml(){
        if(!hasHtml()){
            String htmlElementString = openingTag(HTML) + closingTag(HTML);
            stringBuilder.append(htmlElementString);
        }
        return this;
    }

    public XFormFileBuilder buildHead(){
        String OPENING_HTML = OPEN_TOKEN + HEAD + CLOSE_TOKEN;
        String headElementString = openingTag(HEAD) + closingTag(HEAD);
        stringBuilder.insert(stringBuilder.indexOf(OPENING_HTML), headElementString);
        return this;
    }

    public boolean hasHtml(){
        return false;
    }

    public String openingTag(String name){
        return OPEN_TOKEN + name + CLOSE_TOKEN;
    }

    public String closingTag(String name){
        return OPEN_TOKEN + CLOSE_BRACE  + name + CLOSE_TOKEN;
    }


    private  class  XFormComplexity{
        int noOfQuestions;
        int noOfInternalInstances;
        int noOfExternalInstances;


    }


    public String createXFormWithComplexity(XFormComplexity xFormComplexity) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(buildHtml());
        stringBuilder.append(buildHead());
//        stringBuilder.append(buildTitle());
//        stringBuilder.append(buildTitle());
//        stringBuilder.append(buildTitle());

        File file = File.createTempFile("x_form_" + System.currentTimeMillis(), null);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();

        String xml = new String(Files.readAllBytes(file.toPath()));
        return  xml;
    }



}


