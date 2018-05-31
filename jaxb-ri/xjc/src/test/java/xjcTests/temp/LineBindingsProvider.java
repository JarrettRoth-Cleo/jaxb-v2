package xjcTests.temp;

import java.util.Stack;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public abstract class LineBindingsProvider {

	protected QName buildBindingsQName(Namespace jaxbNS) {
		return new QName("bindings", jaxbNS);
	}

	protected String buildFormattedXPath(int lineNum, Dom4JElementLoader loader) {
		Element e = loader.getElement(lineNum);
		Stack<String> parts = new Stack<>();
		do {
			String elementName = e.getName();
			String nameAttribute = e.attributeValue("name");

			String s = "/xs:" + elementName;
			if (nameAttribute != null) {
				s += (" [@name='" + nameAttribute + "']");
			}
			parts.push(s);

		} while ((e = e.getParent()) != null);

		StringBuilder xpathBuilder = new StringBuilder();
		while (!parts.isEmpty()) {
			xpathBuilder.append(parts.pop());
		}
		return xpathBuilder.toString();
	}

	public abstract void addBindings(Element rootElement, Namespace jaxbNS, Namespace xsdNS, Dom4JElementLoader loader);
}
