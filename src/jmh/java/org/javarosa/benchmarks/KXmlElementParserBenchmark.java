package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.BenchmarkUtils;
import org.javarosa.xml.KXmlElementParser;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
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

public class KXmlElementParserBenchmark {
    public static void main(String[] args) {
        dryRun(KXmlElementParserBenchmark.class);
    }

    @State(Scope.Thread)
    public static class ElementParserState {
        String xFormFile;
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
            xFormFile = BenchmarkUtils.generateXFormFile(noOfQuestions, noOfQuestionGroups, noOfInternalSecondaryInstances, noOfExternalSecondaryInstances, noOf2ndryInstanceElements).getPath();
        }
    }

    @Benchmark
    public void runBenchmark(ElementParserState state, Blackhole bh) throws IOException, XmlPullParserException  {
        Element documentRootElement = parse( state.xFormFile);
        bh.consume(documentRootElement);
    }

    public static Element parse(String path) throws IOException, XmlPullParserException {
        InputStream inputStream = new FileInputStream(path);
        KXmlParser xmlParser = KXmlElementParser.instantiateParser(inputStream);
        KXmlElementParser KXmlElementParser = new KXmlElementParser(xmlParser);
        Document document = KXmlElementParser.parseDoc();
        return document.getRootElement();
    }
}
