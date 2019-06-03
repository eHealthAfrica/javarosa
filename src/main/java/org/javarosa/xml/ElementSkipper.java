package org.javarosa.xml;


/**
 * @author johnthebeloved
 * Abstracts algorithm for skipping child elements
 * This could be implemented with XPath reference
 */
public class ElementSkipper
{
    private String elementName;
    private int from;
    private int to;
    private int currentParsingIndex;

    /**
     * Starts skipping  from the provided from parameter to the last sibling
     * @param elementName  The name of the element to skip
     * @param from the currentParsingIndex to begin skipping subtrees
     *
     */
    public ElementSkipper(String elementName, int from){
        this(elementName,from, 0);
    }

    /**
     * Starts skipping  from the provided from parameter to the last sibling
     * @param elementName  The name of the element to skip
     * @param from the multiplicity currentParsingIndex to begin skipping
     * @param to the multiplicity currentParsingIndex to end skipping
     */
    public ElementSkipper(String elementName, int from, int to){
        this.from = from;
        this.to = to;
        currentParsingIndex = -1;
        this.elementName = elementName;
    }

    public boolean skip(String elementName){
        if(this.elementName.equals(elementName)){
            currentParsingIndex +=1;
            return (currentParsingIndex >= from && (currentParsingIndex <= to || to == 0));
        }
        return false;
    }

}