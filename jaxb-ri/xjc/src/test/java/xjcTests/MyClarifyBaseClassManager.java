package xjcTests;

import java.util.HashMap;
import java.util.Map;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.reader.xmlschema.ct.clFork.BaseClassManager;
import com.sun.xml.xsom.XSComponent;

public class MyClarifyBaseClassManager implements BaseClassManager {

	private CClassInfoParent.Package abstractClassPackage;
	private final String PACKAGE_NAME = "ohBoiNewPackageName2";

	public MyClarifyBaseClassManager() {
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
	public void createExtension(CClassInfo extendingClass, CClass parentClass) {
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

		createExtension(parentClass, parentClass.getBaseClass());
	}

	private void updateFieldsFromAncestors(CClassInfo info, CClassInfo parentClass) {
		CClassInfo currentParent = parentClass;
		for (CPropertyInfo parentProp : currentParent.getProperties()) {
			if (info.getProperty(parentProp.getName(true)) == null) {
				info.addProperty(parentProp);
			}
		}
	}

	private CClassInfo createNewAbstractClassInfo(CClassInfo info, CClassInfo parentInfo) {
		// TODO: make sure name is unique. This is tough being that the classes
		// are being generated in order...
		String name = info.getSqueezedName() + "IF";
		Model m = info.model;
		CClassInfoParent todoPackage = getAbstractClassPackage(info.model);
		XSComponent todoSource = info.getSchemaComponent();
		CClassInfo newInfo = new MyCClassInfo(m, todoPackage, todoSource, name);
		newInfo.setBaseClass(parentInfo);

		return newInfo;
	}

	private CClassInfoParent.Package getAbstractClassPackage(Model m) {
		if (abstractClassPackage == null) {
			JCodeModel codeModel = m.codeModel;
			JPackage jpack = codeModel._package(PACKAGE_NAME);
			abstractClassPackage = new CClassInfoParent.Package(jpack);
		}
		return abstractClassPackage;
	}

	public Map<CClassInfo, CClassInfo> getModifiedClasses() {
		return modifiedClasses;
	}

	public void reset() {
		modifiedClasses.clear();
		abstractClassPackage = null;
	}

	public String getPackageName() {
		return PACKAGE_NAME;
	}

	private class MyCClassInfo extends CClassInfo {
		public MyCClassInfo(Model model, CClassInfoParent p, XSComponent source, String name) {
			super(model, p, name, /* Locator */null, /* typeName */null, /* elementName */null, source, /* cusomizations */null);
			setAbstract();
		}

	}

}
