/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.tools.xjc.reader.xmlschema;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.Multiplicity;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDom;
import com.sun.tools.xjc.reader.xmlschema.ref.Ref;
import com.sun.tools.xjc.reader.xmlschema.ref.RefFactory;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.visitor.XSTermVisitor;

/**
 * Builds {@link RawTypeSet} for XML Schema.
 *
 * @author Kohsuke Kawaguchi
 */
public class RawTypeSetBuilder implements XSTermVisitor {
	/**
	 * @param optional
	 *            if this whole property is optional due to the occurrence
	 *            constraints on ancestors, set this to true. this will prevent
	 *            the primitive types to be generated.
	 */
	public static RawTypeSet build(XSParticle p, boolean optional) {
		RawTypeSetBuilder rtsb = new RawTypeSetBuilder();
		rtsb.particle(p);
		Multiplicity mul = MultiplicityCounter.theInstance.particle(p);

		if (optional)
			mul = mul.makeOptional();

		return new RawTypeSet(rtsb.refs, mul);
	}

	/**
	 * To avoid declaring the same element twice for a content model like (A,A),
	 * we keep track of element names here while we are building up this
	 * instance.
	 */
	private final Set<QName> elementNames = new LinkedHashSet<QName>();

	private final Set<Ref> refs = new LinkedHashSet<Ref>();

	private final RefFactory factory = Ring.get(Model.class).options.refFactory;

	private final BGMBuilder builder = Ring.get(BGMBuilder.class);

	public RawTypeSetBuilder() {
	}

	/**
	 * Gets the {@link RawTypeSet.Ref}s that were built.
	 */
	public Set<Ref> getRefs() {
		return refs;
	}

	/**
	 * Build up {@link #refs} and compute the total multiplicity of this
	 * {@link RawTypeSet.Ref} set.
	 */
	private void particle(XSParticle p) {
		// if the DOM customization is present, bind it like a wildcard
		BIDom dom = builder.getLocalDomCustomization(p);
		if (dom != null) {
			dom.markAsAcknowledged();
			refs.add(factory.initWildCardRef(WildcardMode.SKIP));
		} else {
			p.getTerm().visit(this);
		}
	}

	public void wildcard(XSWildcard wc) {
		refs.add(factory.initWildCardRef(wc));
	}

	public void modelGroupDecl(XSModelGroupDecl decl) {
		modelGroup(decl.getModelGroup());
	}

	public void modelGroup(XSModelGroup group) {
		for (XSParticle p : group.getChildren())
			particle(p);
	}

	public void elementDecl(XSElementDecl decl) {

		QName n = BGMBuilder.getName(decl);
		if (elementNames.add(n)) {
			CElement elementBean = Ring.get(ClassSelector.class).bindToType(decl, null);
			if (elementBean == null)
				refs.add(factory.initXmlTypeRef(decl));
			else {
				// yikes!
				if (elementBean instanceof CClass)
					refs.add(factory.initCClassRef(decl, (CClass) elementBean));
				else
					refs.add(factory.initCElementInfoRef(decl, (CElementInfo) elementBean));
			}
		}
	}

}
