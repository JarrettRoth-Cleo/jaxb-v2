package com.sun.tools.xjc.reader.xmlschema.ct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.xml.xsom.XSComponent;

public class BaseClassManager {

	private static BaseClassManager self;

	public static final BaseClassManager getInstance() {
		if (self == null) {
			self = new BaseClassManager();
		}
		return self;
	}

	private BaseClassManager() {
	}
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
	 * 
	 * TODO: handles only restriction correctly
	 * 
	 * TODO: Move this code to shared location for the 2 methods
	 */
	public void createRestrictingClass(CClassInfo extendingClass, CClass parentClass) {
		CClassInfo newClassInfo = null;
		CClassInfo modifiedParentClassInfo = null;
		if (parentClass != null) {
			CClassInfo parentClassInfo = (CClassInfo) parentClass;
			checkParentClassIsSet(parentClassInfo);
			modifiedParentClassInfo = modifiedClasses.get(parentClassInfo);
		}
		newClassInfo = createNewAbstractClassInfo(extendingClass, modifiedParentClassInfo);

		extendingClass.setBaseClass(newClassInfo);
		modifiedClasses.put(extendingClass, newClassInfo);
	}

	/**
	 * Because no classes will be removed, this could be pretty straight forward
	 * 
	 * TODO: handles only extension correctly, the relation for simple type
	 * restiction is lost
	 */
	public void createExtendingClass(CClassInfo extendingClass, CClass parentClass) {
		createRestrictingClass(extendingClass, parentClass);
		// updateFieldsFromAncestors(extendingClass, (CClassInfo) parentClass);
	}

	public void updateFields(CClassInfo extendingClass, CClass parentClass) {
		updateFieldsFromAncestors(extendingClass, (CClassInfo) parentClass);
	}

	/**
	 * Makes sure that the parent class is added to the abstract class tree
	 * 
	 * @param parentClass
	 */
	private void checkParentClassIsSet(CClassInfo parentClass) {
		if (modifiedClasses.containsKey(parentClass)) {
			return;
		}

		createRestrictingClass(parentClass, parentClass.getBaseClass());
	}

	private void updateFieldsFromAncestors(CClassInfo info, CClassInfo parentClass) {
		List<CPropertyInfo> props = info.getProperties();

		CClassInfo currentParent = parentClass;
		for (CPropertyInfo parentProp : currentParent.getProperties()) {
			if (info.getProperty(parentProp.getName(true)) == null) {
				info.addProperty(parentProp);
			}
		}
		//
		// info.getProperties().clear();
		// info.getProperties().addAll(props);
	}

	/**
	 * TODO: how to handle extending mixed cases where a field matches ....well
	 * that's the other builder I think...
	 * 
	 * @param props
	 * @param name
	 * @return
	 */
	private boolean doesPropertyListContainFieldName(List<CPropertyInfo> props, String name) {
		for (CPropertyInfo info : props) {
			if (name.equals(info.getName(true))) {
				return true;
			}
		}

		return false;
	}

	private CClassInfo createNewAbstractClassInfo(CClassInfo info, CClassInfo parentInfo) {
		// TODO: make sure name is unique. This is tough being that the classes
		// are being generated in order...
		String name = info.getSqueezedName() + "IF";
		Model m = info.model;
		CClassInfoParent todoPackage = getBasePackage(info);
		XSComponent todoSource = info.getSchemaComponent();
		CClassInfo newInfo = new MyCClassInfo(m, todoPackage, todoSource, name);
		newInfo.setBaseClass(parentInfo);

		return newInfo;
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

	private CClassInfo getKeyInfoFromValue(CClassInfo value) {
		for (Map.Entry<CClassInfo, CClassInfo> entry : modifiedClasses.entrySet()) {
			if (entry.getValue() == value) {
				return entry.getKey();
			}
		}
		return null;
	}

	private class MyCClassInfo extends CClassInfo {
		public MyCClassInfo(Model model, CClassInfoParent p, XSComponent source, String name) {
			super(model, p, name, /* Locator */null, /* typeName */null, /* elementName */null, source, /* cusomizations */null);
			setAbstract();
		}

	}

}
