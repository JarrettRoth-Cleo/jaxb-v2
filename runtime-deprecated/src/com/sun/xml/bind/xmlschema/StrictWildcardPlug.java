/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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
package com.sun.xml.bind.xmlschema;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.xml.bind.GrammarImpl;

/**
 * Plug that implements the semantics of strict wildcard of XML Schema.
 * 
 * @since JAXB1.0
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class StrictWildcardPlug extends GrammarImpl.Plug {
    
    /**
     * NameClass object that determines what this wildcard should allow.
     */
    protected final NameClass namespaces;
    
    private ExpressionPool pool;
    
    
    public StrictWildcardPlug( NameClass namespaces ) {
        this.namespaces = namespaces;
        this.exp = Expression.nullSet;
    }
    
    /**
     * Look for elements that belong to the wildcard and pick them up.
     */
    public void connect(ExpressionPool pool, Grammar[] others) {
        this.exp = Expression.nullSet;
        this.pool = pool;
        
        Walker walker = new Walker();
        for( int i=0; i<others.length; i++ )
            others[i].getTopLevel().visit(walker);
        
        this.pool = null;
    }
    
    protected void onElementFound( ElementExp elem ) {
        // root element
        if( namespaces.includes(elem.getNameClass()) ) {
            // add this element
            this.exp = pool.createChoice( this.exp, elem );
        }
    }
    
    private class Walker extends ExpressionWalker {
        public void onElement(ElementExp exp) {
            onElementFound(exp);
        }
    }
}