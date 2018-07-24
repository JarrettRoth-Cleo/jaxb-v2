package com.sun.tools.xjc.reader.xmlschema.ref;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.xsom.XSElementDecl;

public class CClassRef extends Ref {
	public final CClass target;
	public final XSElementDecl decl;

	public CClassRef(XSElementDecl decl, CClass target) {
		this.decl = decl;
		this.target = target;
	}

	public CTypeRef toTypeRef(CElementPropertyInfo ep) {
		return new CTypeRef(target, decl);
	}

	public void toElementRef(CReferencePropertyInfo prop) {
		prop.getElements().add(target);
	}

	public RawTypeSet.Mode canBeType(RawTypeSet parent) {
		// if element substitution can occur, no way it can be mapped to a
		// list of types
		if (decl.getSubstitutables().size() > 1)
			return RawTypeSet.Mode.MUST_BE_REFERENCE;

		return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
	}

	public boolean isListOfValues() {
		return false;
	}

	public ID id() {
		return ID.NONE;
	}
}
