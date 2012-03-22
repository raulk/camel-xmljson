package org.apache.camel.component.xmljson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlJsonDataFormatConfig {
	
	private String encoding;
	private String elementName;
	private String arrayName;
	private Boolean forceTopLevelObject;
	private Boolean namespaceLenient;
	private List<NamespacesPerElementMapping> namespaceMappings;
	private String rootName;
	private Boolean skipWhitespace;
	private Boolean trimSpaces;
	private Boolean skipNamespaces;
	private Boolean removeNamespacePrefixes;
	private List<String> expandableProperties;
	private TypeHintsEnum typeHints;

	public XmlJsonDataFormatConfig() {
	}
	
	// Properties
    // -------------------------------------------------------------------------

	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public Boolean getForceTopLevelObject() {
		return forceTopLevelObject;
	}
	
	public void setForceTopLevelObject(Boolean forceTopLevelObject) {
		this.forceTopLevelObject = forceTopLevelObject;
	}
	
	public Boolean getNamespaceLenient() {
		return namespaceLenient;
	}
	
	public void setNamespaceLenient(Boolean namespaceLenient) {
		this.namespaceLenient = namespaceLenient;
	}
	
	public List<NamespacesPerElementMapping> getNamespaceMappings() {
		return namespaceMappings;
	}
	
	public void setNamespaceMappings(List<NamespacesPerElementMapping> namespaceMappings) {
		this.namespaceMappings = namespaceMappings;
	}
	
	public String getRootName() {
		return rootName;
	}
	
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}
	
	public Boolean getSkipWhitespace() {
		return skipWhitespace;
	}
	
	public void setSkipWhitespace(Boolean skipWhitespace) {
		this.skipWhitespace = skipWhitespace;
	}
	
	public Boolean getTrimSpaces() {
		return trimSpaces;
	}
	
	public void setTrimSpaces(Boolean trimSpaces) {
		this.trimSpaces = trimSpaces;
	}
	
	public TypeHintsEnum getTypeHints() {
		return typeHints;
	}
	
	public void setTypeHints(String typeHints) {
		this.typeHints = TypeHintsEnum.valueOf(typeHints);
	}

	public void setSkipNamespaces(Boolean skipNamespaces) {
		this.skipNamespaces = skipNamespaces;
	}

	public Boolean getSkipNamespaces() {
		return skipNamespaces;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementName() {
		return elementName;
	}

	public void setArrayName(String arrayName) {
		this.arrayName = arrayName;
	}

	public String getArrayName() {
		return arrayName;
	}

	public void setExpandableProperties(List<String> expandableProperties) {
		this.expandableProperties = expandableProperties;
	}

	public List<String> getExpandableProperties() {
		return expandableProperties;
	}

	public void setRemoveNamespacePrefixes(Boolean removeNamespacePrefixes) {
		this.removeNamespacePrefixes = removeNamespacePrefixes;
	}

	public Boolean getRemoveNamespacePrefixes() {
		return removeNamespacePrefixes;
	}

	public static class NamespacesPerElementMapping {
		public String element;
		public Map<String, String> namespaces;
		
		public NamespacesPerElementMapping(String element, Map<String, String> namespaces) {
			this.element = element;
			this.namespaces = namespaces;
		}
		
		public NamespacesPerElementMapping(String element, String prefix, String uri) {
			this.element = element;
			this.namespaces = new HashMap<String, String>();
			this.namespaces.put(prefix, uri);
		}
		
		public NamespacesPerElementMapping(String element, String pipeSeparatedMappings) {
			this.element = element;
			this.namespaces = new HashMap<String, String>();
			String[] origTokens = pipeSeparatedMappings.split("\\|");
			// drop the first token
			String[] tokens = Arrays.copyOfRange(origTokens, 1, origTokens.length);
			
			if (tokens.length % 2 != 0) {
				throw new IllegalArgumentException("Even number of prefix-namespace tokens is expected, number of tokens parsed: " + tokens.length);
			}
			int i = 0;
			// |ns1|http://test.org|ns2|http://test2.org|
			while (i < (tokens.length - 1)) {
				this.namespaces.put(tokens[i], tokens[++i]);
				i++;
			}
		}
		
	}
	
}