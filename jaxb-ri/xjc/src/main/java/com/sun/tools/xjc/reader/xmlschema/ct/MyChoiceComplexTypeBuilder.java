package com.sun.tools.xjc.reader.xmlschema.ct;

import static com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeBindingMode.FALLBACK_CONTENT;
import static com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeBindingMode.NORMAL;

import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.MyParticleBinder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.impl.ModelGroupImpl;
import com.sun.xml.xsom.impl.ParticleImpl;
import com.sun.xml.xsom.visitor.XSContentTypeVisitor;

//TODO: if this works better than the reaction plugin, make things public and configurable....somehow
public class MyChoiceComplexTypeBuilder extends CTBuilder {

	@Override
	boolean isApplicable(XSComplexType ct) {
		XSContentType propInfo = ct.getContentType();
		if (!(propInfo instanceof ParticleImpl)) {
			return false;
		}
		ParticleImpl comp = (ParticleImpl) propInfo;

		if (!(comp.getTerm() instanceof ModelGroupImpl)) {
			return false;
		}
		ModelGroupImpl term = (ModelGroupImpl) comp.getTerm();

		// True if the particle is an unbounded choice
		return term.getCompositor() == XSModelGroup.CHOICE;
	}

	@Override
	void build(final XSComplexType ct) {
		System.out.println("B-b-b-b-build it real good.");
		XSContentType contentType = ct.getContentType();

		contentType.visit(new XSContentTypeVisitor() {
			@Override
			public void simpleType(XSSimpleType st) {
				builder.recordBindingMode(ct, ComplexTypeBindingMode.NORMAL);

				simpleTypeBuilder.refererStack.push(ct);
				TypeUse use = simpleTypeBuilder.build(st);
				simpleTypeBuilder.refererStack.pop();

				BIProperty prop = BIProperty.getCustomization(ct);
				CPropertyInfo p = prop.createValueProperty("Value", false, ct, use, BGMBuilder.getName(st));
				selector.getCurrentBean().addProperty(p);
			}

			@Override
			public void particle(XSParticle p) {
				// determine the binding of this complex type.

				MyParticleBinder binder = new MyParticleBinder(isUnbounded(ct));
				builder.recordBindingMode(ct, binder.checkFallback(p) ? FALLBACK_CONTENT : NORMAL);

				binder.build(p);

				XSTerm term = p.getTerm();
				if (term.isModelGroup() && term.asModelGroup().getCompositor() == XSModelGroup.ALL)
					selector.getCurrentBean().setOrdered(false);

			}

			@Override
			public void empty(XSContentType e) {
				builder.recordBindingMode(ct, NORMAL);
			}
		});

		// adds attributes and we are through.
		green.attContainer(ct);
	}

	private boolean isUnbounded(XSComplexType ct) {
		XSContentType propInfo = ct.getContentType();
		if (!(propInfo instanceof ParticleImpl)) {
			return false;
		}
		ParticleImpl comp = (ParticleImpl) propInfo;
		return comp.isRepeated();
	}
}
