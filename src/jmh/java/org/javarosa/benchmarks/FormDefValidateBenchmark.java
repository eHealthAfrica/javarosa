package org.javarosa.benchmarks;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;

import java.io.File;
import java.io.IOException;

import org.javarosa.benchmarks.utils.BenchmarkUtils;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.ItemsetBinding;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.ValidateOutcome;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.xform.parse.FormParserHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

public class
FormDefValidateBenchmark {
    public static void main(String[] args) {
        dryRun(FormDefValidateBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormDefValidateState {
        FormDef formDef ;
        @Param({"10", "500", "1000", "50000"})
        public int noOfQuestions;
        @Param({"10", "50"})
        public int noOfInternalSecondaryInstances;
        @Param({"10", "500", "1000", "50000"})
        public int noOf2ndryInstanceElements;
        @Param({"0"})
        public int noOfQuestionGroups;
        @Param({"0"})
        public int noOfExternalSecondaryInstances;
        @Setup(Level.Trial)
        public void initialize() throws IOException {
            File xFormXmlFile = BenchmarkUtils.generateXFormFile(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements);
            formDef =  FormParserHelper.parse(xFormXmlFile.toPath());
            FormEntryModel formEntryModel = new FormEntryModel(formDef);
            FormEntryController formEntryController = new FormEntryController(formEntryModel);
            formEntryController.stepToNextEvent();
            while (formEntryModel.getFormIndex().isInForm()) {
                FormIndex questionIndex = formEntryController.getModel().getFormIndex();
                QuestionDef question = formEntryModel.getQuestionPrompt(questionIndex).getQuestion();
                FormEntryPrompt formEntryPrompt = formEntryModel.getQuestionPrompt(questionIndex);
                //Resolve Dynamic Choices
                ItemsetBinding itemsetBinding = question.getDynamicChoices();
                if (itemsetBinding != null) {
                    formDef.populateDynamicChoices(itemsetBinding, (TreeReference) question.getBind().getReference());
                }
                IAnswerData answer = BenchmarkUtils.getStubAnswer(formEntryPrompt.getQuestion());
                formEntryController.answerQuestion(questionIndex, answer, true);
                formEntryController.stepToNextEvent();
            }
            formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
        }
    }

    @Benchmark
    public void runBenchmark(FormDefValidateState state, Blackhole bh) {
        ValidateOutcome validateOutcome = state.formDef.validate(true);
        bh.consume(validateOutcome);
    }
}
