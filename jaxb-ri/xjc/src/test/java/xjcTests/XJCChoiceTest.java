package xjcTests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JNarrowedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;

public class XJCChoiceTest extends AbstractXJCTest {

	// @Ignore
	@Test
	public void runSimpleChoiceTest() throws Throwable {
		runTest(new Logic() {

			@Override
			protected File getXsd() {
				return new File(resourceDir, "ChoiceExample.xsd");
			}

			@Override
			protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
				// TODO
				JDefinedClass clazz = jModel._getClass("issue.choice.just.clarify.cleo.ChoiceType");
				JClass newType = new WhoopHereitIs(jModel, clazz);
				clazz._implements(newType);

				Map<String, com.sun.codemodel.JFieldVar> fields = clazz.fields();
				com.sun.codemodel.JFieldVar field = fields.get("RelatiesOrMaterialGroupsOrMaterialStock");

				JClass newFieldType = newType;
				if (field.type() instanceof JNarrowedClass) {
					JNarrowedClass narrowClass = (JNarrowedClass) field.type();
					JClass basis = narrowClass.getBasis();
					List<JClass> args = narrowClass.getArgs();
					List<JClass> newArgs = new ArrayList<JClass>();
					for (JClass arg : args) {
						if ("java.lang.Object".equals(arg.binaryName())) {
							newArgs.add(newType);
						} else {
							newArgs.add(arg);
						}
					}

					newFieldType = new JNarrowedClass(basis, newArgs);

				}
				field.type(newFieldType);

				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}
				jModel.build(outputDir);
			}

		});
	}

	@Ignore
	@Test
	public void runSimpleChoiceWithBindings() throws Throwable {
		runTest(new Logic() {

			@Override
			protected File getXsd() {
				return new File(resourceDir, "ChoiceExample.xsd");
			}

			@Override
			protected void loadBindings(List<File> files) {
				files.add(new File(resourceDir, "ChoiceExampleBindings.xjb"));
			}

		});
	}

	// TODO
	private class WhoopHereitIs extends JClass {

		private final JDefinedClass clazz;

		private WhoopHereitIs(JCodeModel _owner, JDefinedClass clazz) {
			super(_owner);
			this.clazz = clazz;
		}

		@Override
		public String name() {
			// TODO
			return "ChoiceTypeChoice";
		}

		@Override
		public JPackage _package() {
			return clazz._package();
		}

		@Override
		public JClass _extends() {
			return null;
		}

		@Override
		public Iterator<JClass> _implements() {
			return null;
		}

		@Override
		public boolean isInterface() {
			return true;
		}

		@Override
		public boolean isAbstract() {
			return false;
		}

		@Override
		protected JClass substituteParams(JTypeVar[] variables, List<JClass> bindings) {
			// TODO: what is this?
			return null;
		}

		@Override
		public String fullName() {
			return "WhoopThereitIs";
		}

	}
}
