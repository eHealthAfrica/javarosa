package org.javarosa.benchmarks;

import org.javarosa.core.model.instance.InternalDataInstance;
import org.javarosa.core.reference.InvalidReferenceException;
import org.javarosa.xml.InternalDataInstanceParser;
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

import java.io.IOException;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.generateXFormFile;

public class InternalDataInstanceBuildBenchmark {
    public static void main(String[] args) {
        dryRun(InternalDataInstanceBuildBenchmark.class);
    }

    @State(Scope.Thread)
    public static class InternalDataInstanceState {
        private String xFormXmlFile;
        @Param({"10", "500"})
        public int noOfQuestions = 1;
        @Param({"10", "50"})
        public int noOfInternalSecondaryInstances = 1;
        @Param({"0", "50", "1000"})
        public int noOf2ndryInstanceElements = 1;
        @Param({"1"})
        public int noOfQuestionGroups = 1;
        @Param({"0","50"})
        public int noOfExternalSecondaryInstances = 1;
        @Setup(Level.Trial)
        public void initialize() throws IOException {
            xFormXmlFile = generateXFormFile(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements).getPath();
        }
    }

    @Benchmark
    public void runBenchmark(InternalDataInstanceState state, Blackhole bh)
        throws IOException, XmlPullParserException, InvalidReferenceException,
        UnfullfilledRequirementsException, InvalidStructureException {
        InternalDataInstance wardsInternalInstance =
            InternalDataInstanceParser.build(state.xFormXmlFile, "internal_secondary_instance_1");
        bh.consume(wardsInternalInstance);
    }

}
