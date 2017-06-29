package com.petrolink.mbe.util;

import org.jdom2.Element;
import org.jdom2.Namespace;

import com.thoughtworks.xstream.io.xml.JDom2Writer;

/**
 * An extension of JDom2Writer that adds some basic namespace support.
 * 
 * A constructor allows a default namespace to be set for all elements
 * 
 * If an object is writing an attribute named "xmlns" this will be properly set as the namespace for that element
 * as setting an attribute named "xmlns" is not allowed with JDOM2.
 * 
 * @author langj
 *
 */
public class NamespaceJDom2Writer extends JDom2Writer {
	private final Namespace defaultNamespace;
	
	/**
	 * Initialize the writer with no default namespace.
	 */
	public NamespaceJDom2Writer() {
		this.defaultNamespace = null;
	}
	
	/**
	 * Initialize the writer with a default namespace for all new elements.
	 * @param defaultNamespace The default namespace. Null implies no default namespace.
	 */
	public NamespaceJDom2Writer(Namespace defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}
	
	@Override
	public void addAttribute(String key, String value) {
        if (key.equals("xmlns")) {
        	((Element) getCurrent()).setNamespace(Namespace.getNamespace(value));
        }
        else {
    		super.addAttribute(key, value);
        }
	}
	
	@Override
	protected Object createNode(String name) {
		Object node = super.createNode(name);
		if (defaultNamespace != null && node instanceof Element) {
			Element e = (Element) node;
			e.setNamespace(defaultNamespace);
		}
		return node;
	}
}
