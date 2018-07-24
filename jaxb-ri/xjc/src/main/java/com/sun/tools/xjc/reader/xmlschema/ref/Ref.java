package com.sun.tools.xjc.reader.xmlschema.ref;

import javax.activation.MimeType;

import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.tools.xjc.reader.RawTypeSet.Mode;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.bind.v2.model.core.ID;

public abstract class Ref {
	protected final BGMBuilder builder = Ring.get(BGMBuilder.class);

	/**
	 * @param ep
	 *            the property to which the returned {@link CTypeRef} will be
	 *            added to.
	 */
	public abstract CTypeRef toTypeRef(CElementPropertyInfo ep);

	public abstract void toElementRef(CReferencePropertyInfo prop);

	/**
	 * Can this {@link Ref} be a type ref?
	 * 
	 * @return false to veto.
	 * @param parent
	 */
	public abstract Mode canBeType(RawTypeSet parent);

	public abstract boolean isListOfValues();

	/**
	 * When this {@link RawTypeSet} binds to a {@link CElementPropertyInfo},
	 * this method is used to determine if the property is ID or not.
	 */
	public abstract ID id();

	/**
	 * When this {@link RawTypeSet} binds to a {@link CElementPropertyInfo},
	 * this method is used to determine if the property has an associated
	 * expected MIME type or not.
	 */
	public MimeType getExpectedMimeType() {
		return null;
	}
}
