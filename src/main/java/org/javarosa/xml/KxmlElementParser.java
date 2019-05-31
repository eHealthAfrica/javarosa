package org.javarosa.xml;

import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author johnthebeloved
 * Most of logic copied from #TreeElementParser
 * Implementation builds Elements as in TreeElement parser and
 * also can be used to skip white space to improve performance
 */
public class KxmlElementParser extends ElementParser<Element> {

    private ElementSkipper[] elementSkippers;

    public KxmlElementParser(Reader reader) throws IOException, XmlPullParserException {
        this(new KXmlParser(), reader);
    }

    public KxmlElementParser(KXmlParser parser, Reader reader) throws IOException, XmlPullParserException {
        super(parser);
        parser.setInput(reader);
        parser.setFeature(KXmlParser.FEATURE_PROCESS_NAMESPACES, true);
        //Point to the first available tag.
        parser.next();

    }

    public KxmlElementParser(KXmlParser parser, Reader reader, ElementSkipper ...elementSkippers) throws IOException, XmlPullParserException {
        this(parser, reader);
        this.elementSkippers = elementSkippers;
    }


    public Element parse()
        throws IOException, XmlPullParserException {

        final int depth = parser.getDepth();
        Element element = initCurrentElement();
        final Map<String, Integer> multiplicitiesByName = new HashMap();
        while (parser.getDepth() >= depth) {
            switch (nextNonWhitespace()) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    if(shouldSkipSubTree(name, elementSkippers)){
                        Element elementToSkip = initCurrentElement();
                        element.addChild(Node.ELEMENT,elementToSkip);
                        parser.skipSubTree();
                    }else{
                        final Integer multiplicity = multiplicitiesByName.get(name);
                        int newMultiplicity = (multiplicity != null) ? multiplicity + 1 : 0;
                        multiplicitiesByName.put(name, newMultiplicity);
                        Element childElement = parse();
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

    private boolean shouldSkipSubTree(String elementName, ElementSkipper ...elementSkippers){
       for(int e= 0;e<elementSkippers.length; e++){
           ElementSkipper elementSkipper = elementSkippers[e];
           if(elementSkipper.skip(elementName)){
               return true;
           }
       }
       return false;
    }

    private Element initCurrentElement(){
        Element element = new Element();
        element.setName(parser.getName());
        for (int i = parser.getNamespaceCount (parser.getDepth () - 1);
             i < parser.getNamespaceCount (parser.getDepth ()); i++) {
            element.setPrefix (parser.getNamespacePrefix (i), parser.getNamespaceUri(i));
        }

        for (int i = 0; i < parser.getAttributeCount(); ++i) {
            element.setAttribute(parser.getAttributeNamespace(i), parser.getAttributeName(i), parser.getAttributeValue(i));
        }
        return element;
    }

}
