package com.petrolink.mbe.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * Helper tool to traverse xml configuration.
 * @author aristo
 *
 */
public final class XmlConfigUtil {

	/**
	 * Try to load text from attribute or if not found, from Child Element
	 * @param e Element to be searched
	 * @param name Name of the attribute or child name
	 * @return Text value of the Attribute/Child element, null when not available
	 */
	public static String getAttributeOrChildText(final Element e, final String name) {
		if(e == null) return null;
		
		String value = null;
		Attribute attr = e.getAttribute(name);
		if (attr != null) {
			value = attr.getValue();
		} else {
			Element child = e.getChild(name, e.getNamespace());
			if (child != null) { 
				value = child.getText();
			}
		}
		return value;
	}
	
	/**
	 * Try to load text from attribute/child element, trying from main configuration, then template if not available.
	 * @param mainConfiguration The first element to check
	 * @param template Secondary element to check when can't be found in main configuration
	 * @param name Name of the attribute or child name
	 * @return text from attribute/child element
	 */
	public static String getAttributeOrChildText(final Element mainConfiguration, final Element template, final String name) {
		String text = null;
		if (mainConfiguration != null) {
			text = getAttributeOrChildText(mainConfiguration, name);
		}
		if ((template != null) && StringUtils.isBlank(text)) {
			text = getAttributeOrChildText(template, name);
		} 
		return text;
	}
	
	/**
	 * Try to load child element from attribute/child element, trying from main configuration, then template if not available.
	 * @param mainConfiguration The first element to check
	 * @param template Secondary element to check when can't be found in main configuration
	 * @param name Name of the attribute or child name
	 * @return d child element from attribute/child element
	 */
	public static Element getChildElement(final Element mainConfiguration, final Element template, final String name) {
		Element result = null;
		if (mainConfiguration != null) {
			result = mainConfiguration.getChild(name, mainConfiguration.getNamespace());
		}
		if ((template != null) && (result == null)) {
			result = template.getChild(name, template.getNamespace());
		} 
		return result;
	}
	
	/**
	 * Get Text element for specified children. Equivalent to call getText for every specified element with specified element's namespace.
	 * @param element
	 * @param elementName
	 * @return List of string contained in child, null if element is not specified.
	 */
	public static List<String> getChildrenText(final Element element, final String elementName) {
		return getChildrenText(element, element.getNamespace(), elementName);
	}
	

	/**
	 * Get Text element for specified children. Equivalent to call getText for every specified element.
	 * @param parentElement
	 * @param elementName
	 * @param ns
	 * @return List of string contained in child, null if element is not specified.
	 */
	public static List<String> getChildrenText(final Element parentElement, final Namespace ns, final String elementName) {
		if (parentElement == null) return null;
		ArrayList<String> childrenText = new ArrayList<String>();
		List<Element> elems = parentElement.getChildren(elementName, ns );
		
		for (Element elem : elems) {
			String text = elem.getText();
			if (StringUtils.isNotBlank(text)) {
				childrenText.add(text);
			}
		}
		return childrenText;
	}
}
