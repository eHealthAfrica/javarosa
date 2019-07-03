package org.javarosa.benchmarks;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.getWorkingDir;
import static org.javarosa.core.reference.ReferenceManagerTestUtils.setUpSimpleReferenceManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.javarosa.benchmarks.utils.BenchmarkUtils;
import org.javarosa.core.model.instance.ExternalDataInstance;
import org.javarosa.core.reference.InvalidReferenceException;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.xmlpull.v1.XmlPullParserException;

public class ExternalDataInstanceBuildBenchmark {
    public static void main(String[] args) {
        dryRun(ExternalDataInstanceBuildBenchmark.class);
    }

    @State(Scope.Thread)
    public static class ExternalDataInstanceState {
        String instanceId;
        String instanceFileName;
        public int noOfQuestions = 1;
        public int noOfInternalSecondaryInstances = 0;
        @Param({"50", "1000"})
        public int noOf2ndryInstanceElements = 1;
        @Param({"1"})
        public int noOfQuestionGroups = 1;
        @Param({"50"})
        public int noOfExternalSecondaryInstances = 1;
        @Setup(Level.Trial)
        public void initialize() throws IOException {
            Map<String, Path> externalInstanceFiles = BenchmarkUtils.generateExternalSecondaryInstances(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements);
            Map.Entry<String, Path> generatedInstance = externalInstanceFiles.entrySet().iterator().next();
            instanceId = generatedInstance.getKey();
            instanceFileName = generatedInstance.getValue().toFile().getName();
            setUpSimpleReferenceManager("file", getWorkingDir());
        }

    }

    @Benchmark
    public void runBenchmark(ExternalDataInstanceState state, Blackhole bh)
        throws IOException, XmlPullParserException, InvalidReferenceException,
        UnfullfilledRequirementsException, InvalidStructureException {
        ExternalDataInstance wardsExternalInstance =
            ExternalDataInstance.build("jr://file/"+ state.instanceFileName, state.instanceId);
        bh.consume(wardsExternalInstance);
    }

}
