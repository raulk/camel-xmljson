/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.xmljson;

import java.io.InputStream;
import java.util.Arrays;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;

public class XmlJsonDataFormatTest extends CamelTestSupport {

    @Test
    public void testMarshalAndUnmarshal() throws Exception {
    	InputStream inStream = getClass().getClassLoader().getResourceAsStream("org/apache/camel/component/xmljson/testMessage1.xml");
    	String in = context.getTypeConverter().convertTo(String.class, inStream);
    	
        MockEndpoint mockJSON = getMockEndpoint("mock:json");
        mockJSON.expectedMessageCount(1);
        mockJSON.message(0).body().isInstanceOf(byte[].class);
    	
        MockEndpoint mockXML = getMockEndpoint("mock:xml");
        mockXML.expectedMessageCount(1);
        mockXML.message(0).body().isInstanceOf(String.class);

        Object json = template.requestBody("direct:marshal", in);
        String jsonString = context.getTypeConverter().convertTo(String.class, json);
        JSONObject obj = (JSONObject) JSONSerializer.toJSON(jsonString);
        assertEquals("JSONObject doesn't contain 7 keys", 7, obj.entrySet().size());

        template.sendBody("direct:unmarshal", jsonString);

        mockJSON.assertIsSatisfied();
        mockXML.assertIsSatisfied();
    }
    
    @Test
    public void testUnmarshalJSONObject() throws Exception {
    	InputStream inStream = getClass().getClassLoader().getResourceAsStream("org/apache/camel/component/xmljson/testMessage1.json");
    	String in = context.getTypeConverter().convertTo(String.class, inStream);
    	JSON json = JSONSerializer.toJSON(in);
    	
        MockEndpoint mockXML = getMockEndpoint("mock:xml");
        mockXML.expectedMessageCount(1);
        mockXML.message(0).body().isInstanceOf(String.class);

        Object marshalled = template.requestBody("direct:unmarshal", json);
        Document document = context.getTypeConverter().convertTo(Document.class, marshalled);
        assertEquals("The XML document has an unexpected root node", "o", document.getDocumentElement().getLocalName());
        
        mockXML.assertIsSatisfied();
    }
    
    @Test
    public void testMarshalXMLSources() throws Exception {
    	InputStream inStream = getClass().getClassLoader().getResourceAsStream("org/apache/camel/component/xmljson/testMessage1.xml");
    	DOMSource inDOM = context.getTypeConverter().convertTo(DOMSource.class, inStream);
    	inStream = getClass().getClassLoader().getResourceAsStream("org/apache/camel/component/xmljson/testMessage1.xml");
    	SAXSource inSAX = context.getTypeConverter().convertTo(SAXSource.class, inStream);
    	inStream = getClass().getClassLoader().getResourceAsStream("org/apache/camel/component/xmljson/testMessage1.xml");
    	Document inDocument = context.getTypeConverter().convertTo(Document.class, inStream);
    	
    	// save the expected body of the message to set it later
        Object expectedBody = template.requestBody("direct:marshal", inDOM);

        MockEndpoint mockJSON = getMockEndpoint("mock:json");
        // reset the mock endpoint to get rid of the previous message
        mockJSON.reset();
    	// all three messages should arrive, should be of type byte[] and identical to one another
        mockJSON.expectedMessageCount(3);
        mockJSON.allMessages().body().isInstanceOf(byte[].class);
        mockJSON.expectedBodiesReceived(Arrays.asList(expectedBody, expectedBody, expectedBody));
        
        // start bombarding the route
        Object json = template.requestBody("direct:marshal", inDOM);
        String jsonString = context.getTypeConverter().convertTo(String.class, json);
        JSONObject obj = (JSONObject) JSONSerializer.toJSON(jsonString);
        assertEquals("JSONObject doesn't contain 7 keys", 7, obj.entrySet().size());
        template.requestBody("direct:marshal", inSAX);
        template.requestBody("direct:marshal", inDocument);

        mockJSON.assertIsSatisfied();
    }
    
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                XmlJsonDataFormat format = new XmlJsonDataFormat();
                
                // from XML to JSON
                from("direct:marshal").marshal(format).to("mock:json");
                // from JSON to XML
                from("direct:unmarshal").unmarshal(format).to("mock:xml");
                
            }
        };
    }

}
