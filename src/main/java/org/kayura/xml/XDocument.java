/**
 * Copyright 2015-2015 the original author or authors.
 * HomePage: http://www.kayura.org
 */
package org.kayura.xml;

import org.kayura.exceptions.KayuraException;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author liangxia@live.com
 * @author Clinton Begin
 */
public class XDocument {

	private Document document;
	private boolean validation;
	private EntityResolver entityResolver;
	private Properties variables;
	private XPath xpath;

	public XDocument() {
		commonConstructor(false, null, null);
		this.document = createDocument(null);
	}

	public XDocument(String xml) {
		commonConstructor(false, null, null);
		this.document = createDocument(new InputSource(xml));
	}

	public XDocument(String xml, boolean validation) {
		commonConstructor(validation, null, null);
		this.document = createDocument(new InputSource(xml));
	}

	public XDocument(String xml, boolean validation, Properties variables) {
		commonConstructor(validation, variables, null);
		this.document = createDocument(new InputSource(xml));
	}

	public XDocument(String xml, boolean validation, Properties variables, EntityResolver entityResolver) {
		commonConstructor(validation, variables, entityResolver);
		this.document = createDocument(new InputSource(xml));
	}

	public XDocument(Reader reader) {
		commonConstructor(false, null, null);
		this.document = createDocument(new InputSource(reader));
	}

	public XDocument(Reader reader, boolean validation) {
		commonConstructor(false, null, null);
		this.document = createDocument(new InputSource(reader));
	}

	public XDocument(Reader reader, boolean validation, Properties variables) {
		commonConstructor(validation, variables, null);
		this.document = createDocument(new InputSource(reader));
	}

	public XDocument(Reader reader, boolean validation, Properties variables, EntityResolver entityResolver) {
		commonConstructor(validation, variables, entityResolver);
		this.document = createDocument(new InputSource(reader));
	}

	public XDocument(Document document) {
		commonConstructor(false, null, null);
		this.document = document;
	}

	public XDocument(Document document, boolean validation) {
		commonConstructor(false, null, null);
		this.document = document;
	}

	public XDocument(Document document, boolean validation, Properties variables) {
		commonConstructor(validation, variables, null);
		this.document = document;
	}

	public XDocument(Document document, boolean validation, Properties variables, EntityResolver entityResolver) {
		commonConstructor(validation, variables, entityResolver);
		this.document = document;
	}

	public XDocument(InputStream inputStream) {
		commonConstructor(false, null, null);
		this.document = createDocument(new InputSource(inputStream));
	}

	public XDocument(InputStream inputStream, boolean validation) {
		commonConstructor(validation, null, null);
		this.document = createDocument(new InputSource(inputStream));
	}

	public XDocument(InputStream inputStream, boolean validation, Properties variables) {
		commonConstructor(validation, variables, null);
		this.document = createDocument(new InputSource(inputStream));
	}

	public String evalString(String expression) {
		return evalString(document, expression);
	}

	public String evalString(Object root, String expression) {
		String result = (String) evaluate(expression, root, XPathConstants.STRING);
		result = PropertyParser.parse(result, variables);
		return result;
	}

	public Boolean evalBoolean(String expression) {
		return evalBoolean(document, expression);
	}

	public Boolean evalBoolean(Object root, String expression) {
		return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
	}

	public Short evalShort(String expression) {
		return evalShort(document, expression);
	}

	public Short evalShort(Object root, String expression) {
		return Short.valueOf(evalString(root, expression));
	}

	public Integer evalInteger(String expression) {
		return evalInteger(document, expression);
	}

	public Integer evalInteger(Object root, String expression) {
		return Integer.valueOf(evalString(root, expression));
	}

	public Long evalLong(String expression) {
		return evalLong(document, expression);
	}

	public Long evalLong(Object root, String expression) {
		return Long.valueOf(evalString(root, expression));
	}

	public Float evalFloat(String expression) {
		return evalFloat(document, expression);
	}

	public Float evalFloat(Object root, String expression) {
		return Float.valueOf(evalString(root, expression));
	}

	public Double evalDouble(String expression) {
		return evalDouble(document, expression);
	}

	public Double evalDouble(Object root, String expression) {
		return (Double) evaluate(expression, root, XPathConstants.NUMBER);
	}

	public List<XNode> evalNodes(Object root, String expression) {
		List<XNode> xnodes = new ArrayList<XNode>();
		NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			xnodes.add(new XNode(this, nodes.item(i), variables));
		}
		return xnodes;
	}

	public List<XNode> evalNodes(String expression) {
		return evalNodes(document, expression);
	}

	public XNode evalNode(String expression) {
		return evalNode(document, expression);
	}

	public XNode evalNode(Object root, String expression) {
		Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
		if (node == null) {
			return null;
		}
		return new XNode(this, node, variables);
	}

	public Element createElement(String tagName) {
		Element element = this.document.createElement(tagName);
		return element;
	}

	public XNode createChildNode(String nodeName) {
		Element element = createElement(nodeName);
		this.document.appendChild(element);
		return new XNode(this, element, variables);
	}

	private Object evaluate(String expression, Object root, QName returnType) {
		try {
			return xpath.evaluate(expression, root, returnType);
		} catch (Exception e) {
			throw new KayuraException("Error evaluating XPath.  Cause: " + e, e);
		}
	}

	private Document createDocument(InputSource inputSource) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validation);
			factory.setNamespaceAware(false);
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(false);
			factory.setCoalescing(false);
			factory.setExpandEntityReferences(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(entityResolver);
			builder.setErrorHandler(new ErrorHandler() {

				public void warning(SAXParseException exception) throws SAXException {
				}

				public void fatalError(SAXParseException exception) throws SAXException {
				}

				public void error(SAXParseException exception) throws SAXException {
				}
			});

			Document doc = null;

			if (inputSource == null) {
				doc = builder.newDocument();
			} else {
				doc = builder.parse(inputSource);
			}

			return doc;
		} catch (Exception e) {
			throw new KayuraException("Error creating document instance.  Cause: " + e, e);
		}
	}

	public String exportXml() {
		return exportXml(null);
	}

	public String exportXml(Properties outputAttrs) {
		StringWriter sw = new StringWriter();
		Transformer t;
		try {
			t = TransformerFactory.newInstance().newTransformer();
			if (outputAttrs != null) {
				t.setOutputProperties(outputAttrs);
			}
			t.transform(new DOMSource(this.document), new StreamResult(sw));
			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error convert xml", e);
		}
	}

	private void commonConstructor(boolean validation, Properties variables, EntityResolver entityResolver) {
		this.validation = validation;
		this.entityResolver = entityResolver;
		this.setVariables(variables);
		XPathFactory factory = XPathFactory.newInstance();
		this.xpath = factory.newXPath();
	}

	public Properties getVariables() {
		return variables;
	}

	public void setVariables(Properties variables) {
		this.variables = variables;
	}
}
