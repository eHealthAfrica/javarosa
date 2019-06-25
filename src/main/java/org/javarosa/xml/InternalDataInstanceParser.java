package org.javarosa.xml;

import org.javarosa.core.model.instance.InternalDataInstance;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.reference.InvalidReferenceException;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InternalDataInstanceParser {



    public static List<InternalDataInstance> buildInstances(Map<String, Path> dataInstanceXmlStrings)
        throws IOException, UnfullfilledRequirementsException, XmlPullParserException, InvalidStructureException, InvalidReferenceException {
        List<InternalDataInstance> internalDataInstances = new ArrayList<>();
        for(Map.Entry<String, Path> dataInstance: dataInstanceXmlStrings.entrySet()){
           internalDataInstances.add(build(dataInstance.getKey(), dataInstance.getValue()));
        }
        return internalDataInstances;
    }


    /**
     * Builds the InternalDataInstance out of an xml String
     *
     * @param instancePath the string  which represents the internal data instance…
     * @return a list  new InternalDataInstance
     * @throws IOException                       if FileInputStream can’t find the file, or ElementParser can’t read the stream
     * @throws InvalidReferenceException         if the ReferenceManager in getPath(String srcLocation) can’t derive a reference
     * @throws UnfullfilledRequirementsException thrown by {@link TreeElementParser#parse()}
     * @throws XmlPullParserException            thrown by {@link TreeElementParser#parse()}
     * @throws InvalidStructureException         thrown by {@link TreeElementParser#parse()}
     */
    public static InternalDataInstance build(String instanceId, Path instancePath)
        throws IOException, UnfullfilledRequirementsException, XmlPullParserException, InvalidStructureException, InvalidReferenceException {
        String dataInstanceXmlString = new String(Files.readAllBytes(instancePath));
        StringReader reader = new StringReader(dataInstanceXmlString);
        KXmlParser parser = ElementParser.instantiateParser(reader);
        TreeElementParser treeElementParser =
            new TreeElementParser(parser,0, "");
        TreeElement treeElement = treeElementParser.parse();
        if (treeElement.getNumChildren() == 0)
            throw new RuntimeException("Root TreeElement node has no children");
        InternalDataInstance internalDataInstance  = new InternalDataInstance(treeElement.getChildAt(0), instanceId,  instancePath.toString());
        return internalDataInstance;
    }

    /**
     * Builds the InternalDataInstance out of an xml String
     *
     * @param path the string  which represents the internal data instance…
     * @return a list  new InternalDataInstance
     * @throws IOException                       if FileInputStream can’t find the file, or ElementParser can’t read the stream
     * @throws InvalidReferenceException         if the ReferenceManager in getPath(String srcLocation) can’t derive a reference
     * @throws UnfullfilledRequirementsException thrown by {@link TreeElementParser#parse()}
     * @throws XmlPullParserException            thrown by {@link TreeElementParser#parse()}
     * @throws InvalidStructureException         thrown by {@link TreeElementParser#parse()}
     */
    public static TreeElement buildRoot(String path)
        throws IOException, UnfullfilledRequirementsException, XmlPullParserException, InvalidStructureException, InvalidReferenceException {
        StringReader reader = new StringReader(new String(Files.readAllBytes(new File(path).toPath())));
        KXmlParser parser = ElementParser.instantiateParser(reader);
        TreeElementParser treeElementParser =
            new TreeElementParser(parser,0, "");
        return treeElementParser.parse();
    }


}