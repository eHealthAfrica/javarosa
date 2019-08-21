package org.javarosa.benchmarks;

import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.ItemsetBinding;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.InstanceInitializationFactory;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.xform.parse.FormParserHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.file.Path;

import static org.javarosa.benchmarks.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.BenchmarkUtils.getStubAnswer;

public class OptimizeFormDefQuestions11 {
    public static void main(String[] args) {
        dryRun(OptimizeFormDefQuestions11.class);
    }

    @State(Scope.Thread)
    public static class FormControllerAnswerQuestionState {
        FormEntryController formEntryController;
        FormEntryModel formEntryModel;

        @Setup(Level.Trial)
        public void initialize() throws IOException {
            FormDef.OPTIMZE_DYNAMIC_CHOICES = true;
            Path formFile = BenchmarkUtils.getNigeriaWardsXMLWithExternal2ndryInstance();
            FormDef formDef = FormParserHelper.parse(formFile);
            formDef.initialize(true, new InstanceInitializationFactory());
            formEntryModel = new FormEntryModel(formDef);
            formEntryController = new FormEntryController(formEntryModel);
            formEntryController.stepToNextEvent();
            formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
        }
    }

    @Benchmark
    public void benchmarkAnswerOne(FormControllerAnswerQuestionState state) {
        state.formEntryController.stepToNextEvent();
        AnswerCurrentQuestionAction action = new AnswerCurrentQuestionAction(state).invoke();
        FormIndex questionIndex = action.getQuestionIndex();
        IAnswerData answer = action.getAnswer();
        state.formEntryController.saveAnswer(questionIndex, answer, true);
        state.formEntryController.stepToNextEvent();
        state.formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
    }

    @Benchmark
    public void benchmarkAnswerAll(FormControllerAnswerQuestionState state) {
        state.formEntryController.stepToNextEvent();
        while (state.formEntryModel.getFormIndex().isInForm()) {
            AnswerCurrentQuestionAction action = new AnswerCurrentQuestionAction(state).invoke();
            FormIndex questionIndex = action.getQuestionIndex();
            IAnswerData answer = action.getAnswer();
            state.formEntryController.saveAnswer(questionIndex, answer, true);
            state.formEntryController.stepToNextEvent();
        }
        state.formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
    }

//
//    @Benchmark
//    public void benchmarkAnswerAndSaveAll(FormControllerAnswerQuestionState state) {
//        state.formEntryController.stepToNextEvent();
//        while (state.formEntryModel.getFormIndex().isInForm()) {
//            AnswerCurrentQuestionAction action = new AnswerCurrentQuestionAction(state).invoke();
//            FormIndex questionIndex = action.getQuestionIndex();
//            IAnswerData answer = action.getAnswer();
//            state.formEntryController.saveAnswer(questionIndex, answer, true);
//            state.formEntryController.stepToNextEvent();
//        }
//        state.formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
//    }
//
//    @Benchmark
//    public void benchmarkAnswerAll(FormControllerAnswerQuestionState state) {
//        state.formEntryController.stepToNextEvent();
//        while (state.formEntryModel.getFormIndex().isInForm()) {
//            new AnswerCurrentQuestionAction(state).invoke();
//            state.formEntryController.stepToNextEvent();
//        }
//        state.formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
//    }
//
//    @Benchmark
//    public void benchmarkAnswerAllThenSaveAll(FormControllerAnswerQuestionState state) {
//        HashMap<FormIndex, IAnswerData> answers = new HashMap<>();
//        state.formEntryController.stepToNextEvent();
//        while (state.formEntryModel.getFormIndex().isInForm()) {
//            FormIndex questionIndex = state.formEntryController.getModel().getFormIndex();
//            FormEntryPrompt formEntryPrompt = state.formEntryModel.getQuestionPrompt(questionIndex);
//            QuestionDef question = formEntryPrompt.getQuestion();
//            ItemsetBinding itemsetBinding = question.getDynamicChoices();
//            if (itemsetBinding != null) {
//                state.formEntryController.getModel().getForm()
//                    .populateDynamicChoices(itemsetBinding, (TreeReference) question.getBind().getReference());
//            }
//            IAnswerData answer = getStubAnswer(formEntryPrompt.getQuestion());
//            int saveStatus = state.formEntryController.answerQuestion(questionIndex, answer, true);
//            answers.put(questionIndex, answer);
//            state.formEntryController.stepToNextEvent();
//        }
//        for (FormIndex questionIndex : answers.keySet()) {
//            state.formEntryController.saveAnswer(questionIndex, answers.get(questionIndex), false);
//        }
//        state.formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
//    }
//
//    @Benchmark
//    public void benchmarkAnswerOne(FormControllerAnswerQuestionState state) throws RuntimeException {
//        state.formEntryController.stepToNextEvent();
//        if (state.formEntryModel.getFormIndex().isInForm()) {
//            FormIndex questionIndex = state.formEntryController.getModel().getFormIndex();
//            FormEntryPrompt formEntryPrompt = state.formEntryModel.getQuestionPrompt(questionIndex);
//            IAnswerData answer = getStubAnswer(formEntryPrompt.getQuestion());
//            state.formEntryController.answerQuestion(questionIndex, answer, true);
//            state.formEntryController.stepToNextEvent();
//        } else {
//            throw new RuntimeException("Form controller not in a question index");
//        }
//        state.formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
//    }
//
    private class AnswerCurrentQuestionAction {
        private FormControllerAnswerQuestionState state;
        private FormIndex questionIndex;
        private IAnswerData answer;

        AnswerCurrentQuestionAction(FormControllerAnswerQuestionState state) {
            this.state = state;
        }

        FormIndex getQuestionIndex() {
            return questionIndex;
        }

        IAnswerData getAnswer() {
            return answer;
        }

        AnswerCurrentQuestionAction invoke() {
            questionIndex = state.formEntryController.getModel().getFormIndex();
            FormEntryPrompt formEntryPrompt = state.formEntryModel.getQuestionPrompt(questionIndex);
            QuestionDef question = formEntryPrompt.getQuestion();
            ItemsetBinding itemsetBinding = question.getDynamicChoices();
            if (itemsetBinding != null)
                state.formEntryController.getModel().getForm().populateDynamicChoices(
                    itemsetBinding,
                    (TreeReference) question.getBind().getReference()
                );
            answer = getStubAnswer(formEntryPrompt.getQuestion());
            state.formEntryController.answerQuestion(questionIndex, answer, true);
            return this;
        }
    }
}
