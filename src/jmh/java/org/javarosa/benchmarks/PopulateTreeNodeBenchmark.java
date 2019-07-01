package org.javarosa.benchmarks;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.getWorkingDir;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.prepareAssets;
import static org.javarosa.core.reference.ReferenceManagerTestUtils.setUpSimpleReferenceManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.javarosa.benchmarks.utils.BenchmarkUtils;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.test.FormParseInit;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.xform.parse.XFormParser;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

public class PopulateTreeNodeBenchmark {
    public static void main(String[] args) {
        dryRun(PopulateTreeNodeBenchmark.class);
    }

    @State(Scope.Thread)
    public static class TreeElementPopulateState {
        private TreeElement dataRootNode;
        private TreeElement savedRoot;
        private FormDef formDef;
        Path xFormFile;
        @Param({"10", "500"})
        public int noOfQuestions = 1;
        @Param({"10", "50"})
        public int noOfInternalSecondaryInstances = 1;
        @Param({"1", "50", "1000"})
        public int noOf2ndryInstanceElements = 1;
        @Param({"1"})
        public int noOfQuestionGroups = 1;
        @Param({"0","50"})
        public int noOfExternalSecondaryInstances = 1;
        @Setup(Level.Trial)
        public void initialize() throws IOException {
            xFormFile = BenchmarkUtils.generateXFormFile(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements).toPath();
            setUpSimpleReferenceManager("file", getWorkingDir());
            Path assetsDir = prepareAssets("populate-nodes-attributes-instance.xml");
            Path submissionFile = assetsDir.resolve("populate-nodes-attributes-instance.xml");
            FormParseInit formParseInit = new FormParseInit(xFormFile);
            FormEntryController formEntryController = formParseInit.getFormEntryController();
            byte[] formInstanceAsBytes = Files.readAllBytes(submissionFile);
            savedRoot = XFormParser.restoreDataModel(formInstanceAsBytes, null).getRoot();
            formDef = formEntryController.getModel().getForm();
            dataRootNode = formDef.getInstance().getRoot().deepCopy(true);
        }
    }

    @Benchmark
    public void runBenchmark(TreeElementPopulateState state) {
        state.dataRootNode.populate(state.savedRoot, state.formDef);
    }

}
