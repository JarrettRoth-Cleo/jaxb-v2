package xjcTests.temp.external;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import xjcTests.temp.LineBindingsProvider;
import xjcTests.temp.NameBindingsManager;
import xjcTests.temp.NameBindingsManager.BindingsContainer;

/**
 * Class that will generate an external bindings document
 *
 */
public class ExternalBindingsBuilder {

	private final NameBindingsManager manager;
	private final Namespace jaxbNs = new Namespace("jaxb", "http://java.sun.com/xml/ns/jaxb");
	private final Namespace xsdNS = new Namespace("xs", "http://www.w3.org/2001/XMLSchema");

	public ExternalBindingsBuilder(NameBindingsManager manager) {
		this.manager = manager;
	}

	public List<InputSource> buildDoc() {
		List<InputSource> bindings = new ArrayList<>();
		for (String sysId : manager.getSystemIds()) {
			bindings.add(buildDoc(sysId));
		}

		return bindings;
	}

	private InputSource buildDoc(String systemId) {
		Document doc = buildRootDoc(systemId);

		BindingsContainer c = manager.getBindingsForSystemId(systemId);
		for (LineBindingsProvider prov : c.getBindings()) {
			prov.addBindings(doc.getRootElement(), jaxbNs, xsdNS, c.getElementLoader());
		}

		return buildDocInputSource(doc, systemId);
	}

	private Document buildRootDoc(String systemId) {
		Document doc = DocumentHelper.createDocument();

		// TODO: Share this QName with the resolvers.
		QName jaxbQName = new QName("bindings", jaxbNs);

		Element rootBindingsElement = doc.addElement(jaxbQName);
		rootBindingsElement.add(xsdNS);
		rootBindingsElement.addAttribute("version", "2.1");
		rootBindingsElement.addAttribute("schemaLocation", systemId);

		return doc;
	}

	private InputSource buildDocInputSource(Document doc, String systemId) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// XMLWriter does not implement AutoCloseable
		// TODO
		XMLWriter xmlWriter = null;
		try {
			xmlWriter = new XMLWriter(outputStream);
			xmlWriter.write(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (xmlWriter != null) {
				try {
					xmlWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		InputSource source = new InputSource(new ByteArrayInputStream(outputStream.toByteArray()));
		source.setSystemId(systemId + "_bindings");
		return source;
	}

}
