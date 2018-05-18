package xjcTests;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.impl.ModelGroupImpl;
import com.sun.xml.xsom.impl.ParticleImpl;

import junit.framework.Assert;
import xjcTests.modelModifications.ModelModHandler.ModelModificationException;
import xjcTests.modelModifications.ModelModificationsManager;

public class XJCChoiceTest extends AbstractXJCTest {
	private File choiceResourcesDir = new File(resourceDir, "choice");

	private ModelModificationsManager manager;

	@Test
	// @Ignore
	public void runSimpleChoiceTest() throws Throwable {
		runTest(new ChoiceTestLogic() {

			@Override
			protected File getXsd() {
				return new File(choiceResourcesDir, "ChoiceExample.xsd");
			}

			@Override
			protected boolean genCode() {
				return false;
			}
		});
	}

	@Test
	// @Ignore
	public void overridingParentInterfaceTest() throws Throwable {
		// This tests that a nested class with the same name as the expected
		// generated choice type marker interface
		runTest(new ChoiceTestLogic() {

			@Override
			protected File getXsd() {
				return new File(choiceResourcesDir, "ChoiceExampleWithNameResolution.xsd");
			}

			@Override
			protected boolean genCode() {
				return false;
			}

		});
	}

	@Test
	public void primitiveTypesTest() throws Throwable {
		runTest(new ChoiceTestLogic() {

			@Override
			protected File getXsd() {
				return new File(choiceResourcesDir, "ChoiceExampleWithPrimitveTypes.xsd");
			}

			@Override
			protected boolean genCode() {
				return false;
			}

		});
	}

	@Test
	public void primitiveWithComplexTypeReferencesTest() throws Throwable {
		runTest(new ChoiceTestLogic() {

			@Override
			protected File getXsd() {
				return new File(choiceResourcesDir, "ChoiceExampleWithPrimitveAndComplexTypeReferences.xsd");
			}

			@Override
			protected boolean genCode() {
				return true;
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
			manager = new ModelModificationsManager(model);
			for (Map.Entry<NClass, CClassInfo> beanEntry : model.beans().entrySet()) {
				CClassInfo info = beanEntry.getValue();
				for (CPropertyInfo propInfo : info.getProperties()) {
					if (isPropertyChoice(propInfo)) {
						manager.addChoice(propInfo);
					}
				}
			}
		}

		private boolean isPropertyChoice(CPropertyInfo propInfo) {
			// TODO: is this the only way a property can be a choice?
			// TODO:should these checks use the Impls?
			if (!(propInfo.getSchemaComponent() instanceof ParticleImpl)) {
				return false;
			}
			ParticleImpl comp = (ParticleImpl) propInfo.getSchemaComponent();

			if (!(comp.getTerm() instanceof ModelGroupImpl)) {
				return false;
			}
			ModelGroupImpl term = (ModelGroupImpl) comp.getTerm();

			return "CHOICE".equals(term.getCompositor().name());
		}

	}

	private abstract class ChoiceTestLogic extends Logic {
		@Override
		protected void loadPlugins(List<Plugin> plugins) {
			plugins.add(new ChoiceResolutionPlugin());
		}

		@Override
		protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
			runManager(jModel, outputDir);
		}

		private void runManager(JCodeModel jModel, File outputDir) throws IOException {
			try {
				manager.modify(jModel);
			} catch (ModelModificationException e) {
				Assert.fail();
			}
			if (genCode()) {
				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}
				jModel.build(outputDir);
			}
		}

		protected abstract boolean genCode();
	}

}
