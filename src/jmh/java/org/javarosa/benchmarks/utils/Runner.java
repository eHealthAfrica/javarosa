package org.javarosa.benchmarks.utils;

import org.javarosa.core.model.FormDef;
import org.javarosa.xform.util.XFormUtils;
import org.javarosa.xml.ElementParser;
import org.javarosa.xml.KXmlElementParser;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

public class Runner {

    public static void main(String [] args) throws IOException, XmlPullParserException {

        File file = new File(FileGeneratorUtil.level1().toString());
        String xml = new String(Files.readAllBytes(file.toPath()));

        KXmlParser kXmlParser = ElementParser.instantiateParser(new FileReader(file));
        Document document;
        document = new KXmlElementParser(kXmlParser).parseDoc();

        FormDef formDef = XFormUtils.getFormFromFile(file.getPath(),null);
        System.out.println(xml);
        System.out.println("Path - " + file.getPath());
        System.out.println("Document child count - " + document.getChildCount());
        System.out.println("Form title  - " + formDef.getName());
        System.out.println("Name of main instance - " + formDef.getMainInstance().getName());
        System.out.println("No Of non main instances - " + Collections.list(formDef.getNonMainInstances()).size());
    }


}
