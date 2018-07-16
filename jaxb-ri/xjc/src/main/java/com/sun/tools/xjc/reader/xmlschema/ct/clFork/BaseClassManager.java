package com.sun.tools.xjc.reader.xmlschema.ct.clFork;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;

public interface BaseClassManager {
	public void createExtension(CClassInfo extendingClass, CClass parentClass);

	public void updateFields(CClassInfo extendingClass, CClass parentClass);

}
