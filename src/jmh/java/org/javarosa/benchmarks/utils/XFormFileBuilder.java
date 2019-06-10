package org.javarosa.benchmarks.utils;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XFormFileBuilder{

    String DOUBLE_QUOTE = "\"";
    String SPACE = " ";
    String OPEN_TOKEN = "<";
    String CLOSE_TOKEN = ">";
    String CLOSE_BRACE = "/";
    String EQUALS = "=";
    String HTML = "html";
    String HEAD = "head";
    String BODY = "body";
    String TITLE = "title";
    String MODEL = "model";
    String INSTANCE = "instance";
    String BIND = "bind";
    String INPUT = "input";
    String NEW_LINE = System.getProperty("line.separator");

    Map bindAttributeTemplate;


    StringBuilder stringBuilder;
    XFormComplexity xFormComplexity;

    public XFormFileBuilder(){
       stringBuilder = new StringBuilder();
        bindAttributeTemplate = new HashMap()
        {{
            put("nodeset", "/data/meta/instanceID");
            put("type", "string");
            put("readonly", "true()");
            put("calculate", "concat('uuid:',uuid())");
        }};
        this.xFormComplexity = new XFormComplexity(5,3, 3);
    }

    public XFormFileBuilder(XFormComplexity xFormComplexity){
        stringBuilder = new StringBuilder();
        bindAttributeTemplate = new HashMap()
        {{
            put("nodeset", "/data/meta/instanceID");
            put("type", "string");
            put("readonly", "true()");
            put("calculate", "concat('uuid:',uuid())");
        }};
       this.xFormComplexity = xFormComplexity;
    }

    public XFormFileBuilder buildHtml(){
        if(!hasHtml()){
            String htmlElementString = openAndClose(HTML);
            stringBuilder.append(htmlElementString);
        }
        return this;
    }

    public XFormFileBuilder buildHead(){
        addChild(HTML, openAndClose(HEAD));
        return this;
    }

    public XFormFileBuilder buildBody(){
        addChild(HTML, openAndClose(BODY));
        return this;
    }

    public XFormFileBuilder buildTitle(){
        addChild(HEAD, openAndClose(TITLE));
        return this;
    }

    public XFormFileBuilder buildModel(){
        addChild(HEAD, openAndClose(MODEL));
        return this;
    }

    public XFormFileBuilder buildInstance(){
        StringBuilder sb = new StringBuilder();
        sb.append(openingTag(INSTANCE))
            .append(openingTag("root"))
            .append(generateInstanceItems(10))
            .append(closingTag("root"))
            .append(closingTag(INSTANCE));

        addChild(HEAD, sb.toString());
        return this;
    }

    public XFormFileBuilder buildBind(){
        for(int i = 0; i < xFormComplexity.getNoOfQuestions(); i++){
            addChild(INSTANCE, openAndClose(BIND, bindAttributeTemplate));
        }
        return this;
    }

    public XFormFileBuilder buildInput(){
        addChild(BODY, openAndClose(INPUT));
        return this;
    }

    public boolean hasHtml(){
        return false;
    }

    public String openAndClose(String name){
        return openingTag(name) + closingTag(name);
    }

    public String openAndClose(String name, Map attributes){
        return openingTag(name, attributes) + closingTag(name);
    }

    public String openingTag(String name){
        return OPEN_TOKEN + name + CLOSE_TOKEN + NEW_LINE;
    }

    public String openingTag(String name, Map<String, String> attributes){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(OPEN_TOKEN)
            .append(name)
            .append(SPACE)
            .append(buildAttributes(attributes))
            .append(CLOSE_TOKEN);
        return stringBuilder.toString();
    }

    public String closingTag(String name){
        return OPEN_TOKEN + CLOSE_BRACE  + name + CLOSE_TOKEN  + NEW_LINE;
    }

    public String buildAttributes(Map<String, String> attributes){
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = attributes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry)iterator.next();
            stringBuilder.append(buildAttribute(pair.getKey(), pair.getValue()));
            stringBuilder.append(SPACE);
        }
        return stringBuilder.toString();
    }

    public String buildAttribute(String key, String value){
        return key + EQUALS + DOUBLE_QUOTE + value + DOUBLE_QUOTE;
    }

    public void addChild(String parentName, String childString){
        String CLOSING_TAG_TOKEN = closingTag(parentName);

        int insertionIndex = stringBuilder.indexOf(CLOSING_TAG_TOKEN);
        stringBuilder.insert(insertionIndex, childString);
    }

    public String generateInstanceItems(int noOfItems){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < noOfItems; i++){
            stringBuilder.append(openingTag("item"))
                .append("Count " + i + NEW_LINE)
                .append(closingTag("item"));
        }
        return stringBuilder.toString();
    }

    public String toString(){
        return stringBuilder.toString();
    }


    private  class  XFormComplexity{
        public XFormComplexity(int noOfQuestions, int noOfInternalInstances, int noOfExternalInstances) {
            this.noOfQuestions = noOfQuestions;
            this.noOfInternalInstances = noOfInternalInstances;
            this.noOfExternalInstances = noOfExternalInstances;
        }

        int noOfQuestions;
        int noOfInternalInstances;
        int noOfExternalInstances;

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

    }

    public String createXFormWithComplexity() throws IOException {
        String content = buildHtml()
            .buildHead()
            .buildBody()
            .buildTitle()
            .buildModel()
            .buildInstance()
            .buildBind()
            .buildInput()
            .toString();

        File file = File.createTempFile("x_form_" + System.currentTimeMillis(), null);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.close();

        String xml = new String(Files.readAllBytes(file.toPath()));
        return  xml;
    }



}


