package org.javarosa.xml;

import org.javarosa.core.model.data.UncastData;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.XmlExternalInstance;
import org.javarosa.core.reference.InvalidReferenceException;
import org.javarosa.core.util.InvalidIndexException;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ctsims
 */
public class TreeElementParser extends ElementParser<TreeElement> {
    private final int multiplicity;
    private final String instanceId;

    public TreeElementParser(KXmlParser parser, int multiplicity, String instanceId) {
        super(parser);
        this.multiplicity = multiplicity;
        this.instanceId = instanceId;
    }

    @Override
    public TreeElement parse() throws InvalidStructureException, IOException,
            XmlPullParserException, UnfullfilledRequirementsException {

        final int depth = parser.getDepth();
        final TreeElement element = new TreeElement(parser.getName(), multiplicity);
        element.setInstanceName(instanceId);
        for (int i = 0; i < parser.getAttributeCount(); ++i) {
            element.setAttribute(parser.getAttributeNamespace(i), parser.getAttributeName(i), parser.getAttributeValue(i));
        }

        final Map<String, Integer> multiplicitiesByName = new HashMap<>();

        // loop parses all siblings at a given depth
        while (parser.getDepth() >= depth) {
            switch (nextNonWhitespace()) {
                case KXmlParser.START_TAG:
                    String name = parser.getName();
                    final Integer multiplicity = multiplicitiesByName.get(name);
                    int newMultiplicity = (multiplicity != null) ? multiplicity + 1 : 0;
                    multiplicitiesByName.put(name, newMultiplicity);
                    TreeElement childTreeElement = new TreeElementParser(parser, newMultiplicity, instanceId).parse();
                    element.addChild(childTreeElement);
                    break;
                case KXmlParser.END_TAG:
                    return element;
                case KXmlParser.TEXT:
                    element.setValue(new UncastData(parser.getText().trim()));
                    break;
                default:
                    throw new InvalidStructureException(
                            "Exception while trying to parse an XML Tree, got something other than tags and text", parser);
            }
        }

        return element;
    }


    public TreeElement parseInternalInstance(Integer index) throws IOException, InvalidReferenceException, InvalidStructureException, XmlPullParserException, UnfullfilledRequirementsException {

        final int depth = parser.getDepth();
        int foundInstanceIndex = -1;

        findInstanceNode();
        while (parser.getDepth() >= depth) {
            while(findInstanceNode()){
                foundInstanceIndex+=1;
                if(foundInstanceIndex == index){
                    return new TreeElementParser(parser, 0, "").parse();
                }else{
                    parser.skipSubTree();
                }
            }
        }
        throw new InvalidIndexException(String.format("The instance index %s was not found in the XForm file at ", index), index.toString());
    }


    public List<TreeElement> parseInternalInstance() throws IOException, InvalidStructureException, XmlPullParserException, UnfullfilledRequirementsException {
        List<TreeElement> internalInstances = new ArrayList<>();
        final int depth = parser.getDepth();
        while (parser.getDepth() >= depth) {
            while(findInstanceNode()){
                TreeElement treeElement = new TreeElementParser(parser, 0, "").parse();
                if(treeElement.getAttributeValue("","id") !=null){
                    String instanceId = treeElement.getAttributeValue("","id");
                    if(instanceId != null){
                        treeElement.setInstanceName(instanceId);
                        internalInstances.add(treeElement);
                    }
                }
            }
        }
        return internalInstances;
    }





    public boolean findInstanceNode() throws XmlPullParserException, IOException {
        int ret = nextNonWhitespace();
        if (ret == Node.ELEMENT && parser.getName().equals("instance") ) {
            return true;
        }else if(ret != KXmlParser.END_TAG) {
            return false;
        }else {
            return findInstanceNode();
        }
    }

}
