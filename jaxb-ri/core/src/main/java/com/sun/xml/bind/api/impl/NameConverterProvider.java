package com.sun.xml.bind.api.impl;

import javax.lang.model.SourceVersion;

public class NameConverterProvider {

	private static Standard standard;
	private static NameConverter jaxrpcCompatible;
	private static NameConverter smart;

	public static void setStandard(Standard nc) {
		standard = nc;
	}

	public static void setJaxrpcCompatible(NameConverter nc) {
		jaxrpcCompatible = nc;
	}

	public static void setSmart(NameConverter nc) {
		smart = nc;
	}

	public static Standard getStandard() {
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
			final Standard standardNC = getStandard();

			jaxrpcCompatible = new Standard() {

				@Override
				protected boolean isPunct(char c) {
					return (c == '.' || c == '-' || c == ';' || c == '\u00b7' || c == '\u0387' || c == '\u06dd' || c == '\u06de');
				}

				@Override
				protected boolean isLetter(char c) {
					return standardNC.isLetter(c) || c == '_';
				}

				@Override
				protected int classify(char c0) {
					if (c0 == '_')
						return NameUtil.OTHER_LETTER;
					return standardNC.classify(c0);
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
			final Standard standardNC = getStandard();
			smart = new Standard() {
				@Override
				public String toConstantName(String token) {
					String name = standardNC.toConstantName(token);
					if (!SourceVersion.isKeyword(name))
						return name;
					else
						return '_' + name;
				}
			};
		}
		return smart;
	}

}
