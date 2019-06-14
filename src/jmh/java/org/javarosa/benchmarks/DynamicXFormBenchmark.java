package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.FileGeneratorUtil;
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

public class DynamicXFormBenchmark {

    public static void main(String[] args) {
        dryRun(DynamicXFormBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        Path level1 ;

        @Setup(Level.Trial)
        public void initialize() throws IOException {
            level1 = FileGeneratorUtil.generate(5,100,100,null);
        }
    }

    @Benchmark
    public void before(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBeforeBenchmark(state.level1);
        bh.consume(formDef);
    }

    @Benchmark
    public void after(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runAfterBenchmark(state.level1);
        bh.consume(formDef);
    }

    private FormDef runBeforeBenchmark(Path filePath) throws IOException {
        return FormParserHelper.parse(filePath, false);
    }
    private FormDef runAfterBenchmark(Path filePath) throws IOException {
        return FormParserHelper.parse(filePath, true);
    }

}