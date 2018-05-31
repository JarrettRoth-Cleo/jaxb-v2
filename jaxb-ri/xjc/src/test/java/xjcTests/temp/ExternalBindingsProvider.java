package xjcTests.temp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import com.sun.tools.xjc.model.CClassInfo;

/**
 * Define an external bindings definition for a particular name issue
 *
 */
public class ExternalBindingsProvider {
	File f;
	CClassInfo info;

	private final Namespace jaxbNs = new Namespace("jaxb", "http://java.sun.com/xml/ns/jaxb");

	public ExternalBindingsProvider(File f, CClassInfo info) {
		this.f = f;
		this.info = info;
	}

	/**
	 * Build a bindings file for the fix. Mocks the bindings FileBuilder from
	 * Clarify.
	 * 
	 * @return
	 * @throws IOException
	 */
	public InputSource build() throws IOException {
		Document rootDoc = buildRootDoc();

		return buildDocInputSource(rootDoc);
	}

	private Document buildRootDoc() {
		Document doc = DocumentHelper.createDocument();

		// TODO: Share this QName with the resolvers.
		QName jaxbQName = new QName("bindings", jaxbNs);

		Element rootBindingsElement = doc.addElement(jaxbQName);
		rootBindingsElement.addNamespace("xs", "http://www.w3.org/2001/XMLSchema");
		rootBindingsElement.addAttribute("version", "2.1");
		return doc;
	}

	private InputSource buildDocInputSource(Document doc) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// XMLWriter does not implement AutoCloseable
		XMLWriter xmlWriter = null;
		try {
			xmlWriter = new XMLWriter(outputStream);
			xmlWriter.write(doc);
		} finally {
			if (xmlWriter != null) {
				xmlWriter.close();
			}
		}

		InputSource source = new InputSource(new ByteArrayInputStream(outputStream.toByteArray()));
		// source.setSystemId(builder.getXsdFile().toURI().toString() +
		// "_bindings");
		source.setSystemId("HALLABALOO");

		// System.out.println("doc: " + doc.asXML());

		return source;
	}
}
