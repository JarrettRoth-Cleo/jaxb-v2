package xjcTests.modelModifications;

import java.util.ArrayList;
import java.util.List;

import com.sun.codemodel.JAssignment;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JNarrowedClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;

/**
 * JCodeModel modification handler for a choice definition.
 * 
 * This process will implement a new Marker interface for a field that contains
 * a choice. Each referenced definition implements this new interface to be
 * utilized in a Ruleset switch statement.
 *
 */
public class ChoiceModHandler implements ModelModHandler {

	private final CPropertyInfo info;

	public ChoiceModHandler(CPropertyInfo info) {
		this.info = info;
	}

	// TODO: method-ise this
	@Override
	public void handle(JCodeModel model) throws ModelModificationException {
		JDefinedClass clazz = getClassByTypeInfo(model, info.parent());
		JFieldVar field = clazz.fields().get(info.getName(true));

		String interfaceName = getUniqueInterfaceName(info.getName(true) + "_Type", clazz);
		JClass newType;
		try {
			newType = clazz._interface(interfaceName);
		} catch (JClassAlreadyExistsException e) {
			throw new ModelModificationException();
		}

		// handle the references
		for (CTypeInfo info : info.ref()) {
			JDefinedClass definedClass = getClassByTypeInfo(model, info);
			// TODO
			if (definedClass != null)
				definedClass._implements(newType);
		}

		// handle the field
		JClass fieldClass = buildFieldType(newType, field);
		field.type(fieldClass);

		// handle the method
		JMethod m = clazz.getMethod("get" + field.name(), new JType[0]);
		modifyPropertyMethod(m, fieldClass, newType);
	}

	/**
	 * Get the Defined class from the JCodeModel instance. This will handle
	 * nested classes.
	 * 
	 * @param model
	 * @param fqn
	 * @return
	 */
	private JDefinedClass getClassByTypeInfo(JCodeModel model, CTypeInfo typeInfo) {
		if (typeInfo instanceof CClassInfo) {
			CClassInfo info = (CClassInfo) typeInfo;
			if (isParentPackage(info)) {
				String fullName = info.fullName();
				return model._getClass(fullName);
			}

			CClassInfoParent parent = info.parent();

			String packageVal = parent.getOwnerPackage().name();
			String typeFqn = typeInfo.toString();
			String[] nestedClassPathParts = typeFqn.substring(packageVal.length() + 1).split("\\.");

			String currentFqn = packageVal + "." + nestedClassPathParts[0];

			JDefinedClass returnClass = model._getClass(currentFqn);
			for (int i = 1; i < nestedClassPathParts.length; i++) {
				returnClass = returnClass.getNestedClass(nestedClassPathParts[i]);
			}

			return returnClass;
		}

		// TODO
		return null;
	}

	/**
	 * Iterates over the list of defined nested classes/interfaces and will
	 * return a unique name for a new interface
	 * 
	 * TODO: should this support something other than JDefinedClass? Would it be
	 * better to put these interfaces in packages?
	 * 
	 * TODO: move this to its own class
	 * 
	 * @param baseName
	 * @param parentClazz
	 * @return
	 */
	private String getUniqueInterfaceName(String baseName, JDefinedClass parentClazz) {
		// Build a list of existing nested class/interface names
		List<String> existingNames = buildListOfExistingClassNames(parentClazz);
		return getUniqueNameFromList(baseName, existingNames);
	}

	// TODO: handle sub and parent classes in tree
	// TODO: ensure this handles interface
	// TODO: only analyze a class once
	private List<String> buildListOfExistingClassNames(JDefinedClass parentClazz) {
		JClass[] classes = parentClazz.listClasses();
		List<String> names = new ArrayList<String>();
		for (JClass curClass : classes) {
			names.add(curClass.name());
			// handle any sub classes
			if (curClass instanceof JDefinedClass) {
				names.addAll(buildListOfExistingClassNames((JDefinedClass) curClass));
			}
		}
		return names;
	}

	/**
	 * Build a unique name using the baseName + counter
	 * 
	 * TODO: use NamingUtil in Clarify
	 * 
	 * @param baseName
	 * @param existingNames
	 * @return
	 */
	private String getUniqueNameFromList(String baseName, List<String> existingNames) {
		int counter = 0;
		String modifiedName = baseName;
		// TODO: make case insensitive
		while (existingNames.contains(modifiedName)) {
			modifiedName += ++counter;
		}
		return modifiedName;
	}

	private void modifyPropertyMethod(JMethod m, JClass fieldClass, JClass newType) {
		// set the return type
		m.type(fieldClass);

		// set the type for the assignment of the field...
		JConditional ifStatement = ((JConditional) (m.body().getContents().get(0)));
		JAssignment assignment = (JAssignment) ifStatement._then().getContents().get(0);
		JInvocation expression = (JInvocation) assignment.getRhs();

		// TODO
		expression.type(buildNarrowedClass((JNarrowedClass) expression.getType(), newType));

	}

	/**
	 * Gets the type for the field and for the method..
	 * 
	 * @return
	 */
	private JClass buildFieldType(JClass newType, JFieldVar field) {
		JClass newFieldType = newType;
		if (field.type() instanceof JNarrowedClass) {
			JNarrowedClass narrowClass = (JNarrowedClass) field.type();
			newFieldType = buildNarrowedClass(narrowClass, newType);
		}
		return newFieldType;
	}

	/**
	 * Create a new class that is defined using Generics.
	 * 
	 * ex: ArrayList<String>
	 * 
	 * TODO: how to handle a case similar to this:
	 * 
	 * ArrayList<GenericType <String>>
	 * 
	 * @param narrowClass
	 * @param newType
	 * @return
	 */
	private JClass buildNarrowedClass(JNarrowedClass narrowClass, JClass newType) {
		JClass basis = narrowClass.getBasis();
		List<JClass> args = narrowClass.getArgs();
		List<JClass> newArgs = new ArrayList<JClass>();
		for (JClass arg : args) {
			// TODO: should this be a parameter?
			if ("java.lang.Object".equals(arg.binaryName())) {
				newArgs.add(newType);
			} else {
				newArgs.add(arg);
			}
		}

		return new JNarrowedClass(basis, newArgs);
	}

	private boolean isParentPackage(CClassInfo cci) {
		return cci.parent() instanceof CClassInfoParent.Package;
	}

}
