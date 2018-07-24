package com.sun.tools.xjc.reader.xmlschema.ct;

/**
 * Factory class that will provide the ComplexTypeFieldBuilder class with
 * different implementations of complex type builders
 */
public class CTBuilderFactory {

	/**
	 * Build an array of all available Complex Type Builders order of
	 * instantiation:
	 * 
	 * 1. Multi Wildcard (getMultiWildCardComplexTypeBuilder())
	 * 
	 * 2. Mixed Extended (getMixedExtendedComplexTypeBuilder())
	 * 
	 * 3. Mixed Complex (getMixedComplexTypeBuilder())
	 * 
	 * 4. Fresh Complex (getFreshComplexTypeBuilder())
	 * 
	 * 5. Extended Complex (getExtendedComplexTypeBuilder())
	 * 
	 * 6. Restricted (getRestrictedComplexTypeBuilder())
	 * 
	 * 7. ST Derived (getSTDerivedComplexTypeBuilder())
	 * 
	 * The order can be changed by overriding this method.
	 * 
	 * @return
	 */
	public CTBuilder[] getCTBuilderArray() {
		return new CTBuilder[] { getMultiWildcardComplexTypeBuilder(), getMixedExtendedComplexTypeBuilder(), getMixedComplexTypeBuilder(),
				getFreshComplexTypeBuilder(), getExtendedComplexTypeBuilder(), getRestrictedComplexTypeBuilder(), getSTDerivedComplexTypeBuilder() };
	}

	protected CTBuilder getMultiWildcardComplexTypeBuilder() {
		return new MultiWildcardComplexTypeBuilder();
	}

	protected CTBuilder getMixedExtendedComplexTypeBuilder() {
		return new MixedExtendedComplexTypeBuilder();
	}

	protected CTBuilder getMixedComplexTypeBuilder() {
		return new MixedComplexTypeBuilder();
	}

	protected CTBuilder getFreshComplexTypeBuilder() {
		return new FreshComplexTypeBuilder();
	}

	protected CTBuilder getExtendedComplexTypeBuilder() {
		return new ExtendedComplexTypeBuilder();
	}

	protected CTBuilder getRestrictedComplexTypeBuilder() {
		return new RestrictedComplexTypeBuilder();
	}

	protected CTBuilder getSTDerivedComplexTypeBuilder() {
		return new STDerivedComplexTypeBuilder();
	}

}
