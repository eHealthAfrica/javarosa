package org.javarosa.core.benchmark;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.LongData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.core.model.data.UncastData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.test.FormParseInit;
import org.javarosa.core.util.PathConst;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.PrototypeFactory;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.xform.parse.IXFormParserFactory;
import org.javarosa.xform.parse.XFormParser;
import org.javarosa.xform.parse.XFormParserFactory;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.javarosa.test.utils.ResourcePathHelper.r;
import static org.junit.Assert.fail;


public class BenchmarkUtils {

    public static InputStream getFileInputStream(String path){
        File initialFile = new File(path);
        try (InputStream targetStream = new FileInputStream(initialFile)) {
                return targetStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static IXFormParserFactory _factory = new XFormParserFactory();
    public static IXFormParserFactory setXFormParserFactory(IXFormParserFactory factory) {
        IXFormParserFactory oldFactory = _factory;
        _factory = factory;
        return oldFactory;
    }

    public static  IXFormParserFactory getXFormParserFactory(){
        return _factory;
    }


    public static Object benchMark2(){
        // Given
        FormParseInit formParseInit = new FormParseInit(r("populate-nodes-attributes.xml"));

        FormEntryController formEntryController = formParseInit.getFormEntryController();

        byte[] formInstanceAsBytes = null;
        try {
            formInstanceAsBytes =
                Files.readAllBytes(Paths.get(PathConst.getTestResourcePath().getAbsolutePath(),
                    "populate-nodes-attributes-instance.xml"));
        } catch (IOException e) {
            fail("There was a problem with reading the test data.\n" + e.getMessage());
        }
        TreeElement savedRoot = XFormParser.restoreDataModel(formInstanceAsBytes, null).getRoot();
        FormDef formDef = formEntryController.getModel().getForm();
        TreeElement dataRootNode = formDef.getInstance().getRoot().deepCopy(true);
        return dataRootNode;
    }

    public static Options getJVMOptions(String className){
        Options opt = new OptionsBuilder()
            // Specify which benchmarks to run.
            // You can be more specific if you'd like to run only one benchmark per test.
            .include(className + ".*")
            // Set the following options as needed
            .mode(Mode.AverageTime)
            .timeUnit(TimeUnit.SECONDS)
            .warmupTime(TimeValue.seconds(5))
            .warmupIterations(1)
            .measurementTime(TimeValue.seconds(1))
            .threads(2)
            .measurementIterations(1)
            .forks(1)
            .shouldFailOnError(true)
            .shouldDoGC(true)
            //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
            //.jvmArgs("-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:MaxRAMFraction=10")
            //.addProfiler(WinPerfAsmProfiler.class)
            .build();

        return opt;
    }


    public static IAnswerData getStubAnswer(QuestionDef question) {
        IAnswerData answer;
        switch (question.getLabelInnerText()){
            case "State":
                answer = new SelectOneData(new Selection(question.getChoices().get(0)));
                break;
            case "LGA":
                answer = new SelectOneData(new Selection(question.getDynamicChoices().getChoices().get(0)));
                break;
            case "Ward":
                answer = new SelectOneData(new Selection(question.getDynamicChoices().getChoices().get(0)));
                break;
            case "Comments":
                answer = new StringData("No Comment");
                break;
            case "What population do you want to search for?":
                answer = new LongData(699967);
                break;
            default:
                answer = new LongData(0);

        }
        return answer;
    }

}
