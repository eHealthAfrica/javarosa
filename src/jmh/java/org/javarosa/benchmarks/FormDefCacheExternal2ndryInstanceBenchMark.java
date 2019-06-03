package org.javarosa.benchmarks;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FormDefCacheExternal2ndryInstanceBenchMark {
    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Thread)
    public static class FormDefCacheState {
        Path resourcePath;
        FormDef formDef;
        String cachePath;

        @Setup(Level.Trial)
        public void
        initialize() throws IOException {
            resourcePath = BenchmarkUtils.getNigeriaWardsXMLWithExternal2ndryInstance();
            formDef = FormParserHelper.parse(resourcePath);
            cachePath = BenchmarkUtils.prepareCache().toString();
            PrototypeManager.registerPrototypes(JavaRosaCoreModule.classNames);
            PrototypeManager.registerPrototypes(CoreModelModule.classNames);
            new XFormsModule().registerModule();
        }
    }

    @Benchmark
    public void
    benchmark1FormDefCacheWriteToCache(FormDefCacheState state, Blackhole bh) throws IOException {
        FormDefCache.writeCache(state.formDef, state.resourcePath.toString(), state.cachePath);
    }

    @Benchmark
    public void
    benchmark2FormDefCacheReadFromCache(FormDefCacheState state, Blackhole bh) throws IOException {
        FormDef cachedFormDef = FormDefCache.readCache(state.resourcePath.toFile(), state.cachePath);
        bh.consume(cachedFormDef);
    }
}
