package com.sun.tools.xjc.reader.xmlschema.ref;

import javax.activation.MimeType;

import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXSubstitutable;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.xsom.XSElementDecl;

public class CElementInfoRef extends Ref {
	public final CElementInfo target;
	public final XSElementDecl decl;

	public CElementInfoRef(XSElementDecl decl, CElementInfo target) {
		this.decl = decl;
		this.target = target;
	}

	public CTypeRef toTypeRef(CElementPropertyInfo ep) {
		assert !target.isCollection();
		CAdapter a = target.getProperty().getAdapter();
		if (a != null && ep != null)
			ep.setAdapter(a);

		return new CTypeRef(target.getContentType(), decl);
	}

	public void toElementRef(CReferencePropertyInfo prop) {
		prop.getElements().add(target);
	}

	public RawTypeSet.Mode canBeType(RawTypeSet parent) {
		// if element substitution can occur, no way it can be mapped to a
		// list of types
		if (decl.getSubstitutables().size() > 1)
			return RawTypeSet.Mode.MUST_BE_REFERENCE;
		// BIXSubstitutable also simulates this effect. Useful for separate
		// compilation
		BIXSubstitutable subst = builder.getBindInfo(decl).get(BIXSubstitutable.class);
		if (subst != null) {
			subst.markAsAcknowledged();
			return RawTypeSet.Mode.MUST_BE_REFERENCE;
		}

		// we have no place to put an adater if this thing maps to a type
		CElementPropertyInfo p = target.getProperty();
		// if we have an adapter or IDness, which requires special
		// annotation, and there's more than one element,
		// we have no place to put the special annotation, so we need
		// JAXBElement.
		if ((parent.refs.size() > 1 || !parent.mul.isAtMostOnce()) && p.id() != ID.NONE)
			return RawTypeSet.Mode.MUST_BE_REFERENCE;
		if (parent.refs.size() > 1 && p.getAdapter() != null)
			return RawTypeSet.Mode.MUST_BE_REFERENCE;

		if (target.hasClass())
			// if the CElementInfo was explicitly bound to a class (which
			// happen if and only if
			// the user requested so, then map that to reference property so
			// that the user sees a class
			return RawTypeSet.Mode.CAN_BE_TYPEREF;
		else
			return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
	}

	public boolean isListOfValues() {
		return target.getProperty().isValueList();
	}

	public ID id() {
		return target.getProperty().id();
	}

	@Override
	public MimeType getExpectedMimeType() {
		return target.getProperty().getExpectedMimeType();
	}
}