package com.sun.tools.xjc.reader.xmlschema.ref;

import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import com.sun.xml.xsom.XSWildcard;

public class WildcardRef extends Ref {
	protected final WildcardMode mode;

	public WildcardRef(XSWildcard wildcard) {
		this.mode = getMode(wildcard);
	}

	public WildcardRef(WildcardMode mode) {
		this.mode = mode;
	}

	private static WildcardMode getMode(XSWildcard wildcard) {
		switch (wildcard.getMode()) {
		case XSWildcard.LAX:
			return WildcardMode.LAX;
		case XSWildcard.STRTICT:
			return WildcardMode.STRICT;
		case XSWildcard.SKIP:
			return WildcardMode.SKIP;
		default:
			throw new IllegalStateException();
		}
	}

	public CTypeRef toTypeRef(CElementPropertyInfo ep) {
		// we don't allow a mapping to typeRef if the wildcard is present
		throw new IllegalStateException();
	}

	public void toElementRef(CReferencePropertyInfo prop) {
		prop.setWildcard(mode);
	}

	public RawTypeSet.Mode canBeType(RawTypeSet parent) {
		return RawTypeSet.Mode.MUST_BE_REFERENCE;
	}

	public boolean isListOfValues() {
		return false;
	}

	public ID id() {
		return ID.NONE;
	}
}
