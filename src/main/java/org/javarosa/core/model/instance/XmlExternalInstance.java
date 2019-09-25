package org.javarosa.core.model.instance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.javarosa.xform.parse.XFormParser;
import org.javarosa.xml.ElementParser;
import org.javarosa.xml.TreeElementParser;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlExternalInstance {
    public static TreeElement parse(String instanceId, String path) throws IOException, InvalidStructureException, XmlPullParserException, UnfullfilledRequirementsException {
        InputStream inputStream = new FileInputStream(path);
        KXmlParser xmlParser = ElementParser.instantiateParser(inputStream);
        TreeElementParser treeElementParser = new TreeElementParser(xmlParser, 0, instanceId);
        // #Indexation
        // Maybe IndexerResolver should be passed as an argument to this method instead  XFormParser.getIndexerResolver()
        if(XFormParser.getIndexerResolver() != null){
            return treeElementParser.parse(null, XFormParser.getIndexerResolver());
        }else{
            return treeElementParser.parse();
        }
    }
}
