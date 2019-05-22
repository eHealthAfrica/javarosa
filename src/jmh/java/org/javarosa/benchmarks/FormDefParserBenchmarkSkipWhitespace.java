package org.javarosa.benchmarks;

import org.javarosa.core.model.FormDef;
import org.javarosa.xform.parse.FormParserHelper;
import org.javarosa.xform.parse.IXFormParserFactory;
import org.javarosa.xform.parse.XFormParserFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Path;

import static org.javarosa.benchmarks.BenchmarkUtils.*;

public class FormDefParserBenchmarkSkipWhitespace {
    public static void main(String[] args) {
        dryRun(FormDefParserBenchmarkSkipWhitespace.class);
    }

    @State(Scope.Thread)
    public static class FormDefParserState {
        Path xFormExternalInstanceFilePath;
        Path xFormInternalInstanceFilePath;
        Path xFormMinifiedInternalInstanceFilePath;

        private static IXFormParserFactory _factory = new XFormParserFactory();

        public static IXFormParserFactory setXFormParserFactory(IXFormParserFactory factory) {
            IXFormParserFactory oldFactory = _factory;
            _factory = factory;
            return oldFactory;
        }

        @Setup(Level.Trial)
        public void initialize() {
            xFormExternalInstanceFilePath = getNigeriaWardsXMLWithExternal2ndryInstance();
            xFormInternalInstanceFilePath = getNigeriaWardsXMLWithInternal2ndryInstance();
            xFormMinifiedInternalInstanceFilePath = getMinifiedNigeriaWardsXMLWithInternal2ndryInstance();
        }
    }

    @Benchmark
    public void
    benchmarkParseExternalSecondaryInstance(FormDefParserState state, Blackhole bh) throws IOException {
        FormDef formDef = FormParserHelper.parse(state.xFormExternalInstanceFilePath, true);
        bh.consume(formDef);
    }

    @Benchmark
    public void
    benchmarkParseInternalSecondaryInstance(FormDefParserState state, Blackhole bh) throws IOException {
        FormDef formDef = FormParserHelper.parse(state.xFormInternalInstanceFilePath, true);
        bh.consume(formDef);
    }

    @Benchmark
    public void
    benchmarkParseMinifiedInternalSecondaryInstance(FormDefParserState state, Blackhole bh) throws IOException {
        FormDef formDef = FormParserHelper.parse(state.xFormMinifiedInternalInstanceFilePath, true);
        bh.consume(formDef);
    }
}
