package org.javarosa.xml;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author johnthebeloved
 * Abstracts algorithm for skipping child elements
 * This could be implemented with XPath reference
 * Also assumes that the elements to be skipped are siblings
 */
public class ElementSkipper
{
    /**
     * Signifies skipping should be from the start sibling to the last sibling
     */
    public static final int LAST_ELEMENT_INDEX = -1;
    private String elementName;
    private int from;
    private int to;
    private int currentParsingIndex;
    private List<String> xmlTree;

    /**
     * Starts skipping  from the provided from parameter to the last sibling
     * @param elementName  The name of the element to skip
     * @param from the currentParsingIndex to begin skipping subtrees
     *
     */
    public ElementSkipper(String elementName, int from){
        this(elementName,from, LAST_ELEMENT_INDEX);
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
        currentParsingIndex = 0;
        this.elementName = elementName;
        this.xmlTree = new ArrayList<>();
    }

    public boolean skip(String elementName){
        if(this.elementName.equals(elementName)){
            boolean skip = (currentParsingIndex >= from && (currentParsingIndex <= to || to == LAST_ELEMENT_INDEX));
            currentParsingIndex +=1;
            return skip;
        }
        return false;
    }

    public void addXmlTree(String xmlTree){
        this.xmlTree.add(xmlTree);
    }

    public Map<String, String> getInstances() {
        Map<String, String> fileLocationMap = new HashMap<>();
        try {
            for (String externalInstanceString : xmlTree) {
                String instanceId = getInstanceName(externalInstanceString);
                File externalInstanceFile = File.createTempFile(instanceId, ".xml");
                FileWriter fileWriter = new FileWriter(externalInstanceFile);
                fileWriter.write(externalInstanceString);
                fileWriter.close();
                fileLocationMap.put(instanceId, externalInstanceFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileLocationMap;
    }

    private String getInstanceName(String instanceXML){
        Pattern pattern = Pattern.compile("<instance\\sid=\"([^\"]+)");
        Matcher matcher = pattern.matcher(instanceXML);
        if (matcher.find())
            System.out.println(matcher.group(1));
       return  matcher.group(1);

    }


}