package com.sun.tools.xjc.reader.xmlschema;

import java.math.BigInteger;

import org.xml.sax.Locator;

import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.impl.AnnotationImpl;
import com.sun.xml.xsom.impl.ComponentImpl;
import com.sun.xml.xsom.impl.ContentTypeImpl;
import com.sun.xml.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;

public class MyOverridePartical2 extends ComponentImpl implements XSParticle, ContentTypeImpl {

	private XSParticle w;
	private boolean unbounded;

	public MyOverridePartical2(XSParticle wrapped, boolean unbounded) {
		// WOW
		this((SchemaDocumentImpl) ((ComponentImpl) wrapped).getOwnerSchema().getSourceDocument(), (AnnotationImpl) wrapped.getAnnotation(),
				wrapped.getLocator(), ((ComponentImpl) wrapped).getForeignAttributes().get(0));
		this.w = wrapped;
		this.unbounded = unbounded;
	}

	protected MyOverridePartical2(SchemaDocumentImpl _owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl fa) {
		super(_owner, _annon, _loc, fa);
	}

	@Override
	public XSSimpleType asSimpleType() {
		return w.asSimpleType();
	}

	@Override
	public XSParticle asParticle() {
		// TODO Auto-generated method stub
		return w.asParticle();
	}

	@Override
	public XSContentType asEmpty() {
		// TODO Auto-generated method stub
		return w.asEmpty();
	}

	@Override
	public <T> T apply(XSContentTypeFunction<T> function) {
		// TODO Auto-generated method stub
		return w.apply(function);
	}

	@Override
	public void visit(XSContentTypeVisitor visitor) {
		// TODO Auto-generated method stub
		w.visit(visitor);
	}

	@Override
	public void visit(XSVisitor visitor) {
		// TODO Auto-generated method stub
		w.visit(visitor);
	}

	@Override
	public <T> T apply(XSFunction<T> function) {
		// TODO Auto-generated method stub
		return w.apply(function);
	}

	@Override
	public XSContentType getContentType() {
		// TODO Auto-generated method stub
		return w;
	}

	@Override
	public BigInteger getMinOccurs() {
		// TODO Auto-generated method stub
		return w.getMinOccurs();
	}

	@Override
	public BigInteger getMaxOccurs() {
		// TODO Auto-generated method stub
		return unbounded ? BigInteger.valueOf(UNBOUNDED) : w.getMaxOccurs();
	}

	@Override
	public boolean isRepeated() {
		// TODO Auto-generated method stub
		return unbounded;
	}

	@Override
	public XSTerm getTerm() {
		// TODO Auto-generated method stub
		return w.getTerm();
	}

}
