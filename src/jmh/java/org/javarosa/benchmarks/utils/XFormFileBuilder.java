package org.javarosa.benchmarks.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XFormFileBuilder{

    String DOUBLE_QUOTE = "\"";
    String SPACE = " ";
    String OPEN_TOKEN = "<";
    String CLOSE_TOKEN = ">";
    String FORWARD_SLASH = "/";
    String EQUALS = "=";
    String HTML = "html";
    String HEAD = "head";
    String BODY = "body";
    String TITLE = "title";
    String MODEL = "model";
    String INSTANCE = "instance";
    String BIND = "bind";
    String INPUT = "input";
    String QUESTION = "question";
    String NEW_LINE = System.getProperty("line.separator");

    StringBuilder stringBuilder;
    XFormComplexity xFormComplexity;
    boolean minify;

    public XFormFileBuilder(XFormComplexity xFormComplexity){
        stringBuilder = new StringBuilder("<?xml version=\"1.0\"?>\n");
       this.xFormComplexity = xFormComplexity;
       this.minify = false;
    }

    public String build() {
        return buildHtml()
            .buildHead()
            .buildBody()
            .buildTitle()
            .buildModel()
            .buildPrimaryInstance()
            .buildSecondaryInstances()
            .buildBind()
            .buildControls()
            .toString();
    }

    private XFormFileBuilder buildHtml(){
        if(!hasHtml()){
            String htmlElementString = openAndClose(HTML, xFormComplexity.getNamespaces());
            stringBuilder.append(htmlElementString);
        }
        return this;
    }

    private XFormFileBuilder buildHead(){
        addChild(HTML, openAndClose(HEAD));
        return this;
    }

    private XFormFileBuilder buildBody(){
        addChild(HTML, openAndClose(BODY));
        return this;
    }

    private XFormFileBuilder buildTitle(){
        addChild(HEAD, openAndClose(TITLE, null, "Form Title here"));
        return this;
    }

    private XFormFileBuilder buildModel(){
        addChild(HEAD, openAndClose(MODEL));
        return this;
    }

    public static Map buildMap(String[] ...args){
        Map<String, String> map = new HashMap<>();
        for(String[] pair: args){
            map.put(pair[0], pair[1]);
        }
        return map;
    }


    private XFormFileBuilder buildPrimaryInstance(){
        final String ROOT = xFormComplexity.getMainInstanceTagName();
        final String[] id = {"id", xFormComplexity.getFormId()};
        final StringBuilder sb = new StringBuilder();
            sb.append(openingTag(INSTANCE))
                .append(openingTag(ROOT, buildMap(id)))
                .append(shortOpenAndClose("start"))
                .append(shortOpenAndClose("end"))
                .append(shortOpenAndClose("today"))
                .append(shortOpenAndClose("deviceid"))
                .append(shortOpenAndClose("subscriberid"))
                .append(shortOpenAndClose("simserial"))
                .append(shortOpenAndClose("phonenumber"))
                .append(generateInstanceItems(QUESTION,xFormComplexity.getNoOfQuestions(), true))
                .append(closingTag(ROOT))
                .append(closingTag(INSTANCE));

            addChild(MODEL, sb.toString());
        return this;
    }

    private XFormFileBuilder buildSecondaryInstances(){
        final String ROOT = xFormComplexity.getMainInstanceTagName();
       final Map idAttr = buildMap(new String[]{"id", xFormComplexity.getFormId()});
        for(SecondaryInstanceDef secondaryInstanceDef: xFormComplexity.getInternalSecondaryInstanceDefList()){
            StringBuilder sb = new StringBuilder();
            sb.append(openingTag(INSTANCE, idAttr))
                .append(openingTag(ROOT))
                .append(generateInstanceItems("option",secondaryInstanceDef.getNoOfItems(), false))
                .append(closingTag(ROOT))
                .append(closingTag(INSTANCE));

            addChild(MODEL, sb.toString());
        }
        return this;
    }

    public XFormFileBuilder buildBind(){
        for(int i = 0; i < xFormComplexity.getNoOfQuestions(); i++){
            String nodeset = xFormComplexity.getMainInstanceTagName()+ "/" + QUESTION + (i + 1);
            Map attrs = buildMap(
                new String[]{"nodeset", nodeset},
                new String[]{"type", "string"}
                );
            addChild(MODEL, openAndClose(BIND, attrs));
        }
        return this;
    }

    public XFormFileBuilder buildControls(){
        String question = "What is the answer to Question %s ?";
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < xFormComplexity.getNoOfQuestions(); i++){
            int no = i + 1;
            String ref = generatePath(xFormComplexity.getMainInstanceTagName(), QUESTION+no);
            Map attrs = buildMap(new String[]{"ref", ref});
            String text = String.format(question, no);
            stringBuilder.append(openAndClose(INPUT, attrs, text));
        }
        addChild(BODY, stringBuilder.toString());
        return this;
    }

    public boolean hasHtml(){
        return false;
    }

    public  String shortOpenAndClose(String name){
        return OPEN_TOKEN + name + FORWARD_SLASH + CLOSE_TOKEN + newLine();
    }

    public String openAndClose(String name){
        return openingTag(name) + closingTag(name);
    }

    public String openAndClose(String name, Map attributes){
        return openingTag(name, attributes) + closingTag(name);
    }

    public String openAndClose(String name, Map attributes, String xmlText){
        return openingTag(name, attributes) + xmlText + closingTag(name);
    }

    public String openingTag(String name){
        return OPEN_TOKEN + name + CLOSE_TOKEN + newLine();
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
        return OPEN_TOKEN + FORWARD_SLASH + name + CLOSE_TOKEN  + newLine();
    }

    public String buildAttributes(Map<String, String> attributes){
        if (attributes != null) {
            StringBuilder stringBuilder = new StringBuilder();
            Iterator iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry) iterator.next();
                stringBuilder.append(buildAttribute(pair.getKey(), pair.getValue()));
                stringBuilder.append(SPACE);
            }
            return stringBuilder.toString();
        }
        return "";
    }

    public String buildAttribute(String key, String value){
        return key + EQUALS + DOUBLE_QUOTE + value + DOUBLE_QUOTE;
    }

    public void addChild(String parentName, String childString){
        String CLOSING_TAG_TOKEN = closingTag(parentName);

        int insertionIndex = stringBuilder.indexOf(CLOSING_TAG_TOKEN);
        stringBuilder.insert(insertionIndex, childString);
    }

    public String generatePath(String ...parts){
        return FORWARD_SLASH + String.join(FORWARD_SLASH,parts);
    }

    public String generateInstanceItems(String tagName, int noOfItems,boolean makeTagUnique){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < noOfItems; i++){
            int no =  i + 1;
            String realTagName = makeTagUnique ? tagName + no : tagName;
            stringBuilder.append(openingTag(realTagName))
                .append(tagName + " " + no + newLine())
                .append(closingTag(realTagName));
        }
        return stringBuilder.toString();
    }

    public  String newLine(){
        return minify ? "" : NEW_LINE;
    }

    public String toString(){
        return stringBuilder.toString();
    }

}


