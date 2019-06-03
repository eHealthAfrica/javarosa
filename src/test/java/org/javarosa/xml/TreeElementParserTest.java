/*
 * Copyright 2018 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.javarosa.xml;

import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.reference.InvalidReferenceException;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.junit.Before;
import org.junit.Test;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import static org.javarosa.test.utils.ResourcePathHelper.r;
import static org.junit.Assert.*;

public class TreeElementParserTest {

    private static Path SECONDARY_INSTANCE_XML;

    @Before
    public void setUp() {
            SECONDARY_INSTANCE_XML = r("secondary-instance.xml");
    }

    @Test
    public void parse_internal_instances() throws IOException, UnfullfilledRequirementsException, XmlPullParserException, InvalidStructureException {

        InputStream inputStream = new FileInputStream(SECONDARY_INSTANCE_XML.toString());
        KXmlParser kXmlParser = ElementParser.instantiateParser(inputStream);
        TreeElementParser treeElementParser = new TreeElementParser(kXmlParser, 0, "");
        List<TreeElement> treeElementList = treeElementParser.parseInternalSecondaryInstances();
        assertEquals(treeElementList.size(), 1);
        TreeElement townsTreeElement = treeElementList.get(0);
        assertEquals(townsTreeElement.getInstanceName(), "towns");
        assertEquals(townsTreeElement.getNumChildren(), 1); //Has only one root node - <towndata z="1">
        assertEquals(townsTreeElement.getChildAt(0).getNumChildren(), 1); //Has only one data - <data_set>
        assertEquals(townsTreeElement.getChildAt(0)
            .getChildAt(0) //<data_set>us_east</data_set>
            .getValue().getDisplayText(), "us_east"); //Text Node - us_east

    }


    @Test
    public void parse_internal_instance() throws IOException, UnfullfilledRequirementsException, XmlPullParserException, InvalidStructureException, InvalidReferenceException {

        InputStream inputStream = new FileInputStream(SECONDARY_INSTANCE_XML.toString());
        KXmlParser kXmlParser = ElementParser.instantiateParser(inputStream);
        TreeElementParser treeElementParser = new TreeElementParser(kXmlParser, 0, "towns");
        TreeElement townsTreeElement = treeElementParser.parseInternalSecondaryInstance();
          assertEquals("towns", townsTreeElement.getInstanceName());
        assertEquals(townsTreeElement.getNumChildren(), 1); //Has only one root node - <towndata z="1">
        assertEquals(townsTreeElement.getChildAt(0).getNumChildren(), 1); //Has only one data - <data_set>
        assertEquals(townsTreeElement.getChildAt(0)
            .getChildAt(0) //<data_set>us_east</data_set>
            .getValue().getDisplayText(), "us_east"); //Text Node - us_east

    }

}
