package xjcTests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

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
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.impl.ModelGroupImpl;
import com.sun.xml.xsom.impl.ParticleImpl;

import junit.framework.Assert;

public class XJCChoiceTest extends AbstractXJCTest {

	private ModelModificationsManager manager = new ModelModificationsManager();

	@Test
	public void runSimpleChoiceTest() throws Throwable {
		runTest(new Logic() {

			@Override
			protected File getXsd() {
				return new File(resourceDir, "ChoiceExample.xsd");
			}

			@Override
			protected void loadPlugins(List<Plugin> plugins) {
				plugins.add(new ChoiceResolutionPlugin());
			}

			@Override
			protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
				try {
					managerLogic(jModel);
				} catch (JClassAlreadyExistsException e) {
					Assert.fail();
				}

				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}
				jModel.build(outputDir);
			}
		});
	}

	class ChoiceResolutionPlugin extends Plugin {

		@Override
		public String getOptionName() {
			return "face";
		}

		@Override
		public String getUsage() {
			return "ThisTest";
		}

		@Override
		public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
			return true;
		}

		@Override
		public void postProcessModel(Model model, ErrorHandler errorHandler) {
			for (Map.Entry<NClass, CClassInfo> beanEntry : model.beans().entrySet()) {
				// TODO: remove this hard coded value.
				if ("issue.choice.just.clarify.cleo.ChoiceType".equals(beanEntry.getKey().toString())) {
					CClassInfo info = beanEntry.getValue();
					for (CPropertyInfo propInfo : info.getProperties()) {
						ParticleImpl comp = (ParticleImpl) propInfo.getSchemaComponent();
						ModelGroupImpl term = (ModelGroupImpl) comp.getTerm();

						if ("CHOICE".equals(term.getCompositor().name())) {
							manager.addChoice(propInfo);
						}
					}
				}
			}
		}

	}

	// TODO: method-ise this
	private void managerLogic(JCodeModel jModel) throws JClassAlreadyExistsException, IOException {
		for (CPropertyInfo choice : manager.getChoices()) {
			JDefinedClass clazz = jModel._getClass(choice.parent().toString());
			JFieldVar field = clazz.fields().get(choice.getName(true));

			String interfaceName = getUniqueInterfaceName(choice.getName(true) + "_Type", clazz);
			JClass newType = clazz._interface(interfaceName);

			// handle the references
			for (CTypeInfo info : choice.ref()) {
				JDefinedClass definedClass = findClass(info, jModel);
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
