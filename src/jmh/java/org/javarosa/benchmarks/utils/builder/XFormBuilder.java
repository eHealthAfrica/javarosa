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

public class XFormBuilder {

    private StringBuilder stringBuilder;
    private DummyXForm dummyXForm;
    private Path workingDirectory;
    Map<String, Path> externalSecondaryInstances;
    private boolean minify;

    XFormBuilder(DummyXForm dummyXForm, Path workingDirectory) {
        stringBuilder = new StringBuilder("<?xml version=\"1.0\"?>\n");
        this.dummyXForm = dummyXForm;
        this.workingDirectory = workingDirectory;
        this.minify = false;
    }

    public String build() {
        return
            // formatXML(
            buildHtml()
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
        //);
    }

    public Map<String, Path> buildExternalInstances() {
        return
            buildHtml()
                .buildHead()
                .buildBody()
                .buildTitle()
                .buildModel()
                .buildPrimaryInstance()
                .buildInternalSecondaryInstances()
                .buildExternalSecondaryInstances()
                .buildBind()
                .buildControls()
                .getExternalSecondaryInstances();
    }

    private XFormBuilder buildHtml() {
        if (!hasHtml()) {
            String htmlElementString = openAndClose(HTML, dummyXForm.getNamespaces());
            stringBuilder.append(htmlElementString);
        }
        return this;
    }

    private XFormBuilder buildHead() {
        addChild(HTML, openAndClose(HEAD));
        return this;
    }

    private XFormBuilder buildBody() {
        addChild(HTML, openAndClose(BODY));
        return this;
    }

    private XFormBuilder buildTitle() {
        addChild(HEAD, openAndClose(TITLE, null, dummyXForm.getTitle()));
        return this;
    }

    private XFormBuilder buildModel() {
        addChild(HEAD, openAndClose(MODEL));
        return this;
    }

    static Map<String, String> buildMap(String[]... args) {
        Map<String, String> map = new HashMap<>();
        for (String[] pair : args) map.put(pair[0], pair[1]);
        return map;
    }

    private XFormBuilder buildPrimaryInstance() {
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
                .append(generateQuestions(dummyXForm.getQuestions()))
                .append(closingTag(ROOT))
                .append(closingTag(INSTANCE))
                .toString();
        addChild(MODEL, primaryInstanceString);
        return this;
    }

    private XFormBuilder buildInternalSecondaryInstances() {
        for (OptionSelector optionSelector : dummyXForm.getInternalOptionSelectorList()) {
            StringBuilder sb = new StringBuilder();
            final Map<String, String> idAttr = buildMap(new String[]{"id", optionSelector.getInstanceId()});
            sb.append(openingTag(INSTANCE, idAttr))
                .append(openingTag(optionSelector.getInstanceId()))
                .append(generateSecondaryInstanceOptions(optionSelector))
                .append(closingTag(optionSelector.getInstanceId()))
                .append(closingTag(INSTANCE));

            addChild(MODEL, sb.toString());
        }
        return this;
    }

    private XFormBuilder buildExternalSecondaryInstances() {
        generateExternalInstanceFiles(dummyXForm.getExternalOptionSelectorList());
        for (OptionSelector optionSelector : dummyXForm.getExternalOptionSelectorList()) {
            String instanceId = optionSelector.getInstanceId();
            final Map<String, String> attributesMap = buildMap(
                new String[]{"id", optionSelector.getInstanceId()},
                new String[]{"src", "jr://" + externalSecondaryInstances.get(instanceId)}
            );
            addChild(MODEL, openAndClose(INSTANCE, attributesMap));
        }
        return this;
    }

    private XFormBuilder buildBind() {
        List<QuestionGroup> questionGroups = dummyXForm.getQuestionGroups();
        for (QuestionGroup questionGroup : questionGroups) {
            for (Question question : questionGroup.getQuestions()) {
                String nodeset = generatePath(dummyXForm.getMainInstanceTagName(),
                    questionGroup.getName(),
                    question.getTagName()
                );
                Map<String, String> attrs = buildMap(
                    new String[]{NODE_SET, nodeset},
                    new String[]{"type", "string"}
                );
                addChild(MODEL, shortOpenAndClose(BIND, attrs));
            }
        }

        for (Question question : dummyXForm.getQuestions()) {
            String nodeset = generatePath(dummyXForm.getMainInstanceTagName(), question.getTagName());
            Map<String, String> attrs = buildMap(
                new String[]{NODE_SET, nodeset},
                new String[]{"type", "string"}
            );
            addChild(MODEL, shortOpenAndClose(BIND, attrs));
        }
        return this;
    }

