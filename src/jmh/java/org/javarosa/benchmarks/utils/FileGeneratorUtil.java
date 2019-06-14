package org.javarosa.benchmarks.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileGeneratorUtil {

    public static Path level1() throws IOException {
        return level1(null);
    }
        public static Path level1(File directory) throws IOException {

        Map<String, String> namespaces =
            XFormFileBuilder.buildMap(
                new String[]{"", "http://www.w3.org/2002/xforms"},
                new String[]{"h", "http://www.w3.org/1999/xhtml"},
                new String[]{"ev", "http://www.w3.org/2001/xml-events"},
                new String[]{"jr", "http://openrosa.org/javarosa"},
                new String[]{"orx", "http://openrosa.org/xforms"},
                new String[]{"xsd", "http://www.w3.org/2001/XMLSchema"}
            );

        List<SecondaryInstanceDef> secondaryInstanceDefList = Arrays.asList(
            new SecondaryInstanceDef("first", 10)
//            new SecondaryInstanceDef("second", 20),
//            new SecondaryInstanceDef("third", 30),
//            new SecondaryInstanceDef("forth", 40),
//            new SecondaryInstanceDef("fifth", 500),
//            new SecondaryInstanceDef("fifth", 1000)
        );

        XFormComplexity xFormComplexity =
            new XFormComplexity(
                "Generated Form",
                1000,
                secondaryInstanceDefList,
            null,
                namespaces);

        XFormFileBuilder xFormFileBuilder = new XFormFileBuilder(xFormComplexity);

        File file = File.createTempFile("x_form_" + System.currentTimeMillis(), "xml", directory);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(xFormFileBuilder.build());
        fileWriter.close();
        return file.toPath();
    }
}
