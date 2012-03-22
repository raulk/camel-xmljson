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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.camel.Exchange;
import org.apache.camel.component.xmljson.XmlJsonDataFormatConfig.NamespacesPerElementMapping;
import org.apache.camel.spi.DataFormat;

/**
 * A <a href="http://camel.apache.org/data-format.html">data format</a> ({@link DataFormat})
 * using <a href="http://json-lib.sourceforge.net/">json-lib</a> to convert between XML and JSON directly.
 * @author Raul Kripalani
 */
public class XmlJsonDataFormat implements DataFormat {

    private final XMLSerializer serializer;
    private XmlJsonDataFormatConfig config;

	public XmlJsonDataFormat() {
    	serializer = initSerializer();
    }
    
	public XmlJsonDataFormat(XmlJsonDataFormatConfig config) {
		this.config = config;
		this.serializer = initSerializer();
	}
	
	public XMLSerializer initSerializer() {
		XMLSerializer serializer = new XMLSerializer();
		
		if (config == null)
			return serializer;
		
		if (config.getForceTopLevelObject() != null) {
			serializer.setForceTopLevelObject(config.getForceTopLevelObject());
		}
		
		if (config.getNamespaceLenient() != null) {
			serializer.setNamespaceLenient(config.getNamespaceLenient());
		}
		
		if (config.getNamespaceMappings() != null) {
			for (NamespacesPerElementMapping nsMapping : config.getNamespaceMappings()) {
				for (Entry<String, String> entry : nsMapping.namespaces.entrySet()) {
					// prefix, URI, elementName (which can be null or empty string, in which case the 
					// mapping is added to the root element
					serializer.addNamespace(entry.getKey(), entry.getValue(), nsMapping.element);
				}
			}
		}
		
		if (config.getRootName() != null) {
			serializer.setRootName(config.getRootName());
		}
		
		if (config.getElementName() != null) {
			serializer.setElementName(config.getElementName());
		}
		
		if (config.getArrayName() != null) {
			serializer.setArrayName(config.getArrayName());
		}
		
		if (config.getExpandableProperties() != null && config.getExpandableProperties().size() != 0) {
			serializer.setExpandableProperties(config.getExpandableProperties().toArray(new String[config.getExpandableProperties().size()]));
		}
		
		if (config.getSkipWhitespace() != null) {
			serializer.setSkipWhitespace(config.getSkipWhitespace());
		}
		
		if (config.getTrimSpaces() != null) {
			serializer.setTrimSpaces(config.getTrimSpaces());
		}
		
		if (config.getSkipNamespaces() != null) {
			serializer.setSkipNamespaces(config.getSkipNamespaces());
		}
		
		if (config.getRemoveNamespacePrefixes() != null) {
			serializer.setRemoveNamespacePrefixFromElements(config.getRemoveNamespacePrefixes());
		}
		
		if (config.getTypeHints() == TypeHintsEnum.YES || config.getTypeHints() == TypeHintsEnum.WITH_PREFIX) {
			serializer.setTypeHintsEnabled(true);
			if (config.getTypeHints() == TypeHintsEnum.WITH_PREFIX) {
				serializer.setTypeHintsCompatibility(true);
			}
		} else {
			serializer.setTypeHintsEnabled(false);
		}
		
		return serializer;
	}
	
	/**
	 * Marshal from XML to JSON
	 * @param exchange
	 * @param graph
	 * @param stream
	 * @throws Exception
	 */
	@Override
	public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
		boolean streamTreatment = true;
		// try to process as an InputStream if it's not a String
		Object xml = graph instanceof String ? null : exchange.getContext().getTypeConverter().convertTo(InputStream.class, graph);
		// if conversion to InputStream was unfeasible, fall back to String
		if (xml == null) {
			xml = exchange.getContext().getTypeConverter().mandatoryConvertTo(String.class, graph);
			streamTreatment = false;
		}
		
		JSON json;
		// perform the marshaling to JSON
		if (streamTreatment) {
			json = serializer.readFromStream((InputStream) xml);
		} else {
			json = serializer.read((String) xml);
		}
		
		OutputStreamWriter osw = new OutputStreamWriter(stream);
		json.write(osw);
		osw.flush();
		
	}
	
	/**
	 * Convert from JSON to XML
	 * @param exchange
	 * @param stream
	 * @throws Exception
	 */
	@Override
	public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
		Object inBody = exchange.getIn().getBody();
		JSON toConvert;
		// if the incoming object is already a JSON object, process as-is, otherwise parse it as a String
		if (inBody instanceof JSON) {
			toConvert = (JSON) inBody;
		} else {
			String jsonString = exchange.getContext().getTypeConverter().convertTo(String.class, inBody);
			toConvert = JSONSerializer.toJSON(jsonString);
		}
		
		return convertToXMLUsingEncoding(toConvert);
	}
	
	private String convertToXMLUsingEncoding(JSON json) {
		if (config == null || config.getEncoding() == null) {
			return serializer.write(json);
		} else {
			return serializer.write(json, config.getEncoding());
		}
	}
	
	public XMLSerializer getSerializer() {
		return serializer;
	}

}
