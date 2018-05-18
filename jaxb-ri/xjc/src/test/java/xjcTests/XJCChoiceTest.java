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
				runManager(jModel, outputDir, false);
			}

		});
	}

	@Test
	// TODO: need to make this have nested classes like a pleb
	public void overridingParentInterfaceTest() throws Throwable {
		runTest(new Logic() {

			@Override
			protected File getXsd() {
				return new File(resourceDir, "ChoiceExampleWithNameResolution.xsd");
			}

			@Override
			protected void loadPlugins(List<Plugin> plugins) {
				plugins.add(new ChoiceResolutionPlugin());
			}

			@Override
			protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
				runManager(jModel, outputDir, true);
			}

		});
	}

	private void runManager(JCodeModel jModel, File outputDir, boolean genCode) throws IOException {
		try {
			manager.modify(jModel);
		} catch (ModelModificationException e) {
			Assert.fail();
		}
		if (genCode) {
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			jModel.build(outputDir);
		}
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
				// if
				// ("issue.choice.just.clarify.cleo.ChoiceType".equals(beanEntry.getKey().toString()))
				// {
				CClassInfo info = beanEntry.getValue();
				for (CPropertyInfo propInfo : info.getProperties()) {
					ParticleImpl comp = (ParticleImpl) propInfo.getSchemaComponent();
					ModelGroupImpl term = (ModelGroupImpl) comp.getTerm();

					if ("CHOICE".equals(term.getCompositor().name())) {
						manager.addChoice(propInfo);
					}
					// }
				}
			}
		}
	}

}
