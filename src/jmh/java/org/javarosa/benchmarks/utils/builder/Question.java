package org.javarosa.benchmarks.utils.builder;

public class Question implements IsNodeElement{

    private String tagName;
    private QuestionType questionType;
    private String label;
    private String hint;
    private RenderMode renderMode;
    private OptionSelector options;

    public Question(QuestionType questionType, String label) {
        this(questionType, label, "");
    }

    public Question(QuestionType questionType, String label, String hint) {
        this(questionType, label, hint, RenderMode.INPUT);
    }

    public Question(QuestionType questionType, String label, String hint,  RenderMode renderMode) {
        this(questionType, label,  hint, null, renderMode);
    }

    public Question(QuestionType questionType, String label, String hint, OptionSelector options, RenderMode renderMode) {
        this(questionType, generateTagName(label) , label, hint,  options, renderMode);
    }

    public Question(QuestionType questionType, String tagName, String label, String hint, OptionSelector options, RenderMode renderMode) {
        this.tagName = tagName;
        this.questionType = questionType;
        this.label = label;
        this.hint = hint;
        this.options = options;
        this.renderMode = renderMode;
    }

    public String getTagName() {
        return tagName;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public String getLabel() {
        return label;
    }

    public String getHint() {
        return hint;
    }

    public OptionSelector getOptionSelector() {
        return options;
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    private static String generateTagName(String label){
        String tagName = label
            .toLowerCase()
            .replaceAll("[^_0-9a-zA-Z]+", "_");
        int lastIndex = tagName.length() - 1;
        return tagName.substring(lastIndex).equals("_") ? tagName.substring(0, lastIndex) : tagName;
    }

}
