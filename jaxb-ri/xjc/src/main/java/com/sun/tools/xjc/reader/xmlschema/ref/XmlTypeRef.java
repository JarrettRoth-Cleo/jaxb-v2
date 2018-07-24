package com.sun.tools.xjc.reader.xmlschema.ref;

import javax.activation.MimeType;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.ClassSelector;
import com.sun.tools.xjc.reader.xmlschema.SimpleTypeBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.xsom.XSElementDecl;

public class XmlTypeRef extends Ref {
	protected final XSElementDecl decl;
	protected final TypeUse target;

	public XmlTypeRef(XSElementDecl decl) {
		this.decl = decl;
		SimpleTypeBuilder stb = Ring.get(SimpleTypeBuilder.class);
		stb.refererStack.push(decl);
		TypeUse r = Ring.get(ClassSelector.class).bindToType(decl.getType(), decl);
		stb.refererStack.pop();
		target = r;
	}

	public CTypeRef toTypeRef(CElementPropertyInfo ep) {
		if (ep != null && target.getAdapterUse() != null)
			ep.setAdapter(target.getAdapterUse());
		return new CTypeRef(target.getInfo(), decl);
	}

	/**
	 * The whole type set can be later bound to a reference property, in which
	 * case we need to generate additional code to wrap this type reference into
	 * an element class.
	 *
	 * This method generates such an element class and returns it.
	 */
	public void toElementRef(CReferencePropertyInfo prop) {
		CClassInfo scope = Ring.get(ClassSelector.class).getCurrentBean();
		Model model = Ring.get(Model.class);

		CCustomizations custs = Ring.get(BGMBuilder.class).getBindInfo(decl).toCustomizationList();

		if (target instanceof CClassInfo && Ring.get(BIGlobalBinding.class).isSimpleMode()) {
			CClassInfo bean = new CClassInfo(model, scope, model.getNameConverter().toClassName(decl.getName()), decl.getLocator(), null,
					BGMBuilder.getName(decl), decl, custs);
			bean.setBaseClass((CClassInfo) target);
			prop.getElements().add(bean);
		} else {
			CElementInfo e = new CElementInfo(model, BGMBuilder.getName(decl), scope, target, decl.getDefaultValue(), decl, custs, decl.getLocator());
			prop.getElements().add(e);
		}
	}

	public RawTypeSet.Mode canBeType(RawTypeSet parent) {
		// if we have an adapter or IDness, which requires special
		// annotation, and there's more than one element,
		// we have no place to put the special annotation, so we need
		// JAXBElement.
		if ((parent.refs.size() > 1 || !parent.mul.isAtMostOnce()) && target.idUse() != ID.NONE)
			return RawTypeSet.Mode.MUST_BE_REFERENCE;
		if (parent.refs.size() > 1 && target.getAdapterUse() != null)
			return RawTypeSet.Mode.MUST_BE_REFERENCE;

		// nillable and optional at the same time. needs an element wrapper
		// to distinguish those
		// two states. But this is not a hard requirement.
		if (decl.isNillable() && parent.mul.isOptional())
			return RawTypeSet.Mode.CAN_BE_TYPEREF;

		return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
	}

	public boolean isListOfValues() {
		return target.isCollection();
	}

	public ID id() {
		return target.idUse();
	}

	@Override
	public MimeType getExpectedMimeType() {
		return target.getExpectedMimeType();
	}
}