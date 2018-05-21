package org.javarosa.model.xform;
/*
 * Copyright (C) 2009 JavaRosa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IAnswerDataSerializer;
import org.javarosa.core.model.IDataReference;
import org.javarosa.core.model.instance.FormInstance;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.model.utils.IInstanceSerializingVisitor;
import org.javarosa.core.services.transport.payload.ByteArrayPayload;
import org.javarosa.core.services.transport.payload.IDataPayload;
import org.javarosa.xform.util.XFormAnswerDataSerializer;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.IOException;

/**
 * A modified version of Clayton's XFormSerializingVisitor that constructs
 * SMS's.
 *
 * @author Munaf Sheikh, Cell-Life
 */
public class SMSSerializingVisitor implements IInstanceSerializingVisitor {

    private String theSmsStr = null; // sms string to be returned
    private String nodeSet = null; // which nodeset the sms contents are in
    private String xmlns = null;
    private String delimiter = null;
    private String prefix = null;

    /**
     * The serializer to be used in constructing XML for AnswerData elements
     */
    IAnswerDataSerializer serializer;

    /**
     * The schema to be used to serialize answer data
     */
    FormDef schema; // not used

    private void init() {
        theSmsStr = null;
        schema = null;
        theSmsStr = "";
    }

    public byte[] serializeInstance(FormInstance model, FormDef formDef) throws IOException {
        init();
        this.schema = formDef;
        return serializeInstance(model);
    }

    /*
     * (non-Javadoc)
     * @see org.javarosa.core.model.utils.IInstanceSerializingVisitor#serializeInstance(org.javarosa.core.model.instance.FormInstance)
     */
    public byte[] serializeInstance(FormInstance model) throws IOException {
        return this.serializeInstance(model, new XPathReference("/"));
    }

    /*
     * (non-Javadoc)
     * @see org.javarosa.core.model.utils.IInstanceSerializingVisitor#serializeInstance(org.javarosa.core.model.instance.FormInstance, org.javarosa.core.model.IDataReference)
     */
    public byte[] serializeInstance(FormInstance model, IDataReference ref) throws IOException {
        init();

        if (this.serializer == null) {
            this.setAnswerDataSerializer(new XFormAnswerDataSerializer());
        }
        model.accept(this);
        if (theSmsStr != null) {
            //Encode in UTF-16 by default, since it's the default for complex messages
            return theSmsStr.getBytes("UTF-16BE");
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.javarosa.core.model.utils.IInstanceSerializingVisitor#createSerializedPayload(org.javarosa.core.model.instance.FormInstance)
     */
    public IDataPayload createSerializedPayload(FormInstance model) throws IOException {
        return createSerializedPayload(model, new XPathReference("/"));
    }

    public IDataPayload createSerializedPayload(FormInstance model, IDataReference ref)
        throws IOException {
        init();

        if (this.serializer == null) {
            this.setAnswerDataSerializer(new XFormAnswerDataSerializer());
        }
        model.accept(this);
        if (theSmsStr != null) {
            byte[] form = theSmsStr.getBytes("UTF-8");
            return new ByteArrayPayload(form, null, IDataPayload.PAYLOAD_TYPE_SMS);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javarosa.core.model.utils.ITreeVisitor#visit(org.javarosa.core.model
     * .DataModelTree)
     */
    public void visit(FormInstance tree) {
        nodeSet = new String();

        TreeElement root = tree.getRoot();

        xmlns = root.getAttributeValue("", "xmlns");
        delimiter = root.getAttributeValue("", "delimiter");
        if (delimiter == null) {
            // for the spelling-impaired...
            delimiter = root.getAttributeValue("", "delimeter");
        }
        prefix = root.getAttributeValue("", "prefix");

        xmlns = (xmlns != null) ? xmlns : " ";
        delimiter = (delimiter != null) ? delimiter : " ";
        prefix = (prefix != null) ? prefix : " ";

        theSmsStr = prefix.concat(delimiter);

        // serialize each node (and it's children) to get it's answers
        serializeTree(root);
        theSmsStr = theSmsStr.trim();
    }

    public void serializeTree(TreeElement root) {
        for (int j = 0; j < root.getNumChildren(); j++) {
            TreeElement treeElement = root.getChildAt(j);
            if (treeElement.isLeaf() && treeElement.getAttribute("", "tag") != null) {
                String result = serializeNode(treeElement);
                if (result != null) {
                    theSmsStr += result;
                }
            } else {
                serializeTree(treeElement);
            }
        }
    }

    public String serializeNode(TreeElement instanceNode) {
        StringBuilder stringBuilder = new StringBuilder();
        // don't serialize template nodes or non-relevant nodes
        if (!instanceNode.isRelevant()
            || instanceNode.getMult() == TreeReference.INDEX_TEMPLATE) {
            return null;
        }

        if (instanceNode.getValue() != null) {
            Object serializedAnswer = serializer.serializeAnswerData(
                instanceNode.getValue(), instanceNode.getDataType());

            if (serializedAnswer instanceof Element) {
                // DON"T handle this.
                throw new RuntimeException("Can't handle serialized output for "
                    + instanceNode.getValue().toString() + ", "
                    + serializedAnswer);
            } else if (serializedAnswer instanceof String) {
                Element element = new Element();
                element.addChild(Node.TEXT, serializedAnswer);

                String tag = instanceNode.getAttributeValue("", "tag");

                //checks to see if someone included a + inside their tag.
                if (tag.substring(0, 1) == "+") {
                    stringBuilder.append(tag);
                } else {
                    stringBuilder.append("+").append(tag);
                }

                stringBuilder.append(delimiter);

                for (int k = 0; k < element.getChildCount(); k++) {
                    stringBuilder.append(element.getChild(k).toString().replace("\\", "\\\\"));
                    stringBuilder.append(delimiter);
                }
                stringBuilder.append(delimiter);
            } else {
                throw new RuntimeException("Can't handle serialized output for "
                    + instanceNode.getValue().toString() + ", "
                    + serializedAnswer);
            }
        }
        return stringBuilder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.javarosa.core.model.utils.IInstanceSerializingVisitor#
     * setAnswerDataSerializer(org.javarosa.core.model.IAnswerDataSerializer)
     */
    public void setAnswerDataSerializer(IAnswerDataSerializer ads) {
        this.serializer = ads;
    }

    public IInstanceSerializingVisitor newInstance() {
        XFormSerializingVisitor modelSerializer = new XFormSerializingVisitor();
        modelSerializer.setAnswerDataSerializer(this.serializer);
        return modelSerializer;
    }
}