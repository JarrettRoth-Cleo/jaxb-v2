package xjcTests.CtBuilders;

import com.sun.tools.xjc.reader.xmlschema.ct.CTBuilder;
import com.sun.tools.xjc.reader.xmlschema.ct.CTBuilderFactory;

public class TestingCTBuilderFactory extends CTBuilderFactory {

	private final TestingBaseClassManager m;

	public TestingCTBuilderFactory(TestingBaseClassManager m) {
		this.m = m;
	}

	protected CTBuilder getMixedExtendedComplexTypeBuilder() {
		return new TestingMixedExtendedComplexTypeBuilder(m);
	}

	protected CTBuilder getExtendedComplexTypeBuilder() {
		return new TestingExtendedComplexTypeBuilder(m);
	}

	protected CTBuilder getRestrictedComplexTypeBuilder() {
		return new TestingRestrictedComplexTypeBuilder(m);
	}

}
