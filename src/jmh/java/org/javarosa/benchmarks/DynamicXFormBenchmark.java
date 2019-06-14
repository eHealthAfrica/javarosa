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
import static org.javarosa.benchmarks.BenchmarkUtils.prepareAssets;

public class DynamicFormTypesBeforeChangeBenchmark {

    public static void main(String[] args) {
        dryRun(FormTypesAfterChangeBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        Path xformM1K1S1K ;

        @Setup(Level.Trial)
        public void initialize() throws IOException {
            String filePathString = FileGeneratorUtil.level1().toString();
            Path assetsPath = prepareAssets(filePathString);
            xformM1K1S1K = assetsPath.resolve(filePathString);
        }
    }

    @Benchmark
    public void benchmarkForm_EIMCI(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBenchmark(state.xformM1K1S1K);
        bh.consume(formDef);
    }

    private FormDef runBenchmark(Path filePath) throws IOException {
        return FormParserHelper.parse(filePath, false);
    }

}