    private XFormBuilder buildControls() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
            buildControl(dummyXForm.getQuestions(),
                dummyXForm.getMainInstanceTagName())
        );
        List<QuestionGroup> questionGroups = dummyXForm.getQuestionGroups();
        for (QuestionGroup questionGroup : questionGroups) {
            stringBuilder.append(openingTag(GROUP, buildMap(new String[]{"appearance", "field-list"})));
            stringBuilder.append(
                buildControl(questionGroup.getQuestions(),
                    generatePath(dummyXForm.getMainInstanceTagName(), questionGroup.getName()))
            );
            stringBuilder.append(closingTag(GROUP));
        }
        addChild(BODY, stringBuilder.toString());
        return this;
    }

    private String buildControl(List<Question> questions, String parentNode) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Question question : questions) {
            String ref = generatePath(parentNode, question.getTagName());
            String controlTag = getWidgetName(question.getQuestionType());
            Map<String, String> attributes = getWidgetAttributes(question.getQuestionType(), new String[]{REF, ref});
            stringBuilder
                .append(openingTag(controlTag, attributes))
                .append(openAndClose(LABEL, null, question.getLabel()))
                .append(openAndClose(HINT, null, question.getHint()));

            if (question.getRenderMode().equals(RenderMode.LIST)) {
                String instanceId = question.getOptionSelector().getInstanceId();
                String instanceSelector = "instance('" + instanceId + "')";
                String nodeset = generatePath(false, instanceSelector, instanceId, ITEM);
                stringBuilder
                    .append(openingTag(ITEM_SET, buildMap(new String[]{NODE_SET, nodeset})))
                    .append(shortOpenAndClose(VALUE, buildMap(new String[]{REF, VALUE})))
                    .append(shortOpenAndClose(LABEL, buildMap(new String[]{REF, LABEL})))
                    .append(closingTag(ITEM_SET));
            }
            stringBuilder
                .append(closingTag(controlTag));

        }
        return stringBuilder.toString();
    }


    public Map<String, Path> getExternalSecondaryInstances() {
        return externalSecondaryInstances;
    }


    private String getWidgetName (QuestionType questionType) {
        switch (questionType) {
                case SELECT_ONE:
                    return SELECT_ONE;
                case SELECT_MULTIPLE:
                    return SELECT_MULTIPLE;
                case RANK:
                    return ODK_RANK;
                case RANGE:
                    return RANGE;
                case UPLOAD_DOCUMENT:
                    return UPLOAD;
                default:
                    return INPUT_TEXT;

            }
    }

    private void generateExternalInstanceFiles(List<OptionSelector> optionSelectorList) {
        final String ROOT = dummyXForm.getMainInstanceTagName();
        externalSecondaryInstances = new HashMap<>();
        try {
            for (OptionSelector optionSelector : optionSelectorList) {
                StringBuilder sb = new StringBuilder();
                String instanceId = optionSelector.getInstanceId();
                String rootElementName = ROOT + "_" + instanceId;
                sb.append(openingTag(rootElementName))
                    .append(generateSecondaryInstanceOptions(optionSelector))
                    .append(closingTag(rootElementName));
                File externalInstanceFile = new File(workingDirectory + File.separator + instanceId + ".xml");
                FileWriter fileWriter = new FileWriter(externalInstanceFile);
                fileWriter.write(sb.toString());
                fileWriter.close();
                externalSecondaryInstances.put(optionSelector.getInstanceId(), externalInstanceFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateQuestionGroup(List<QuestionGroup> questionGroupList) {
        if (questionGroupList != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (QuestionGroup questionGroup : questionGroupList) {
                stringBuilder
                    .append(openingTag(questionGroup.getName()))
                    .append(generateQuestions(questionGroup.getQuestions()))
                    .append(closingTag(questionGroup.getName()));
            }
            return stringBuilder.toString();
        }
        return EMPTY_STRING;
    }

    private String generatePath (String ...parts){
        return generatePath(true, parts);
    }

    private String generatePath ( boolean absolute, String ...parts){
        return ((absolute ? FORWARD_SLASH : EMPTY_STRING) + String.join(FORWARD_SLASH, parts)).replaceAll("//", FORWARD_SLASH);
    }

    private String generateSecondaryInstanceOptions (OptionSelector optionSelector){
        StringBuilder stringBuilder = new StringBuilder();
        for (Option option : optionSelector.getItems()) {
            stringBuilder
                .append(openingTag(ITEM))
                .append(openAndClose(LABEL, null, option.getLabel()))
                .append(openAndClose(VALUE, null, option.getValue()))
                .append(closingTag(ITEM));
        }
        return stringBuilder.toString();
    }

    private String generateQuestions (List < Question > questions) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Question question : questions) {
            String realTagName = question.getTagName();
            stringBuilder.append(shortOpenAndClose(realTagName));
        }
        return stringBuilder.toString();
    }


    private String generateAttributes(Map < String, String > attributes){
        if (attributes != null) {
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pair = iterator.next();
                stringBuilder.append(generateAttribute(pair.getKey(), pair.getValue()));
                stringBuilder.append(iterator.hasNext() ? SPACE : EMPTY_STRING);
            }
            return stringBuilder.toString();
        }
        return EMPTY_STRING;
    }

    private String generateAttribute(String key, String value){
        return key + EQUALS + DOUBLE_QUOTE + value + DOUBLE_QUOTE;
    }

    private Map<String, String> getWidgetAttributes (QuestionType questionType, String[]ref) {
            switch (questionType) {
                case STRING:
                    return buildMap(ref);
                case SELECT_ONE:
                    return buildMap(ref, new String[]{"appearance", "minimal"});
                case SELECT_MULTIPLE:
                    return buildMap(ref);
                case RANK:
                    return buildMap(ref);
                case RANGE:
                    return buildMap(ref);
                case UPLOAD_DOCUMENT:
                    return buildMap(ref, new String[]{"mediatype", "image/*"});
                default:
                    return buildMap(ref);

            }
        }

        private boolean hasHtml () {
            return false;
        }

        private String shortOpenAndClose (String name, Map < String, String > attributes){
            return OPEN_TOKEN +
                name +
                SPACE +
                generateAttributes(attributes) +
                FORWARD_SLASH + CLOSE_TOKEN +
                newLine();
        }

        private String shortOpenAndClose (String name){
            return OPEN_TOKEN + name + FORWARD_SLASH + CLOSE_TOKEN + newLine();
        }

        private String openAndClose (String name){
            return openingTag(name) + closingTag(name);
        }

        private String openAndClose (String name, Map < String, String > attributes){
            return openingTag(name, attributes) + closingTag(name);
        }

        private String openAndClose (String name, Map < String, String > attributes, String xmlText){
            return openingTag(name, attributes) +
                xmlText +
                NEW_LINE +
                closingTag(name);
        }

        private String openingTag (String name){
            return OPEN_TOKEN + name + CLOSE_TOKEN + newLine();
        }

        private String openingTag (String name, Map < String, String > attributes){
            return
                new StringBuilder(OPEN_TOKEN)
                    .append(name)
                    .append(attributes == null ? EMPTY_STRING : SPACE)
                    .append(generateAttributes(attributes))
                    .append(CLOSE_TOKEN)
                    .append(NEW_LINE)
                    .toString();
        }

        private String closingTag (String name){
            return OPEN_TOKEN + FORWARD_SLASH + name + CLOSE_TOKEN + newLine();
        }

        private void addChild (String parentName, String childString){
            String CLOSING_TAG_TOKEN = closingTag(parentName);
            int insertionIndex = stringBuilder.indexOf(CLOSING_TAG_TOKEN);
            stringBuilder.insert(insertionIndex, childString);
        }

        private String newLine () {
            return minify ? EMPTY_STRING : NEW_LINE;
        }

        public String toString () {
            return stringBuilder.toString();
        }

        public String formatXML (){
            String unformattedXML = stringBuilder.toString();
            final int length = unformattedXML.length();
            final int indentSpace = 3;
            final StringBuilder newString = new StringBuilder(length + length / 10);
            final char space = ' ';
            int i = 0;
            int indentCount = 0;
            char currentChar = unformattedXML.charAt(i++);
            char previousChar = currentChar;
            boolean nodeStarted = true;
            newString.append(currentChar);
            for (; i < length - 1; ) {
                currentChar = unformattedXML.charAt(i++);
                if (((int) currentChar < 33) && !nodeStarted) {
                    continue;
                }
                switch (currentChar) {
                    case '<':
                        if ('>' == previousChar && '/' != unformattedXML.charAt(i - 1) && '/' != unformattedXML.charAt(i) && '!' != unformattedXML.charAt(i)) {
                            indentCount++;
                        }
                        newString.append(System.lineSeparator());
                        for (int j = indentCount * indentSpace; j > 0; j--) {
                            newString.append(space);
                        }
                        newString.append(currentChar);
                        nodeStarted = true;
                        break;
                    case '>':
                        newString.append(currentChar);
                        nodeStarted = false;
                        break;
                    case '/':
                        if ('<' == previousChar || '>' == unformattedXML.charAt(i)) {
                            indentCount--;
                        }
                        newString.append(currentChar);
                        break;
                    default:
                        newString.append(currentChar);
                }
                previousChar = currentChar;
            }
            newString.append(unformattedXML.charAt(length - 1));
            return newString.toString();
        }
}