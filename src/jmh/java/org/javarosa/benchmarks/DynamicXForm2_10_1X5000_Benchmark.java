package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.builder.XFormFileGenerator;
import org.javarosa.core.model.FormDef;
import org.javarosa.xform.parse.FormParserHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.getWorkingDir;

public class DynamicXForm2_10_1X5000_Benchmark {

    public static void main(String[] args) {
        dryRun(DynamicXForm2_10_1X5000_Benchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        Path formPath ;

        @Setup(Level.Trial)
        public void initialize() throws IOException {
            XFormFileGenerator xFormFileGenerator = new XFormFileGenerator();
            final int noOf2ndryInstanceElements = 5000;
            final int noOfQuestions = 10;
            final int noOfQuestionGroups = 1;
            final int noOfInternalSecondaryInstances = 1;
            final int noOfExternalSecondaryInstances = 0;
            final Path WORKING_DIR = getWorkingDir();
            File xFormXmlFile = xFormFileGenerator.generate(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements, WORKING_DIR);
            formPath = xFormXmlFile.toPath();
        }
    }

    @Benchmark
    public void benchmarkDynamicXForm(FormTypesState state, Blackhole bh) throws IOException {
        FormDef formDef =  FormParserHelper.parse(state.formPath);
        bh.consume(formDef);
    }

}