package xjcTests.ref;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.reader.xmlschema.ref.XmlTypeRef;
import com.sun.xml.xsom.XSElementDecl;

import xjcTests.CtBuilders.TestingBaseClassManager;

public class TestingXmlTypeRef extends XmlTypeRef {

	private final TestingBaseClassManager manager;

	public TestingXmlTypeRef(XSElementDecl decl, TestingBaseClassManager m) {
		super(decl);
		this.manager = m;
	}

	public CTypeRef toTypeRef(CElementPropertyInfo ep) {
		if (ep != null && target.getAdapterUse() != null)
			ep.setAdapter(target.getAdapterUse());
		CNonElement e = target.getInfo();

		if (e instanceof CClassInfo && manager != null) {
			CClassInfo ccInfo = (CClassInfo) e;
			CClassInfo realInfo = manager.getModifiedClass(ccInfo);
			return new CTypeRef(realInfo, decl);
		} else {
			return new CTypeRef(target.getInfo(), decl);
		}
	}

}
