package org.javarosa.benchmarks.utils;

import static org.javarosa.benchmarks.BenchmarkUtils.prepareAssets;

import java.nio.file.Path;

public class FormBinUtils{

    public static Path getEIMCI(){
        Path assetsPath = prepareAssets("random_eIMCI.xml");
        Path filePath = assetsPath.resolve("random_eIMCI.xml");
        return filePath;
    }

}