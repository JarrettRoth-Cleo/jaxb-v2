package xjcTests.ref;

import com.sun.tools.xjc.reader.xmlschema.ref.Ref;
import com.sun.tools.xjc.reader.xmlschema.ref.RefFactory;
import com.sun.xml.xsom.XSElementDecl;

import xjcTests.CtBuilders.TestingBaseClassManager;

public class TestingRefFactory extends RefFactory {
	private final TestingBaseClassManager m;

	public TestingRefFactory(TestingBaseClassManager m) {
		this.m = m;
	}

	public Ref initXmlTypeRef(XSElementDecl decl) {
		return new TestingXmlTypeRef(decl, m);
	}

}
