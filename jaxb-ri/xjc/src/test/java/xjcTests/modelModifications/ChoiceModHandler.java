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
	private final String interfaceTypeName;

	public ChoiceModHandler(CPropertyInfo info, String interfaceTypeName) {
		this.info = info;
		this.interfaceTypeName = interfaceTypeName;
	}

	@Override
	public void handle(JCodeModel model) throws ModelModificationException {
		// Load information
		JDefinedClass clazz = getClassByTypeInfo(model, info.parent());
		JFieldVar field = clazz.fields().get(info.getName(true));
		JClass newType = initNewInterface(clazz);
		JClass fieldClass = buildFieldType(newType, field);

		// Update all references.
		updateField(field, fieldClass);
		updateFieldMethod(clazz, field.name(), fieldClass, newType);
		updateReferences(model, newType);
	}

	private JClass initNewInterface(JDefinedClass clazz) throws ModelModificationException {
		try {
			return clazz._interface(interfaceTypeName);
		} catch (JClassAlreadyExistsException e) {
			throw new ModelModificationException();
		}
	}

	/**
	 * Update any information for the field.
	 * 
	 * @param field
	 * @param fieldClass
	 */
	private void updateField(JFieldVar field, JClass fieldClass) {
		field.type(fieldClass);
	}

	private void updateFieldMethod(JDefinedClass clazz, String fieldName, JClass fieldClass, JClass newType) {
		JMethod m = clazz.getMethod("get" + fieldName, new JType[0]);
		modifyPropertyMethod(m, fieldClass, newType);
	}

	/**
	 * Update the referenced choice options to extend the correct value.
	 * 
	 * @param model
	 * @param newType
	 */
	private void updateReferences(JCodeModel model, JClass newType) {
		for (CTypeInfo info : info.ref()) {
			JDefinedClass definedClass = getClassByTypeInfo(model, info);
			if (definedClass != null) {
				definedClass._implements(newType);
			}
		}
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
