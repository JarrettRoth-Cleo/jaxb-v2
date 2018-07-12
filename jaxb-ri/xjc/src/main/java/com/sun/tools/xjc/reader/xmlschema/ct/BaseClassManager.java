package com.sun.tools.xjc.reader.xmlschema.ct;

import java.util.HashMap;
import java.util.Map;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.Model;
import com.sun.xml.xsom.XSComponent;

public class BaseClassManager {
	/*
	 * This class will attempt to manage an inheritance tree that the
	 * ExtendedComplexTypeBuilder and the restriction will use to set its
	 * classes correctly...
	 * 
	 */

	/**
	 * Key - original parent (base) class
	 * 
	 * Value - generated abstract class
	 */
	private Map<CClassInfo, CClassInfo> modifiedClasses = new HashMap<>();

	/**
	 * Because no classes will be removed, this could be pretty straight forward
	 */
	public void setBaseClass(CClassInfo extendingClass, CClass parentClass) {

		CClassInfoParent newClassPackage = getBasePackage((CClassInfo) parentClass);
		CClassInfo newClassInfo = new MyCClassInfo(extendingClass.model, newClassPackage, extendingClass.getSchemaComponent());

		// TODO: cache things...
		extendingClass.setBaseClass(newClassInfo);
	}

	/**
	 * TODO: create a new package instead of placing in the same as the working
	 * class.
	 * 
	 * TODO: will not handle all cases potentially.
	 * 
	 * @param parentClass
	 * @return
	 */
	private CClassInfoParent getBasePackage(CClassInfo parentClass) {
		CClassInfoParent p = parentClass.parent();
		if (!(p instanceof CClassInfoParent.Package)) {
			return getBasePackage((CClassInfo) p);
		}
		return p;
	}

	private class MyCClassInfo extends CClassInfo {
		public MyCClassInfo(Model model, CClassInfoParent p, XSComponent source) {
			super(model, p, "shortName", /* Locator */null, /* typeName */null, /* elementName */null, source, /* cusomizations */null);
			setAbstract();
		}
	}

}
