package org.javarosa.benchmarks;

import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.instance.InstanceInitializationFactory;
import org.javarosa.xform.parse.FormParserHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.file.Path;

import static org.javarosa.benchmarks.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.BenchmarkUtils.getNigeriaWardsXMLWithInternal2ndryInstance;

public class OptimizeFormDefQuestions21 {
    public static void main(String[] args) {
        dryRun(OptimizeFormDefQuestions21.class);
    }

    @State(Scope.Thread)
    public static class FormParserHelperParseInternalInstanceBenchmarkState {
        Path xFormFilePath;

        @Setup(Level.Trial)
        public void initialize() {
            FormDef.OPTIMZE_DYNAMIC_CHOICES = true;
            xFormFilePath = getNigeriaWardsXMLWithInternal2ndryInstance();
        }
    }

    @Benchmark
    public void
    benchmarkLoadInitForm(FormParserHelperParseInternalInstanceBenchmarkState state) throws IOException {
        FormDef formDef = FormParserHelper.parse(state.xFormFilePath);
        formDef.initialize(true, new InstanceInitializationFactory());

    }
}
