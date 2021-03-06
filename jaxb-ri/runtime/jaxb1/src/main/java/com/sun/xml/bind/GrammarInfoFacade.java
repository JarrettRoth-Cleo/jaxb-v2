/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
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

package com.sun.xml.bind;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;

/**
 * This class is a facade to a collection of GrammarInfo objects.  It
 * dispatches rootElement requests to the underlying GrammarInfo objects.
 *
 * @since JAXB1.0
 * @deprecated in JAXB1.0.1
 */
class GrammarInfoFacade extends GrammarInfo {

    private GrammarInfo[] grammarInfos = null;
    
    private Grammar bgm = null;
    
    
    public GrammarInfoFacade( GrammarInfo[] items ) throws JAXBException {
        // TODO: is a shallow copy acceptable?
        grammarInfos = items;
        
        detectRootElementCollisions( getProbePoints() );
    }

    /*
     * Gets a generated implementation class for the specified root element.
     * This method is used to determine the first object to be unmarshalled.
     */
    public Class getRootElement(String namespaceUri, String localName) {
        // find the root element among the GrammarInfos
        for( int i = 0; i < grammarInfos.length; i++ ) {
            Class c = grammarInfos[i].getRootElement( namespaceUri, localName );
            if( c != null ) {
                return c;
            }
        }
        
        // the element was not located in any of the grammar infos...
        return null;
    }
    
    /*
     * Return the probe points for this GrammarInfo, which are used to detect 
     * {namespaceURI,localName} collisions across the GrammarInfo's on the
     * schemaPath.  This is a slightly more complex implementation than a simple
     * hashmap, but it is more flexible in supporting additional schema langs.
     */
    public String[] getProbePoints() {
        ArrayList probePointList = new ArrayList();
        
        for( int i = 0; i < grammarInfos.length; i++ ) {
            String[] points = grammarInfos[i].getProbePoints();
            for( int j = 0; j < points.length; j++ ) {
                probePointList.add( points[j] );
            }
        }

        // TODO: cache this array, but this method should only be called
        // once per JAXBContext creation, so it may not be worth it.
        return (String[])probePointList.toArray( new String[ probePointList.size() ] );        
    }
    
       
    /*
     * This static method is used to setup the GrammarInfoFacade.  It 
     * is invoked by the DefaultJAXBContextImpl constructor
     */
    static GrammarInfo createGrammarInfoFacade( String contextPath, 
                                                ClassLoader classLoader ) 
        throws JAXBException {
            
        // array of GrammarInfo objs
        ArrayList gis = new ArrayList();

        StringTokenizer st = new StringTokenizer( contextPath, ":;" );

        while( st.hasMoreTokens() ) {
            String objectFactoryName = st.nextToken() + ".ObjectFactory";
            
            // instantiate all of the specified JAXBContextImpls
            try {
                DefaultJAXBContextImpl c = 
                    (DefaultJAXBContextImpl)Class.forName(
                        objectFactoryName, true, classLoader ).newInstance();
                gis.add( c.getGrammarInfo() );
            } catch( ClassNotFoundException cnfe ) {
                throw new NoClassDefFoundError(cnfe.getMessage());
            } catch( Exception e ) {
                // e.printStackTrace();
                // do nothing - IllegalAccessEx, InstantiationEx, SecurityEx
            }
        }

        if( gis.size()==1 )
            // if there's only one path, no need to use a facade.
            return (GrammarInfo)gis.get(0);
        
        return new GrammarInfoFacade( 
            (GrammarInfo[])(gis.toArray( new GrammarInfo[ gis.size() ] ) ) );
    }

    public Class getDefaultImplementation( Class javaContentInterface ) {
        for( int i=0; i<grammarInfos.length; i++ ) {
            Class c = grammarInfos[i].getDefaultImplementation( javaContentInterface );
            if(c!=null)     return c;
        }
        return null;
    }


    public Grammar getGrammar() throws JAXBException {
        if(bgm==null) {
            Grammar[] grammars = new Grammar[grammarInfos.length];
            
            // load al the grammars individually
            for( int i=0; i<grammarInfos.length; i++ )
                grammars[i] = grammarInfos[i].getGrammar();
            
            // connect them to each other
            for( int i=0; i<grammarInfos.length; i++ )
                if( grammars[i] instanceof GrammarImpl )
                    ((GrammarImpl)grammars[i]).connect(grammars);
            
            // take union of them
            for( int i=0; i<grammarInfos.length; i++ ) {
                Grammar n = grammars[i];
                if( bgm == null )   bgm = n;
                else                bgm = union( bgm, n );
            }
        }
        return bgm;
    }


    /**
     * Computes the union of two grammars.
     */
    private Grammar union( Grammar g1, Grammar g2 ) {
        // either g1.getPool() or g2.getPool() is OK.
        // this is just a metter of performance problem.
        final ExpressionPool pool = g1.getPool();
        final Expression top = pool.createChoice(g1.getTopLevel(),g2.getTopLevel());
        
        return new Grammar() {
            public ExpressionPool getPool() {
                return pool;
            }
            public Expression getTopLevel() {
                return top;
            }
        };
    }
    
    
    /**
     * Iterate through the probe points looking for root element collisions.
     * If a duplicate is detected, then multiple root element componenets
     * exist with the same uri:localname
     */
    private void detectRootElementCollisions( String[] points ) 
        throws JAXBException {
            
        // the array of probe points contain uri:localname pairs
        for( int i = 0; i < points.length; i += 2 ) {
            // iterate over GrammarInfos - if more than one GI returns
            // a class from getRootElement, then there is a collision
            boolean elementFound = false;
            for( int j = grammarInfos.length-1; j >= 0; j -- ) {
                if( grammarInfos[j].getRootElement( points[i], points[i+1] ) != null ) {
                    if( elementFound == false ) {
                        elementFound = true;
                    } else {
                        throw new JAXBException( 
                            Messages.format( Messages.COLLISION_DETECTED,
                                points[i], points[i+1] ) );
                    }
                }
            }
        }
    }
}
