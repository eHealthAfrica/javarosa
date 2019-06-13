package org.javarosa.benchmarks.utils;

import static org.javarosa.benchmarks.BenchmarkUtils.prepareAssets;

import java.nio.file.Path;

public class FormBinUtils{

    public static Path prepForm(String fileName){
        Path assetsPath = prepareAssets(fileName);
        Path filePath = assetsPath.resolve("random_eIMCI.xml");
        return filePath;
    }

}