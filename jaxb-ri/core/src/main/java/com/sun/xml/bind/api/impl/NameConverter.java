/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.bind.api.impl;

/**
 * Converts aribitrary strings into Java identifiers.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface NameConverter {
	/**
	 * converts a string into an identifier suitable for classes.
	 *
	 * In general, this operation should generate "NamesLikeThis".
	 */
	String toClassName(String token);

	/**
	 * converts a string into an identifier suitable for interfaces.
	 *
	 * In general, this operation should generate "NamesLikeThis". But for
	 * example, it can prepend every interface with 'I'.
	 */
	String toInterfaceName(String token);

	/**
	 * converts a string into an identifier suitable for properties.
	 *
	 * In general, this operation should generate "NamesLikeThis", which will be
	 * used with known prefixes like "get" or "set".
	 */
	String toPropertyName(String token);

	/**
	 * converts a string into an identifier suitable for constants.
	 *
	 * In the standard Java naming convention, this operation should generate
	 * "NAMES_LIKE_THIS".
	 */
	String toConstantName(String token);

	/**
	 * Converts a string into an identifier suitable for variables.
	 *
	 * In general it should generate "namesLikeThis".
	 */
	String toVariableName(String token);

	/**
	 * Converts a namespace URI into a package name. This method should expect
	 * strings like "http://foo.bar.zot/org", "urn:abc:def:ghi" "", or even
	 * "###" (basically anything) and expected to return a package name, liks
	 * "org.acme.foo".
	 *
	 */
	String toPackageName(String namespaceUri);

}
