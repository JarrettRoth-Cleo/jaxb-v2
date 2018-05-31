package xjcTests.temp;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import com.sun.tools.xjc.model.CClassInfo;

/**
 * Hopefully provide a container for a change that needs to be made during a
 * retry for name resolution
 *
 */
public class NameResolutionBindingsProvider {

	public LineBindingsProvider buildResolutions(String newShortName, CClassInfo bean) {
		// TODO: how to handle other cases than just nested types?
		int complexType = getComplexTypeElementLineNumber(bean) - 1;

		new XPathFromClassInfoBuilder().build(bean);
		return new AnnClassNameResolutionContainer(complexType, newShortName);
	}

	private int getComplexTypeElementLineNumber(CClassInfo info) {
		return info.getSchemaComponent().getLocator().getLineNumber();
	}

	private class AnnClassNameResolutionContainer extends LineBindingsProvider {

		int complexTypeLineNum;
		String shortName;

		// TODO: this needs to be the line of the sequence+1
		public AnnClassNameResolutionContainer(int complexTypeLieNum, String shortName) {

			this.complexTypeLineNum = complexTypeLieNum;
			this.shortName = shortName;
		}

		@Override
		public void addBindings(Element rootElement, Namespace jaxbNS, Namespace xsdNS, Dom4JElementLoader loader) {
			Element bindingsEle = rootElement.addElement(buildBindingsQName(jaxbNS));
			bindingsEle.addAttribute("node", buildFormattedXPath(complexTypeLineNum, loader));
			Element classNameEle1 = bindingsEle.addElement(new QName("class", jaxbNS));
			classNameEle1.addAttribute("name", shortName);

			Element propEle = bindingsEle.addElement(new QName("property", jaxbNS));
			propEle.addAttribute("name", shortName);

			Element parentBindingsEle = bindingsEle.addElement(buildBindingsQName(jaxbNS));
			parentBindingsEle.addAttribute("node", "..");
			Element classNameEle2 = parentBindingsEle.addElement(new QName("property", jaxbNS));
			classNameEle2.addAttribute("name", shortName);
		}
	}
}
