package xjcTests.temp.autoNameResolution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;

/**
 * Simple Dom4j loader that will generate a map of XPath for xsd elements based
 * on their line number (0 based)
 * 
 * Note: line numbers are based on the closing bracket '>' of the element
 * 
 * Source:
 * https://stackoverflow.com/questions/3405702/using-sax-to-parse-common-xml-
 * elements
 */
public class Dom4JElementLoader {

	private final File f;
	private final Map<Integer, Element> elements;

	public Dom4JElementLoader(File f) throws DomLoadingException {
		this.f = f;
		this.elements = new HashMap<>();

		load();
	}

	public String getPath(Integer i) {
		Element e = elements.get(i);

		return e == null ? "" : e.getPath();
	}

	public Element getElement(Integer i) {
		return elements.get(i);
	}

	private void load() throws DomLoadingException {
		SAXReader reader = new MySAXReader();
		reader.setDocumentFactory(new LocatorAwareDocumentFactory());

		Document doc;
		try {
			doc = reader.read(f);
		} catch (DocumentException e) {
			throw new DomLoadingException(e);
		}
		doc.accept(new VisitorSupport() {
			@Override
			public void visit(Element node) {
				elements.put(((LocationAwareElement) node).getLineNumber() - 1, node);
			}
		});

	}

	static class MySAXReader extends SAXReader {

		@Override
		protected SAXContentHandler createContentHandler(XMLReader reader) {
			return new MySAXContentHandler(getDocumentFactory(), getDispatchHandler());
		}

		@Override
		public void setDocumentFactory(DocumentFactory documentFactory) {
			super.setDocumentFactory(documentFactory);
		}

	}

	static class MySAXContentHandler extends SAXContentHandler {

		private Locator locator;

		// this is already in SAXContentHandler, but private
		private DocumentFactory documentFactory;

		public MySAXContentHandler(DocumentFactory documentFactory, ElementHandler elementHandler) {
			super(documentFactory, elementHandler);
			this.documentFactory = documentFactory;
		}

		@Override
		public void setDocumentLocator(Locator documentLocator) {
			super.setDocumentLocator(documentLocator);
			this.locator = documentLocator;
			if (documentFactory instanceof LocatorAwareDocumentFactory) {
				((LocatorAwareDocumentFactory) documentFactory).setLocator(documentLocator);
			}

		}

		public Locator getLocator() {
			return locator;
		}
	}

	static class LocatorAwareDocumentFactory extends DocumentFactory {

		private Locator locator;

		public LocatorAwareDocumentFactory() {
			super();
		}

		public void setLocator(Locator locator) {
			this.locator = locator;
		}

		@Override
		public Element createElement(QName qname) {
			LocationAwareElement element = new LocationAwareElement(qname);
			if (locator != null)
				element.setLineNumber(locator.getLineNumber());
			return element;
		}

	}

	/**
	 * An Element that is aware of it location (line number in) in the source
	 * document
	 */
	static class LocationAwareElement extends DefaultElement {

		private int lineNumber = -1;

		public LocationAwareElement(QName qname) {
			super(qname);
		}

		public LocationAwareElement(QName qname, int attributeCount) {
			super(qname, attributeCount);

		}

		public LocationAwareElement(String name, Namespace namespace) {
			super(name, namespace);

		}

		public LocationAwareElement(String name) {
			super(name);

		}

		public int getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

	}

	public static class DomLoadingException extends Exception {
		public DomLoadingException(Exception e) {
			super(e);
		}
	}
}
