package org.javarosa.benchmarks.utils.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.javarosa.benchmarks.utils.builder.Constants.*;

public class XFormBuilder{

    StringBuilder stringBuilder;
    DummyXForm dummyXForm;
    Path workingDirectory;
    boolean minify;

    public XFormBuilder(DummyXForm dummyXForm, Path workingDirectory){
        stringBuilder = new StringBuilder("<?xml version=\"1.0\"?>\n");
        this.dummyXForm = dummyXForm;
        this.workingDirectory = workingDirectory;
        this.minify = false;
    }

    public String build() {
        return buildHtml()
            .buildHead()
            .buildBody()
            .buildTitle()
            .buildModel()
            .buildPrimaryInstance()
            .buildInternalSecondaryInstances()
            .buildExternalSecondaryInstances()
            .buildBind()
            .buildControls()
            .toString();
    }

    private XFormBuilder buildHtml(){
        if(!hasHtml()){
            String htmlElementString = openAndClose(HTML, dummyXForm.getNamespaces());
            stringBuilder.append(htmlElementString);
        }
        return this;
    }

    private XFormBuilder buildHead(){
        addChild(HTML, openAndClose(HEAD));
        return this;
    }

    private XFormBuilder buildBody(){
        addChild(HTML, openAndClose(BODY));
        return this;
    }

    private XFormBuilder buildTitle(){
        addChild(HEAD, openAndClose(TITLE, null, dummyXForm.getTitle()));
        return this;
    }

    private XFormBuilder buildModel(){
        addChild(HEAD, openAndClose(MODEL));
        return this;
    }

    public static Map<String, String> buildMap(String[]... args){
        Map<String, String> map = new HashMap<>();
        for(String[] pair: args) map.put(pair[0], pair[1]);
        return map;
    }

    private XFormBuilder buildPrimaryInstance(){
        final String ROOT = dummyXForm.getMainInstanceTagName();
        final String primaryInstanceString =
            new StringBuilder(openingTag(INSTANCE))
            .append(openingTag(ROOT, buildMap(dummyXForm.getIdAttribute())))
            .append(shortOpenAndClose("start"))
            .append(shortOpenAndClose("end"))
            .append(shortOpenAndClose("today"))
            .append(shortOpenAndClose("deviceid"))
            .append(shortOpenAndClose("subscriberid"))
            .append(shortOpenAndClose("simserial"))
            .append(shortOpenAndClose("phonenumber"))
            .append(generateQuestionGroup(dummyXForm.getQuestionGroups()))
            .append(generateItemset(QUESTION, dummyXForm.getNoOfQuestions(), true))
            .append(closingTag(ROOT))
            .append(closingTag(INSTANCE))
            .toString();
        addChild(MODEL, primaryInstanceString);
        return this;
    }

