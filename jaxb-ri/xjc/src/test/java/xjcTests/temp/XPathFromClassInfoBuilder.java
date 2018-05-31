package xjcTests.temp;

import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.ComplexTypeImpl;
import com.sun.xml.xsom.impl.ModelGroupImpl;
import com.sun.xml.xsom.impl.ParticleImpl;
import com.sun.xml.xsom.parser.SchemaDocument;

@SuppressWarnings("unused")
public class XPathFromClassInfoBuilder {

	public void build(CClassInfo info) {
		try {
			// topDown(info);
			System.out.println(bottomUp(info));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String bottomUp(CClassInfo info) {
		// com.sun.xml.bind.v2.runtime.Location l = info.getLocation();
		Stack<String> ss = new Stack<>();
		QName qn = info.getElementName();
		String s = info.fullName();
		XSComponent c = info.getSchemaComponent();
		SchemaDocument sd = c.getSourceDocument();
		if (c instanceof ComplexTypeImpl) {
			ss.push("/*[local-name()='complexType']");
			ComplexTypeImpl cti = (ComplexTypeImpl) c;
			XSElementDecl t = cti.getScope(); // parent element
			XSContentType xct = cti.getContentType(); // child sequence
			int l = xct.getLocator().getLineNumber();
			s = xct.toString();
			info.parent();
		}
		return "";
	}

	private void topDown(CClassInfo info) {
		XSSchema s = info.getSchemaComponent().getOwnerSchema();
		Map<String, XSComplexType> cts = s.getComplexTypes();
		handleElements(s.getElementDecls());
		Map<String, XSType> types = s.getTypes();

	}

	private void handleElements(Map<String, XSElementDecl> eles) {
		String schemaXPath = ("/*[local-name()='schema']");
		for (Map.Entry<String, XSElementDecl> ele : eles.entrySet()) {
			String s = ele.getKey();
			XSElementDecl eleDecl = ele.getValue();
			XSType t = eleDecl.getType();
			if (t instanceof XSComplexType) {
				// Nested Type
				buildXPathForComplexType((XSComplexType) t);

			}
			System.out.println("StopHere");
		}
	}

	private String buildXPathForParticle(ParticleImpl pi) {
		StringBuilder builder = new StringBuilder();

		XSTerm t = pi.getTerm();
		if (t instanceof XSElementDecl) {
			builder.append("/*[local-name()='element' and @name='").append(((XSElementDecl) t).getName()).append("']");
			XSType type = ((XSElementDecl) t).getType();
			if (type instanceof XSComplexType) {
				buildXPathForComplexType((XSComplexType) type);
			}
		}

		return builder.toString();
	}

	private String buildXPathForComplexType(XSComplexType ct) {
		XSTerm term = ct.getContentType().asParticle().getTerm();
		if (term instanceof ModelGroupImpl) {
			ModelGroupImpl mgi = (ModelGroupImpl) term;
			String s2 = mgi.getCompositor().toString().toLowerCase();
			for (ParticleImpl pi : mgi.getChildren()) {
				System.out.println(buildXPathForParticle(pi));
			}
		}
		return "";
	}

}
