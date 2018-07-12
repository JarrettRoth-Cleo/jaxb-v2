package xjcTests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;

public class XJCExtensionTests extends AbstractXJCTest {
	@Test
	public void simpleTypeRestrictionAndExtensionTest() throws Throwable {
		runTest(new XJCExtensionLogic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "JustRestrictionIssue.xsd");
			}

			@Override
			protected void validateModel(Model m) {
				assertClassInModel(m, "test.IdentifierTypeIF");
				assertClassInModel(m, "test.PartyIdentifierTypeIF");
				assertClassInModel(m, "test.BankAccountPartyRelationshipTypeIF");

				assertClassInModel(m, "test.IdentifierType");
				assertClassInModel(m, "test.PartyIdentifierType");
				assertClassInModel(m, "test.BankAccountPartyRelationshipType");
			}

		});
	}

	@Test
	public void complexTypeRestriction() throws Throwable {
		runTest(new XJCExtensionLogic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "BookExample.xsd");
			}

			@Override
			protected void validateModel(Model m) {
				assertClassInModel(m, "test.physicalAddressTypeIF");
				assertClassInModel(m, "test.simpleAddressTypeIF");

				assertClassInModel(m, "test.physicalAddressType");
				assertClassInModel(m, "test.simpleAddressType");

			}
		});
	}

	private void assertClassInModel(Model m, String className) {
		for (com.sun.tools.xjc.model.CClassInfo info : m.beans().values()) {
			if (info.fullName().equals(className)) {
				return;
			}
		}
		Assert.fail(String.format("%s could not be found in model", className));
	}

	private abstract class XJCExtensionLogic extends Logic {

		protected void loadBindingFiles(List<File> files) {
			files.add(new File(resourceDir, "restrictionBindings.xjb"));
		}

		protected void loadPlugins(List<Plugin> plugins) {
			plugins.add(new PostProcessPlugin());
		}

		protected boolean genCode() {
			return false;
		}

		protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
			if (genCode()) {
				System.out.println(outputDir.getAbsolutePath());
				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}
				jModel.build(outputDir);
			}
		}

		protected abstract void validateModel(Model m);

		private class PostProcessPlugin extends Plugin {

			@Override
			public String getOptionName() {
				// TODO Auto-generated method stub
				return "everthing";
			}

			@Override
			public String getUsage() {
				// TODO Auto-generated method stub
				return "allTheTime";
			}

			@Override
			public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
				// TODO Auto-generated method stub
				return true;
			}

			public void postProcessModel(Model model, ErrorHandler errorHandler) {
				validateModel(model);
			}
		}
	}
}
