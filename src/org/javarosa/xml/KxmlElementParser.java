package org.javarosa.xml;

import org.javarosa.core.model.data.UncastData;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author johnthebeloved
 */
public class KxmlElementParser extends ElementParser<Element> {

    public KxmlElementParser(KXmlParser parser, Reader reader) throws IOException {
        super(parser);
        initialize(reader);

    }

    private static final Logger logger = LoggerFactory.getLogger(KxmlElementParser.class);

    /**
     * Prepares a parser that will be used by the element parser, configuring relevant
     * parameters and setting it to the appropriate point in the document.
     *
     * @param reader A stream which is reading the XML content
     *               of the document.
     * @throws IOException If the stream cannot be read for any reason
     *                     other than invalid XML Structures.
     */
    public void initialize(Reader reader) throws IOException {
       try {
            parser.setInput(reader);
            parser.setFeature(KXmlParser.FEATURE_PROCESS_NAMESPACES, true);

            //Point to the first available tag.
            parser.next();

        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            logger.error("Element Parser", e);
            throw new IOException(e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }


    public Element parse(String ...skipSubTrees)
        throws IOException, XmlPullParserException {

        final int depth = parser.getDepth();
        Element element =   new Element().createElement(parser.getNamespace(), parser.getName());
        for (int i = parser.getNamespaceCount (parser.getDepth () - 1);
             i < parser.getNamespaceCount (parser.getDepth ()); i++) {
            element.setPrefix (parser.getNamespacePrefix (i), parser.getNamespaceUri(i));
        }

        for (int i = 0; i < parser.getAttributeCount(); ++i) {
            element.setAttribute(parser.getAttributeNamespace(i), parser.getAttributeName(i), parser.getAttributeValue(i));
        }
        final Map<String, Integer> multiplicitiesByName = new HashMap();

        while (parser.getDepth() >= depth) {
            int type = nextNonWhitespace();
            switch (type) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    if(containsElementName(name, skipSubTrees)){
                        parser.skipSubTree();
                    }else{
                        final Integer multiplicity = multiplicitiesByName.get(name);
                        int newMultiplicity = (multiplicity != null) ? multiplicity + 1 : 0;
                        multiplicitiesByName.put(name, newMultiplicity);
                        Element childElement = parse(skipSubTrees);
                        element.addChild(Node.ELEMENT, childElement);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    return element;
                case XmlPullParser.TEXT:
                    if (parser.getText() != null)
                        element.addChild(Node.TEXT, parser.getText().trim());
                    break;
                default:
                    throw new XmlPullParserException(
                        "Exception while trying to parse an XML Tree, got something other than tags and text");
            }
        }
        return  element;

    }

    private boolean containsElementName(String text, String ...texts){
       return Arrays.asList(texts).contains(text);
    }


    public Element parse()
        throws IOException, XmlPullParserException {

        final int depth = parser.getDepth();
        Element element =   new Element().createElement(parser.getNamespace(), parser.getName());
        for (int i = parser.getNamespaceCount (parser.getDepth () - 1);
             i < parser.getNamespaceCount (parser.getDepth ()); i++) {
            element.setPrefix (parser.getNamespacePrefix (i), parser.getNamespaceUri(i));
        }

        for (int i = 0; i < parser.getAttributeCount(); ++i) {
            element.setAttribute(parser.getAttributeNamespace(i), parser.getAttributeName(i), parser.getAttributeValue(i));
        }
        final Map<String, Integer> multiplicitiesByName = new HashMap();

        while (parser.getDepth() >= depth) {
            int type = nextNonWhitespace();
            switch (type) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    final Integer multiplicity = multiplicitiesByName.get(name);
                    int newMultiplicity = (multiplicity != null) ? multiplicity + 1 : 0;
                    multiplicitiesByName.put(name, newMultiplicity);
                    Element childElement = parse();
                    element.addChild(Node.ELEMENT, childElement);
                    break;
                case XmlPullParser.END_TAG:
                    return element;
                case XmlPullParser.TEXT:
                    if (parser.getText() != null)
                        element.addChild(Node.TEXT, parser.getText().trim());
                    break;
                default:
                    throw new XmlPullParserException(
                        "Exception while trying to parse an XML Tree, got something other than tags and text");
            }
        }
        return  element;

    }



}
