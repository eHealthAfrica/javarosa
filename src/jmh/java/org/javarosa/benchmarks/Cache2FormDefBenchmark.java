package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.BenchmarkUtils;
import org.javarosa.benchmarks.utils.FormDefCache;
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

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.getCachePath;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.getWorkingDir;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.registerCacheProtoTypes;
import static org.javarosa.core.reference.ReferenceManagerTestUtils.setUpSimpleReferenceManager;

public class Cache2FormDefBenchmark {

    public static void main(String[] args) {
        dryRun(Cache2FormDefBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        File xFormXmlFile ;
        FormDef formDef ;
        String CACHE_PATH;
        @Param({"10", "500"})
        public int noOfQuestions = 1;
        @Param({"10", "50"})
        public int noOfInternalSecondaryInstances = 1;
        @Param({"50", "1000"})
        public int noOf2ndryInstanceElements = 1;
        @Param({"1"})
        public int noOfQuestionGroups = 1;
        @Param({"0","50"})
        public int noOfExternalSecondaryInstances = 1;
        @Setup(Level.Trial)
        public void initialize() throws IOException {
            CACHE_PATH = getCachePath().toString();
            xFormXmlFile = BenchmarkUtils.generateXFormFile(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements);
            setUpSimpleReferenceManager("file", getWorkingDir());
            String formPath = xFormXmlFile.getPath();
            formDef =  FormParserHelper.parse(xFormXmlFile.toPath());
            registerCacheProtoTypes();
            FormDefCache.writeCache(formDef, formPath, CACHE_PATH);
        }
    }

    @Benchmark
    public void runBenchmark(FormTypesState state, Blackhole bh) throws IOException {
        FormDef formDef = FormDefCache.readCache(state.xFormXmlFile, state.CACHE_PATH);
        bh.consume(formDef);
    }

}
