package com.sun.tools.xjc.reader.xmlschema.ref;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSWildcard;

public class RefFactory {
	public Ref initCClassRef(XSElementDecl decl, CClass elementBean) {
		return new CClassRef(decl, elementBean);
	}

	public Ref initWildCardRef(WildcardMode skip) {
		return new WildcardRef(skip);
	}

	public Ref initWildCardRef(XSWildcard wc) {
		return new WildcardRef(wc);
	}

	public Ref initCElementInfoRef(XSElementDecl decl, CElementInfo elementBean) {
		return new CElementInfoRef(decl, elementBean);
	}

	public Ref initXmlTypeRef(XSElementDecl decl) {
		return new XmlTypeRef(decl);
	}

	public Ref initCClassInfoRef(CClassInfo info) {
		return new CClassInfoRef(info);
	}

}
