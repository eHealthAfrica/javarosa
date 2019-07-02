package org.javarosa.benchmarks;

import org.javarosa.benchmarks.utils.FileGeneratorUtil;
import org.javarosa.benchmarks.utils.FormDefCache;
import org.javarosa.core.model.CoreModelModule;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.services.PrototypeManager;
import org.javarosa.core.util.JavaRosaCoreModule;
import org.javarosa.model.xform.XFormsModule;
import org.javarosa.xform.parse.FormParserHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.getCachePath;

public class DynamicXFormCachingBenchmark {

    public static void main(String[] args) {
        dryRun(DynamicXFormCachingBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        Path formXmlBeforePath;
        Path formXmlAfterPath;
        String cachePath;
        FormDef formDefBefore;
        FormDef formDefAfter;
        @Setup(Level.Trial)
        public void initialize() throws IOException {
            formXmlBeforePath = FileGeneratorUtil.generate(5,10,0,100,null);
            formXmlAfterPath = FileGeneratorUtil.generate(5,100,0,100,null);
            formDefBefore = FormParserHelper.parse(formXmlBeforePath, false);
            formDefAfter = FormParserHelper.parse(formXmlAfterPath, true);
            cachePath = getCachePath().toString();
            PrototypeManager.registerPrototypes(JavaRosaCoreModule.classNames);
            PrototypeManager.registerPrototypes(CoreModelModule.classNames);
            new XFormsModule().registerModule();
        }
    }

    @Benchmark
    public void benchmark1Before(FormTypesState state, Blackhole bh) throws IOException {
        runWriteBenchmark(state.formDefBefore, state.formXmlBeforePath, state.cachePath);
        FormDef formDef = runReadBenchmark(state.formXmlBeforePath.toFile(), state.cachePath);
        bh.consume(formDef);
    }


    @Benchmark
    public void benchmark2After(FormTypesState state, Blackhole bh) throws IOException {
        runWriteBenchmark(state.formDefAfter, state.formXmlAfterPath, state.cachePath);
        FormDef formDef = runReadBenchmark(state.formXmlAfterPath.toFile(), state.cachePath);
        bh.consume(formDef);
    }

    private void runWriteBenchmark(FormDef formDef,Path resourcePath, String cachePath) throws IOException {
         FormDefCache.writeCache(formDef, resourcePath.toString(), cachePath);
    }
    private FormDef runReadBenchmark(File formXml, String cachePath) {
        return FormDefCache.readCache(formXml, cachePath);
    }

}