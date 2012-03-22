package org.apache.camel.component.xmljson.converters;

import java.io.IOException;
import java.io.InputStream;

import net.sf.json.JSON;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.converter.IOConverter;

/**
 * Contains necessary type converters to cater for Camel's unconditional conversion of the message body to an InputStream prior to marshaling
 * @author Raul Kripalani
 */
@Converter
public class XmlJsonConverters {

	/**
	 * Converts from an existing JSON object circulating as such to an InputStream, by dumping it to a 
	 * String first and then using camel-core's {@link IOConverter#toInputStream(String, Exchange)}
	 * @param json the JSON object
	 * @return 
	 * @throws IOException 
	 */
	@Converter
	public static InputStream fromJSONtoInputStream(JSON json, Exchange exchange) throws IOException {
		return IOConverter.toInputStream(json.toString(), exchange);
	}
	
}
