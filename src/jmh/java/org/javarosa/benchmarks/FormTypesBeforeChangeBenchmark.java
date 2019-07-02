package org.javarosa.benchmarks;

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

import static org.javarosa.benchmarks.utils.BenchmarkUtils.dryRun;
import static org.javarosa.benchmarks.utils.BenchmarkUtils.prepareAssets;

public class FormTypesBeforeChangeBenchmark {

    public static void main(String[] args) {
        dryRun(FormTypesAfterChangeBenchmark.class);
    }

    @State(Scope.Thread)
    public static class FormTypesState {
        Path eimci;
        Path nhirEcho;
        Path vacinationFollowup;
        Path vacinationEnrollment;
        Path odkSRMonitoring;
        Path whoVA2016;

        @Setup(Level.Trial)
        public void initialize() {
            Path assetsPath = prepareAssets("random_eIMCI.xml",
                "NIHR_echo.xml",
                "child_vaccination_enrollment.xml",
                "child_vaccination_followup.xml",
                "ODK_FILES_FOR_SP_MONITORING_VER_ONE.xml",
                "WHOVA2016_v1_4_1_XLS_form_for_ODK.xml"
            );
            eimci = assetsPath.resolve("random_eIMCI.xml");
            nhirEcho = assetsPath.resolve("NIHR_echo.xml");
            vacinationEnrollment = assetsPath.resolve("child_vaccination_enrollment.xml");
            vacinationFollowup = assetsPath.resolve("child_vaccination_followup.xml");
            odkSRMonitoring = assetsPath.resolve("ODK_FILES_FOR_SP_MONITORING_VER_ONE.xml");
            whoVA2016 = assetsPath.resolve("WHOVA2016_v1_4_1_XLS_form_for_ODK.xml");
        }
    }

    @Benchmark
    public void benchmarkForm_EIMCI(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBenchmark(state.eimci);
        bh.consume(formDef);
    }

    @Benchmark
    public void benchmarkForm_NHIRecho(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBenchmark(state.nhirEcho);
        bh.consume(formDef);
    }

    @Benchmark
    public void benchmarkForm_childVacination(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBenchmark(state.vacinationFollowup);
        bh.consume(formDef);
    }

    @Benchmark
    public void benchmarkForm_childEnrollment(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBenchmark(state.vacinationEnrollment);
        bh.consume(formDef);
    }

    @Benchmark
    public void benchmarkForm_srMonitoring(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBenchmark(state.odkSRMonitoring);
        bh.consume(formDef);
    }

    @Benchmark
    public void benchmarkForm_WHOVA(FormTypesState state, Blackhole bh) throws IOException, XmlPullParserException {
        FormDef formDef = runBenchmark(state.whoVA2016);
        bh.consume(formDef);
    }



    private FormDef runBenchmark(Path filePath) throws IOException {
        return FormParserHelper.parse(filePath, false);
    }

}