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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;

public class DynamicXFormBenchmark {

    public static void main(String[] args) {
        dryRun(DynamicXFormBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        Path formPath ;

        @Setup(Level.Trial)
        public void initialize() throws IOException {
            formPath = FileGeneratorUtil.generate(5,100,0,100,null);

            String xml = "<instance id=\"idhere\"><instance id=\"idhere\"><instance id=\"idhere\"><instance id=\"idhere\"></instance></instance></instance></instance>";
            String id = getInstanceName(xml);
            System.out.println(id);

        }

        private String getInstanceName(String instanceXML){
            Pattern pattern = Pattern.compile("<instance\\sid=\"([^\"]+)");
            Matcher matcher = pattern.matcher(instanceXML);
            if (matcher.find())
                System.out.println(matcher.group(1));
            return  matcher.group(1);

        }
    }

    @Benchmark
    public void before(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBeforeBenchmark(state.formPath);
        bh.consume(formDef);
    }

    @Benchmark
    public void after(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runAfterBenchmark(state.formPath);
        bh.consume(formDef);
    }

    private FormDef runBeforeBenchmark(Path filePath) throws IOException {
        return FormParserHelper.parse(filePath, false);
    }
    private FormDef runAfterBenchmark(Path filePath) throws IOException {
        return FormParserHelper.parse(filePath, true);
    }

}