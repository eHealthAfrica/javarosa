package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.FormBinUtils;
import org.javarosa.benchmarks.utils.XFormFileBuilder;
import org.javarosa.core.model.FormDef;
import org.javarosa.xform.parse.FormParserHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Path;

import static org.javarosa.benchmarks.BenchmarkUtils.dryRun;

public class FormTypesBenchmark2 {

    public static void main(String[] args) {
        dryRun(FormTypesBenchmark2.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        XFormFileBuilder  xFormFileBuilder;
        Path eIMCI;

        @Setup(Level.Trial)
        public void initialize() {
            xFormFileBuilder = new XFormFileBuilder();
            eIMCI = FormBinUtils.getEIMCI();
        }
    }

    @Benchmark
    public void benchmarkForm_eIMCI_old(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = FormParserHelper.parse(state.eIMCI, false);
        bh.consume(formDef);
    }

}