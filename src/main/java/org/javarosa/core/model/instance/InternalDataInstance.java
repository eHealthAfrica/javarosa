package org.javarosa.core.model.instance;

import org.javarosa.core.reference.InvalidReferenceException;
import org.javarosa.core.reference.ReferenceManager;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.core.util.externalizable.PrototypeFactory;
import org.javarosa.xml.ElementParser;
import org.javarosa.xml.TreeElementParser;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InternalDataInstance extends DataInstance {
    private static final Logger logger = LoggerFactory.getLogger(InternalDataInstance.class.getSimpleName());
    private String path;
    private TreeElement root;
    private int index;

    // todo Make @mdudzinski’s recommended changes from https://github.com/opendatakit/javarosa/pull/154#pullrequestreview-51806826

    /**
     * No-args constructor for deserialization
     */
    public InternalDataInstance() {
    }

    private InternalDataInstance(TreeElement root, String instanceId, String xFormPath) {
        super(instanceId);
        this.path = xFormPath;
        setName(instanceId);
        setRoot(root);
    }

    /**
     * Builds an InternalDataInstance
     *
     * @param xFormSrc the path  of the Xform containing external instances…
     * @return a new ExternalDataInstance
     * @throws IOException                       if FileInputStream can’t find the file, or ElementParser can’t read the stream
     * @throws InvalidReferenceException         if the ReferenceManager in getPath(String srcLocation) can’t derive a reference
     * @throws UnfullfilledRequirementsException thrown by {@link TreeElementParser#parse()}
     * @throws XmlPullParserException            thrown by {@link TreeElementParser#parse()}
     * @throws InvalidStructureException         thrown by {@link TreeElementParser#parse()}
     */
    public static List<InternalDataInstance> build(String xFormSrc)
        throws IOException, UnfullfilledRequirementsException, XmlPullParserException, InvalidStructureException, InvalidReferenceException {


        InputStream inputStream = new FileInputStream(xFormSrc);
        KXmlParser parser = ElementParser.instantiateParser(inputStream);
        TreeElementParser treeElementParser =
            new TreeElementParser(parser,0, "");


        List<TreeElement> instances = treeElementParser.parseInternalInstance();
        List<InternalDataInstance> internalDataInstances = new ArrayList<>();
        for(TreeElement element: instances){
            String id = element.getAttribute("", "id").getAttributeValue();
            InternalDataInstance internalDataInstance  = new InternalDataInstance(element.getChild("root",0), id,  xFormSrc);
            internalDataInstances.add(internalDataInstance);
        }
        return internalDataInstances;
    }

    private static TreeElement parseInternalInstance(String xFormSrc, int index) throws IOException, InvalidReferenceException, InvalidStructureException, XmlPullParserException, UnfullfilledRequirementsException {
        InputStream inputStream = new FileInputStream(xFormSrc);
        KXmlParser parser = ElementParser.instantiateParser(inputStream);
        TreeElement treeElement =  new TreeElementParser(parser, 0, "").parseInternalInstance(index);
        return treeElement;
    }


    @Override
    public AbstractTreeElement getBase() {
        return root;
    }

    @Override
    public AbstractTreeElement getRoot() {
        if (root.getNumChildren() == 0)
            throw new RuntimeException("root node has no children");

        return root.getChildAt(0);
    }

    private void setRoot(TreeElement topLevel) {
        root = new TreeElement();
        root.setInstanceName(getName());
        root.addChild(topLevel);
    }

    @Override
    public void initialize(InstanceInitializationFactory initializer, String instanceId) {
    }

    @Override
    public void readExternal(DataInputStream in, PrototypeFactory pf)
        throws IOException, DeserializationException {
        super.readExternal(in, pf);
        path = ExtUtil.readString(in);
        index = ExtUtil.readInt(in);
        try {
            setRoot(parseInternalInstance(path, index));
        } catch (InvalidReferenceException | InvalidStructureException | XmlPullParserException | UnfullfilledRequirementsException e) {
            throw new DeserializationException("Unable to parse external instance: " + e);
        }
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        super.writeExternal(out);
        ExtUtil.write(out, path);
        ExtUtil.write(out, index);
    }

    public static TreeElement parse(String instanceId, String path) throws IOException, InvalidStructureException, XmlPullParserException, UnfullfilledRequirementsException {
        InputStream inputStream = new FileInputStream(path);
        KXmlParser xmlParser = ElementParser.instantiateParser(inputStream);
        TreeElementParser treeElementParser = new TreeElementParser(xmlParser, 0, instanceId);
        return treeElementParser.parse();
    }



}
