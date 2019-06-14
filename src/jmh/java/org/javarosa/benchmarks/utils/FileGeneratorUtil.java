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

        int itemsetMultiplier = 200;

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
            new SecondaryInstanceDef("first", itemsetMultiplier),
            new SecondaryInstanceDef("second", itemsetMultiplier * 2),
            new SecondaryInstanceDef("third", itemsetMultiplier * 3),
            new SecondaryInstanceDef("forth", itemsetMultiplier * 4),
            new SecondaryInstanceDef("fifth", itemsetMultiplier * 5)
        );


        List<QuestionGroup> questionGroups = Arrays.asList(
            new QuestionGroup("group1", itemsetMultiplier),
            new QuestionGroup("group2", itemsetMultiplier * 2),
            new QuestionGroup("group3", itemsetMultiplier * 3),
            new QuestionGroup("group4", itemsetMultiplier * 4),
            new QuestionGroup("group5", itemsetMultiplier * 5)
        );

        XFormComplexity xFormComplexity =
            new XFormComplexity(
                "Generated Form",
                itemsetMultiplier * 5,
                questionGroups,
                secondaryInstanceDefList,
            null,
                namespaces);

        XFormFileBuilder xFormFileBuilder = new XFormFileBuilder(xFormComplexity);

        File file = File.createTempFile("x_form_" + System.currentTimeMillis(), ".xml", directory);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(xFormFileBuilder.build());
        fileWriter.close();
        return file.toPath();
    }
}
