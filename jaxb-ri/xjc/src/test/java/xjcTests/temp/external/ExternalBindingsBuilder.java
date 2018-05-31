package xjcTests.temp.external;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.xml.sax.InputSource;

import xjcTests.temp.Dom4JElementLoader;
import xjcTests.temp.Dom4JElementLoader.DomLoadingException;
import xjcTests.temp.LineBindingsProvider;
import xjcTests.temp.NameBindingsManager;

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
		// TODO: pass this in somehow
		Dom4JElementLoader loader = null;
		try {
			loader = new Dom4JElementLoader(new File("C:/temp/xjc/bindingsTesting/simplifiedPrecision.xsd"));
		} catch (DomLoadingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (LineBindingsProvider prov : manager.getBindingsForSystemId(systemId)) {
			prov.addBindings(doc.getRootElement(), jaxbNs, xsdNS, loader);
		}
		System.out.println(doc.asXML());

		return null;
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

}
