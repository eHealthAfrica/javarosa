package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.BenchmarkUtils;
import org.javarosa.xform.parse.XFormParser;
import org.kxml2.kdom.Document;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;

public class XFormParserBenchmark {
    public static void main(String[] args) {
        dryRun(XFormParserBenchmark.class);
    }

    @State(Scope.Thread)
    public static class XFormParserBenchmarkState {
        File xFormXmlFile ;
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
            xFormXmlFile = BenchmarkUtils.generateXFormFile(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements);
        }
    }

    @Benchmark
    public void
    runBenchmark(XFormParserBenchmarkState state, Blackhole bh)
        throws IOException {
        Reader reader = new FileReader(state.xFormXmlFile);
        Document kxmlDocument = XFormParser.getXMLDocument(reader);
        bh.consume(kxmlDocument);
    }


}
