package com.sun.tools.xjc.reader.xmlschema.ref;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.xml.bind.v2.model.core.ID;

public class CClassInfoRef extends Ref {

	protected final CClassInfo ci;

	public CClassInfoRef(CClassInfo ci) {
		this.ci = ci;
		assert ci.isElement();
	}

	public ID id() {
		return ID.NONE;
	}

	public boolean isListOfValues() {
		return false;
	}

	public RawTypeSet.Mode canBeType(RawTypeSet parent) {
		return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
	}

	public void toElementRef(CReferencePropertyInfo prop) {
		prop.getElements().add(ci);
	}

	public CTypeRef toTypeRef(CElementPropertyInfo ep) {
		return new CTypeRef(ci, ci.getElementName(), ci.getTypeName(), false, null);
	}

}