    private String generateQuestionGroup(List<QuestionGroup> questionGroupList){
        if(questionGroupList != null){
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0;  i < questionGroupList.size(); i++){
                QuestionGroup questionGroup = questionGroupList.get(i);
                stringBuilder
                    .append(openingTag(questionGroup.getName()))
                    .append(generateItemset(QUESTION,questionGroup.getNoOfQuestions(), true))
                    .append(closingTag(questionGroup.getName()));
            }
            return stringBuilder.toString();
        }
        return EMPTY_STRING;
    }

    private XFormBuilder buildInternalSecondaryInstances(){
        final String ROOT = dummyXForm.getMainInstanceTagName();
        for(SecondaryInstanceDef secondaryInstanceDef: dummyXForm.getInternalSecondaryInstanceDefList()){
            StringBuilder sb = new StringBuilder();
            final Map<String, String> idAttr = buildMap(new String[]{"id", secondaryInstanceDef.getInstanceId()});
            sb.append(openingTag(INSTANCE, idAttr))
                .append(openingTag(ROOT))
                .append(generateItemset(SecondaryInstanceDef.ITEM_TAG,secondaryInstanceDef.getNoOfItems(), false))
                .append(closingTag(ROOT))
                .append(closingTag(INSTANCE));

            addChild(MODEL, sb.toString());
        }
        return this;
    }

    private XFormBuilder buildExternalSecondaryInstances(){
        Map<String, Path> paths = createExternalInstances(dummyXForm.getExternalSecondaryInstanceDefList());
        for(SecondaryInstanceDef secondaryInstanceDef: dummyXForm.getExternalSecondaryInstanceDefList()){
            String instanceId = secondaryInstanceDef.getInstanceId();
            final Map<String, String> attributesMap = buildMap(
                new String[]{"id", secondaryInstanceDef.getInstanceId()},
                new String[]{"src", "jr://" + paths.get(instanceId)}
                );
            addChild(MODEL, openAndClose(INSTANCE, attributesMap));
        }
        return this;
    }

    private  Map<String, Path> createExternalInstances(List<SecondaryInstanceDef> secondaryInstanceDefList){
        final String ROOT = dummyXForm.getMainInstanceTagName();
        Map<String, Path> fileLocationMap = new HashMap<>();
        try {
            for(SecondaryInstanceDef secondaryInstanceDef: secondaryInstanceDefList){
                StringBuilder sb = new StringBuilder();
                String instanceId = secondaryInstanceDef.getInstanceId();
                String rootElementName = ROOT + "_" + instanceId;
                sb.append(openingTag(rootElementName))
                    .append(generateItemset(SecondaryInstanceDef.ITEM_TAG,secondaryInstanceDef.getNoOfItems(), false))
                    .append(closingTag(rootElementName));
                File externalInstanceFile =  new File(workingDirectory + File.separator + instanceId + ".xml");
                FileWriter fileWriter = new FileWriter(externalInstanceFile);
                fileWriter.write(sb.toString());
                fileWriter.close();
                fileLocationMap.put(secondaryInstanceDef.getInstanceId(), externalInstanceFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileLocationMap;
    }

    private XFormBuilder buildBind(){
        List<QuestionGroup> questionGroups = dummyXForm.getQuestionGroups();
        for(int i = 0; i < questionGroups.size(); i++){
            QuestionGroup questionGroup = questionGroups.get(i);
            for(int j = 0; j < questionGroup.getNoOfQuestions(); j++){
                String nodeset = generatePath(dummyXForm.getMainInstanceTagName(),
                    questionGroup.getName(),
                    QUESTION + (j + 1)
                );
                Map<String, String> attrs = buildMap(
                    new String[]{"nodeset", nodeset},
                    new String[]{"type", "string"}
                );
                addChild(MODEL, openAndClose(BIND, attrs));
            }
        }

        for(int i = 0; i < dummyXForm.getNoOfQuestions(); i++){
            String nodeset = dummyXForm.getMainInstanceTagName()+ FORWARD_SLASH + QUESTION + (i + 1);
            Map<String, String> attrs = buildMap(
                new String[]{"nodeset", nodeset},
                new String[]{"type", "string"}
            );
            addChild(MODEL, openAndClose(BIND, attrs));
        }
        return this;
    }

    private XFormBuilder buildControls(){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
            buildControl(null, dummyXForm.getNoOfQuestions(),
                dummyXForm.getMainInstanceTagName())
        );

        List<QuestionGroup> questionGroups = dummyXForm.getQuestionGroups();
        for(int i = 0; i < questionGroups.size(); i++){
            stringBuilder.append(openingTag("group",  buildMap(new String[]{"appearance", "field-list"})));
            QuestionGroup questionGroup = questionGroups.get(i);

            for(int j = 0; j < questionGroup.getNoOfQuestions(); j++){
                stringBuilder.append(
                    buildControl(j, questionGroup.getNoOfQuestions(),
                        generatePath(dummyXForm.getMainInstanceTagName(), questionGroup.getName()))
                );
            }
            stringBuilder.append(closingTag("group"));
        }
        addChild(BODY, stringBuilder.toString());
        return this;
    }

    private String buildControl(Integer groupIndex, int noOfQuestions, String parentNode){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < noOfQuestions; i++){
            int questionIndex = i + 1;
            String ref = generatePath(parentNode, QUESTION + questionIndex);
            Map<String, String> attrsMap = buildMap(new String[]{"ref", ref});
            String text =
                groupIndex == null ?
                    String.format(DummyXForm.QUESTION_TEMPLATE, questionIndex) :
                    String.format(DummyXForm.GROUP_QUESTION_TEMPLATE, groupIndex, questionIndex);
            stringBuilder.append(openAndClose(CONTROL, attrsMap, text));
        }
        return stringBuilder.toString();
    }

    private boolean hasHtml(){
        return false;
    }

    private  String shortOpenAndClose(String name){
        return OPEN_TOKEN + name + FORWARD_SLASH + CLOSE_TOKEN + newLine();
    }

    private String openAndClose(String name){
        return openingTag(name) + closingTag(name);
    }

    private String openAndClose(String name, Map<String, String> attributes){
        return openingTag(name, attributes) + closingTag(name);
    }

    private String openAndClose(String name, Map<String, String> attributes, String xmlText){
        return openingTag(name, attributes) + xmlText + closingTag(name);
    }

    private String openingTag(String name){
        return OPEN_TOKEN + name + CLOSE_TOKEN + newLine();
    }

    private String openingTag(String name, Map<String, String> attributes){
        return
            new StringBuilder(OPEN_TOKEN)
            .append(name)
            .append(SPACE)
            .append(buildAttributes(attributes))
            .append(CLOSE_TOKEN)
            .toString();
    }

    private String closingTag(String name){
        return OPEN_TOKEN + FORWARD_SLASH + name + CLOSE_TOKEN  + newLine();
    }

    private String buildAttributes(Map<String, String> attributes){
        if (attributes != null) {
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pair = iterator.next();
                stringBuilder.append(buildAttribute(pair.getKey(), pair.getValue()));
                stringBuilder.append(SPACE);
            }
            return stringBuilder.toString();
        }
        return EMPTY_STRING;
    }

    private String buildAttribute(String key, String value){
        return key + EQUALS + DOUBLE_QUOTE + value + DOUBLE_QUOTE;
    }

    private void addChild(String parentName, String childString){
        String CLOSING_TAG_TOKEN = closingTag(parentName);
        int insertionIndex = stringBuilder.indexOf(CLOSING_TAG_TOKEN);
        stringBuilder.insert(insertionIndex, childString);
    }

    private String generatePath(String ...parts){
        return (FORWARD_SLASH + String.join(FORWARD_SLASH,parts)).replaceAll("//",FORWARD_SLASH);
    }

    private String generateItemset(String tagName, int noOfItems, boolean makeTagUnique){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < noOfItems; i++){
            int no =  i + 1;
            String realTagName = makeTagUnique ? (tagName + no) : tagName;
            stringBuilder.append(openingTag(realTagName))
                .append(tagName)
                .append(" ")
                .append(no)
                .append(newLine())
                .append(closingTag(realTagName));
        }
        return stringBuilder.toString();
    }

    private  String newLine(){
        return minify ? EMPTY_STRING : NEW_LINE;
    }

    public String toString(){
        return stringBuilder.toString();
    }

}


