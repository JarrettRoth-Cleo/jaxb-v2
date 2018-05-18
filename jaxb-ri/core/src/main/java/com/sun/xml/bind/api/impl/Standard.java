package com.sun.xml.bind.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.lang.model.SourceVersion;

public class Standard extends NameUtil implements NameConverter {
	@Override
	public String toClassName(String s) {
		return toMixedCaseName(toWordList(s), true);
	}

	@Override
	public String toVariableName(String s) {
		return toMixedCaseName(toWordList(s), false);
	}

	@Override
	public String toInterfaceName(String token) {
		return toClassName(token);
	}

	@Override
	public String toPropertyName(String s) {
		String prop = toClassName(s);
		// property name "Class" with collide with Object.getClass,
		// so escape this.
		if (prop.equals("Class"))
			prop = "Clazz";
		return prop;
	}

	@Override
	public String toConstantName(String token) {
		return super.toConstantName(token);
	}

	/**
	 * Computes a Java package name from a namespace URI, as specified in the
	 * spec.
	 *
	 * @return null if it fails to derive a package name.
	 */
	@Override
	public String toPackageName(String nsUri) {
		// remove scheme and :, if present
		// spec only requires us to remove 'http' and 'urn'...
		int idx = nsUri.indexOf(':');
		String scheme = "";
		if (idx >= 0) {
			scheme = nsUri.substring(0, idx);
			if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("urn"))
				nsUri = nsUri.substring(idx + 1);
		}

		// tokenize string
		ArrayList<String> tokens = tokenize(nsUri, "/: ");
		if (tokens.size() == 0) {
			return null;
		}

		// remove trailing file type, if necessary
		if (tokens.size() > 1) {
			// for uri's like "www.foo.com" and "foo.com", there is no
			// trailing
			// file, so there's no need to look at the last '.' and
			// substring
			// otherwise, we loose the "com" (which would be wrong)
			String lastToken = tokens.get(tokens.size() - 1);
			idx = lastToken.lastIndexOf('.');
			if (idx > 0) {
				lastToken = lastToken.substring(0, idx);
				tokens.set(tokens.size() - 1, lastToken);
			}
		}

		// tokenize domain name and reverse. Also remove :port if it
		// exists
		String domain = tokens.get(0);
		idx = domain.indexOf(':');
		if (idx >= 0)
			domain = domain.substring(0, idx);
		ArrayList<String> r = reverse(tokenize(domain, scheme.equals("urn") ? ".-" : "."));
		if (r.get(r.size() - 1).equalsIgnoreCase("www")) {
			// remove leading www
			r.remove(r.size() - 1);
		}

		// replace the domain name with tokenized items
		tokens.addAll(1, r);
		tokens.remove(0);

		// iterate through the tokens and apply xml->java name algorithm
		for (int i = 0; i < tokens.size(); i++) {

			// get the token and remove illegal chars
			String token = tokens.get(i);
			token = removeIllegalIdentifierChars(token);

			// this will check for reserved keywords
			if (SourceVersion.isKeyword(token.toLowerCase())) {
				token = '_' + token;
			}

			tokens.set(i, token.toLowerCase());
		}

		// concat all the pieces and return it
		return combine(tokens, '.');
	}

	protected String removeIllegalIdentifierChars(String token) {
		// max expected length
		StringBuilder newToken = new StringBuilder(token.length() + 1);
		for (int i = 0; i < token.length(); i++) {
			char c = token.charAt(i);
			// c can't be the first char
			if (i == 0 && !Character.isJavaIdentifierStart(c)) {
				newToken.append('_');
			}
			if (!Character.isJavaIdentifierPart(c)) { // c can't be used
				newToken.append('_');
			} else {
				newToken.append(c); // c is valid
			}
		}
		return newToken.toString();
	}

	protected ArrayList<String> tokenize(String str, String sep) {
		StringTokenizer tokens = new StringTokenizer(str, sep);
		ArrayList<String> r = new ArrayList<String>();

		while (tokens.hasMoreTokens())
			r.add(tokens.nextToken());

		return r;
	}

	protected <T> ArrayList<T> reverse(List<T> a) {
		ArrayList<T> r = new ArrayList<T>();

		for (int i = a.size() - 1; i >= 0; i--)
			r.add(a.get(i));

		return r;
	}

	@SuppressWarnings("rawtypes")
	protected String combine(List r, char sep) {
		StringBuilder buf = new StringBuilder(r.get(0).toString());

		for (int i = 1; i < r.size(); i++) {
			buf.append(sep);
			buf.append(r.get(i));
		}

		return buf.toString();
	}
}
