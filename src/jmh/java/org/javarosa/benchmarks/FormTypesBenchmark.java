package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.XFormFileBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static org.javarosa.benchmarks.BenchmarkUtils.dryRun;

public class FormTypesBenchmark {

    public static void main(String[] args) {
        dryRun(FormTypesBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        XFormFileBuilder  xFormFileBuilder;
        @Setup(Level.Trial)
        public void initialize() {
            xFormFileBuilder = new XFormFileBuilder();
        }
    }

    @Benchmark
    public void benchmarkParseMinifiedInternalInstanceXForm(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        String xml = state.xFormFileBuilder.createXFormWithComplexity();
        System.out.println(xml);
    }

}