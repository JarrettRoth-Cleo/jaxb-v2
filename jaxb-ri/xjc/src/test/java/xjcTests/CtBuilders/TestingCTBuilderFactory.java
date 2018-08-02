package xjcTests.CtBuilders;

import com.sun.tools.xjc.reader.xmlschema.ct.CTBuilder;
import com.sun.tools.xjc.reader.xmlschema.ct.CTBuilderFactory;
import com.sun.tools.xjc.reader.xmlschema.ct.FreshComplexTypeBuilder;

public class TestingCTBuilderFactory extends CTBuilderFactory {

	private final TestingBaseClassManager m;

	public TestingCTBuilderFactory(TestingBaseClassManager m) {
		this.m = m;
	}

	public CTBuilder getMixedExtendedComplexTypeBuilder() {
		return new TestingMixedExtendedComplexTypeBuilder(m);
	}

	public CTBuilder getExtendedComplexTypeBuilder() {
		return new TestingExtendedComplexTypeBuilder(m);
	}

	public CTBuilder getRestrictedComplexTypeBuilder() {
		return new TestingRestrictedComplexTypeBuilder(m);
	}

	public CTBuilder getFreshComplexTypeBuilder() {
		return new FreshComplexTypeBuilder();
	}

}
