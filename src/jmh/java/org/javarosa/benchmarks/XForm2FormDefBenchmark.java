package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.BenchmarkUtils;
import org.javarosa.core.model.FormDef;
import org.javarosa.xform.parse.FormParserHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.getWorkingDir;
import static org.javarosa.core.reference.ReferenceManagerTestUtils.setUpSimpleReferenceManager;

public class XForm2FormDefBenchmark {

    public static void main(String[] args) {
        dryRun(XForm2FormDefBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        Path xFormXmlPath ;
        @Param({"10", "500"})
        public int noOfQuestions = 500;
        @Param({"1", "10"})
        public int noOfInternalSecondaryInstances = 50;
        @Param({"50", "500", "5000"})
        public int noOf2ndryInstanceElements = 100;
        @Param({"0"})
        public int noOfQuestionGroups = 1;
        @Param({"0"})
        public int noOfExternalSecondaryInstances = 50;
        @Setup(Level.Trial)
        public void initialize() throws IOException {
            File xFormXmlFile = BenchmarkUtils.generateXFormFile(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements);
            setUpSimpleReferenceManager("file", getWorkingDir());
            xFormXmlPath = xFormXmlFile.toPath();
        }
    }

    @Benchmark
    public void runBenchmark(FormTypesState state, Blackhole bh) throws IOException {
        FormDef formDef =  FormParserHelper.parse(state.xFormXmlPath);
        bh.consume(formDef);
    }

}