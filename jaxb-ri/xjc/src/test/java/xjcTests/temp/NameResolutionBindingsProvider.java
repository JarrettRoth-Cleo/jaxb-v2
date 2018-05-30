package xjcTests.temp;

import java.util.Map;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.xml.xsom.impl.ComplexTypeImpl;

/**
 * Hopefully provide a container for a change that needs to be made during a
 * retry for name resolution
 *
 */
public class NameResolutionBindingsProvider {

	public LineBindingsProvider buildResolutions(String newShortName, CClassInfo bean) {
		// TODO: how to handle other cases than just nested types?
		int element = getElementLineNumber(bean) - 1;
		int complexType = getComplexTypeElementLineNumber(bean) - 1;
		return new AnnClassNameResolutionContainer(element, complexType, newShortName);
	}

	private int getElementLineNumber(CClassInfo info) {
		ComplexTypeImpl c = (ComplexTypeImpl) info.getSchemaComponent();
		return c.getScope().getLocator().getLineNumber();
	}

	private int getComplexTypeElementLineNumber(CClassInfo info) {
		return info.getSchemaComponent().getLocator().getLineNumber();
	}

	private class AnnClassNameResolutionContainer implements LineBindingsProvider {

		int eleLineNum, complexTypeLineNum;
		String fullName;

		// TODO: this should be shortname
		public AnnClassNameResolutionContainer(int eleLineNum, int complexTypeLieNum, String fullName) {
			this.eleLineNum = eleLineNum;
			this.complexTypeLineNum = complexTypeLieNum;
			this.fullName = fullName;
		}

		@Override
		public void addBindings(Map<Integer, String> m) {
			m.put(eleLineNum, buildElementBinding(fullName));
			m.put(complexTypeLineNum, buildComplexTypeBinding(fullName));
		}

		// TODO: use actual XML builder
		private String buildElementBinding(String fullName) {
			StringBuilder builder = new StringBuilder();
			builder.append("<xs:annotation xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\">");
			builder.append("<xs:appinfo>");
			builder.append("<jxb:propery name=\"").append(fullName).append("\" />");
			builder.append("</xs:appinfo>");
			builder.append("</xs:annotation>");
			return builder.toString();
		}

		private String buildComplexTypeBinding(String fullName) {
			StringBuilder builder = new StringBuilder();
			builder.append("<xs:annotation xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\">");
			builder.append("<xs:appinfo>");
			builder.append("<jxb:class name=\"").append(fullName).append("\" />");
			builder.append("<jxb:property name=\"").append(fullName).append("\" />");
			builder.append("</xs:appinfo>");
			builder.append("</xs:annotation>");
			return builder.toString();
		}
	}
}
