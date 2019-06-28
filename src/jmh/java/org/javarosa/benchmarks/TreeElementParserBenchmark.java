package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.BenchmarkUtils;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.xml.ElementParser;
import org.javarosa.xml.TreeElementParser;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.kxml2.io.KXmlParser;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;

public class TreeElementParserBenchmark {
    public static void main(String[] args) {
        dryRun(TreeElementParserBenchmark.class);
    }

    @State(Scope.Thread)
    public static class TreeElementParserBenchmarkState {
        String xFormFile;
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
            xFormFile = BenchmarkUtils.generateXFormFile(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements).getPath();

        }
    }

    @Benchmark
    public void runBenchmark(TreeElementParserBenchmarkState state, Blackhole bh) throws IOException, UnfullfilledRequirementsException, XmlPullParserException, InvalidStructureException {
        TreeElement documentRootTreeElement = parse(state.xFormFile);
        bh.consume(documentRootTreeElement);
    }

    private static TreeElement parse(String path) throws IOException, InvalidStructureException, XmlPullParserException, UnfullfilledRequirementsException {
        InputStream inputStream = new FileInputStream(path);
        KXmlParser xmlParser = ElementParser.instantiateParser(inputStream);
        TreeElementParser treeElementParser = new TreeElementParser(xmlParser, 0, "");
        return treeElementParser.parse();
    }

}
