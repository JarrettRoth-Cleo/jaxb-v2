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

public class ChoiceModHandler implements ModelModHandler {

	private final CPropertyInfo info;

	public ChoiceModHandler(CPropertyInfo info) {
		this.info = info;
	}

	// TODO: method-ise this
	@Override
	public void handle(JCodeModel model) throws ModelModificationException {
		JDefinedClass clazz = model._getClass(info.parent().toString());
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
			JDefinedClass definedClass = findClass(info, model);
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
	 * Iterates over the list of defined nested classes/interfaces and will
	 * return a unique name for a new interface
	 * 
	 * TODO: should this support something other than JDefinedClass? Would it be
	 * better to put these interfaces in packages?
	 * 
	 * @param baseName
	 * @param parentClazz
	 * @return
	 */
	private String getUniqueInterfaceName(String baseName, JDefinedClass parentClazz) {
		// Build a list of existing nested class/interface names
		JClass[] classes = parentClazz.listClasses();
		if (classes != null && classes.length >= 1) {
			List<String> existingNames = new ArrayList<String>(classes.length);
			// TODO: handle sub and parent classes in tree
			for (JClass curClass : classes) {
				existingNames.add(curClass.name());
			}
			return getUniqueNameFromList(baseName, existingNames);
		} else {
			return baseName;
		}
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

	private JDefinedClass findClass(CTypeInfo typeInfo, JCodeModel jModel) {
		// TODO: can this be any thing else?
		// TODO: JCodeModel doesn't work with Nested classes. Fix this...
		if (typeInfo instanceof CClassInfo) {
			CClassInfo info = (CClassInfo) typeInfo;
			CClassInfoParent p = info.parent();
			if (p instanceof CClassInfoParent.Package) {
				String fullName = info.fullName();
				return jModel._getClass(fullName);
			} else if (p instanceof CClassInfo) {
				String parentFQN = p.fullName();
				// TODO: handle multiple levels of nesting...
				return jModel._getClass(parentFQN).getNestedClass(info.shortName);
			} else {
				System.out.println("TODO: handle this parent type: " + p.getClass());
			}
		} else {
			System.out.println("TODO handle: " + typeInfo.getClass());
		}
		return null;
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

}
