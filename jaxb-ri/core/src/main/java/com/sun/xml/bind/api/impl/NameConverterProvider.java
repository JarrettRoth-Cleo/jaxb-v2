package com.sun.xml.bind.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.lang.model.SourceVersion;

public class NameConverterProvider {

	private static NameConverter standard;
	private static NameConverter jaxrpcCompatible;
	private static NameConverter smart;

	public static void setStandard(NameConverter nc) {
		standard = nc;
	}

	public static void setJaxrpcCompatible(NameConverter nc) {
		jaxrpcCompatible = nc;
	}

	public static void setSmart(NameConverter nc) {
		smart = nc;
	}

	public static NameConverter getStandard() {
		if (standard == null) {
			standard = new Standard();
		}
		return standard;
	}

	/**
	 * JAX-PRC compatible name converter implementation.
	 *
	 * The only difference is that we treat '_' as a valid character and not as
	 * a word separator.
	 */
	public static final NameConverter getJaxrpcCompatible() {
		if (jaxrpcCompatible == null) {
			// TODO: somehow extend the dynamic standard value
			jaxrpcCompatible = new Standard() {
				@Override
				protected boolean isPunct(char c) {
					return (c == '.' || c == '-' || c == ';' /*
																 * || c == '_'
																 */ || c == '\u00b7' || c == '\u0387' || c == '\u06dd' || c == '\u06de');
				}

				@Override
				protected boolean isLetter(char c) {
					return super.isLetter(c) || c == '_';
				}

				@Override
				protected int classify(char c0) {
					if (c0 == '_')
						return NameUtil.OTHER_LETTER;
					return super.classify(c0);
				}
			};
		}
		return jaxrpcCompatible;
	}

	/**
	 * Smarter converter used for RELAX NG support.
	 */
	public static final NameConverter getSmart() {
		if (smart == null) {
			// TODO: somehow extend the dynamic standard value
			smart = new Standard() {
				@Override
				public String toConstantName(String token) {
					String name = super.toConstantName(token);
					if (!SourceVersion.isKeyword(name))
						return name;
					else
						return '_' + name;
				}
			};
		}
		return smart;
	}

	// TODO: convert this to the value used originally
	public static class Standard extends NameUtil implements NameConverter {

		@Override
		public String toClassName(String s) {
			return toMixedCaseName(toWordList(s), true);
			// return s;
		}

		@Override
		public String toVariableName(String s) {
			// return toMixedCaseName(toWordList(s), false);
			return s;
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
		 * Computes a Java package name from a namespace URI, as specified in
		 * the spec.
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
				/*
				 * for uri's like "www.foo.com" and "foo.com", there is no
				 * trailing file, so there's no need to look at the last '.' and
				 * substring otherwise, we loose the "com" (which would be
				 * wrong)
				 */
				String lastToken = tokens.get(tokens.size() - 1);
				idx = lastToken.lastIndexOf('.');
				if (idx > 0) {
					lastToken = lastToken.substring(0, idx);
					tokens.set(tokens.size() - 1, lastToken);
				}
			}

			// tokenize domain name and reverse. Also remove :port if it exists
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

		private static String removeIllegalIdentifierChars(String token) {
			// max expected length
			StringBuilder newToken = new StringBuilder(token.length() + 1);
			for (int i = 0; i < token.length(); i++) {
				char c = token.charAt(i);
				// c can't be used as first char
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

		private static ArrayList<String> tokenize(String str, String sep) {
			StringTokenizer tokens = new StringTokenizer(str, sep);
			ArrayList<String> r = new ArrayList<String>();

			while (tokens.hasMoreTokens())
				r.add(tokens.nextToken());

			return r;
		}

		private static <T> ArrayList<T> reverse(List<T> a) {
			ArrayList<T> r = new ArrayList<T>();

			for (int i = a.size() - 1; i >= 0; i--)
				r.add(a.get(i));

			return r;
		}

		@SuppressWarnings("rawtypes")
		private static String combine(List r, char sep) {
			StringBuilder buf = new StringBuilder(r.get(0).toString());

			for (int i = 1; i < r.size(); i++) {
				buf.append(sep);
				buf.append(r.get(i));
			}

			return buf.toString();
		}
	}

}
