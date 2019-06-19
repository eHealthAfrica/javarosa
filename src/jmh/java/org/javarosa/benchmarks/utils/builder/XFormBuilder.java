package org.javarosa.benchmarks.utils.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.javarosa.benchmarks.utils.builder.Constants.*;

public class XFormBuilder{

    StringBuilder stringBuilder;
    XFormComplexity xFormComplexity;
    boolean minify;

    public XFormBuilder(XFormComplexity xFormComplexity){
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

    private XFormBuilder buildHtml(){
        if(!hasHtml()){
            String htmlElementString = openAndClose(HTML, xFormComplexity.getNamespaces());
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
        addChild(HEAD, openAndClose(TITLE, null, xFormComplexity.getTitle()));
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
        final String ROOT = xFormComplexity.getMainInstanceTagName();
        final String primaryInstanceString =
            new StringBuilder(openingTag(INSTANCE))
            .append(openingTag(ROOT, buildMap(xFormComplexity.getIdAttribute())))
            .append(shortOpenAndClose("start"))
            .append(shortOpenAndClose("end"))
            .append(shortOpenAndClose("today"))
            .append(shortOpenAndClose("deviceid"))
            .append(shortOpenAndClose("subscriberid"))
            .append(shortOpenAndClose("simserial"))
            .append(shortOpenAndClose("phonenumber"))
            .append(generateQuestionGroup(xFormComplexity.getQuestionGroups()))
            .append(generateItemset(QUESTION,xFormComplexity.getNoOfQuestions(), true))
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

    private XFormBuilder buildSecondaryInstances(){
        final String ROOT = xFormComplexity.getMainInstanceTagName();
        int counter = 1;
        for(SecondaryInstanceDef secondaryInstanceDef: xFormComplexity.getInternalSecondaryInstanceDefList()){
            StringBuilder sb = new StringBuilder();
            final Map<String, String> idAttr = buildMap(new String[]{"id", xFormComplexity.getFormId() + "_" + counter++});
            sb.append(openingTag(INSTANCE, idAttr))
                .append(openingTag(ROOT))
                .append(generateItemset(SecondaryInstanceDef.ITEM_TAG,secondaryInstanceDef.getNoOfItems(), false))
                .append(closingTag(ROOT))
                .append(closingTag(INSTANCE));

            addChild(MODEL, sb.toString());
        }
        return this;
    }

    private XFormBuilder buildBind(){
        List<QuestionGroup> questionGroups = xFormComplexity.getQuestionGroups();
        for(int i = 0; i < questionGroups.size(); i++){
            QuestionGroup questionGroup = questionGroups.get(i);
            for(int j = 0; j < questionGroup.getNoOfQuestions(); j++){
                String nodeset = generatePath(xFormComplexity.getMainInstanceTagName(),
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

        for(int i = 0; i < xFormComplexity.getNoOfQuestions(); i++){
            String nodeset = xFormComplexity.getMainInstanceTagName()+ FORWARD_SLASH + QUESTION + (i + 1);
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
            buildControl(null, xFormComplexity.getNoOfQuestions(),
                xFormComplexity.getMainInstanceTagName())
        );

        List<QuestionGroup> questionGroups = xFormComplexity.getQuestionGroups();
        for(int i = 0; i < questionGroups.size(); i++){
            stringBuilder.append(openingTag("group",  buildMap(new String[]{"appearance", "field-list"})));
            QuestionGroup questionGroup = questionGroups.get(i);

            for(int j = 0; j < questionGroup.getNoOfQuestions(); j++){
                System.out.println(i+"-"+j);
                stringBuilder.append(
                    buildControl(j, questionGroup.getNoOfQuestions(),
                        generatePath(xFormComplexity.getMainInstanceTagName(), questionGroup.getName()))
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
                    String.format(XFormComplexity.QUESTION_TEMPLATE, questionIndex) :
                    String.format(XFormComplexity.QUESTION_GROUP_TEMPLATE, groupIndex, questionIndex);
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
            .append(OPEN_TOKEN)
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
            Iterator iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry<String, String>) iterator.next();
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


