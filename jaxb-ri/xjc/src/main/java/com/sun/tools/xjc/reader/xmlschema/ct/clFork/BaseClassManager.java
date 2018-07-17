package com.sun.tools.xjc.reader.xmlschema.ct.clFork;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;

public interface BaseClassManager {
	public void createExtension(CClassInfo extendingClass, CClass parentClass);

	public void updateFields(CClassInfo extendingClass, CClass parentClass);

	/**
	 * Implementation of the BaseClassManager class that will throw exceptions
	 * when used. The SchemaCompiler's options baseClassManager field should be
	 * a reference to an actual BaseClassManager instance
	 * 
	 */
	public static class FailureBaseClassManager implements BaseClassManager {

		@Override
		public void createExtension(CClassInfo extendingClass, CClass parentClass) {
			throw new UnsupportedOperationException("Method is not enabled.");
		}

		@Override
		public void updateFields(CClassInfo extendingClass, CClass parentClass) {
			throw new UnsupportedOperationException("Method is not enabled.");
		}

		@Override
		public CClassInfo getModifiedClass(CClassInfo ccInfo) {
			throw new UnsupportedOperationException("Method is not enabled.");
		}

	}

	public CClassInfo getModifiedClass(CClassInfo ccInfo);

}
