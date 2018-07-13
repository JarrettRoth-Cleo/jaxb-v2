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
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;

public class XJCExtensionTests extends AbstractXJCTest {
	private File extensionsResourceDir = new File(resourceDir, "extensions");

	@Test
	public void simpleTypeRestrictionAndExtensionTest() throws Throwable {
		runTest(new XJCExtensionLogic() {
			@Override
			protected File getXsd() {
				return new File(extensionsResourceDir, "JustRestrictionIssue.xsd");
			}

			@Override
			protected void validateModel(Model m) {
				CClassInfo identifierAb = getInfoFromModel(m, "test.IdentifierTypeIF");
				CClassInfo partyIdentifierAb = getInfoFromModel(m, "test.PartyIdentifierTypeIF");
				CClassInfo bankAccountIdentifierAb = getInfoFromModel(m, "test.BankAccountPartyRelationshipTypeIF");

				CClassInfo identifierType = getInfoFromModel(m, "test.IdentifierType");
				CClassInfo partyIdentifierType = getInfoFromModel(m, "test.PartyIdentifierType");
				CClassInfo bankAccountidentifierType = getInfoFromModel(m, "test.BankAccountPartyRelationshipType");

				Assert.assertTrue(identifierType.getBaseClass() == identifierAb);
				Assert.assertTrue(partyIdentifierType.getBaseClass() == partyIdentifierAb);
				Assert.assertTrue(bankAccountidentifierType.getBaseClass() == bankAccountIdentifierAb);

				Assert.assertTrue(partyIdentifierAb.getBaseClass() == identifierAb);
				Assert.assertTrue(bankAccountIdentifierAb.getBaseClass() == partyIdentifierAb);

			}

		});
	}

	@Test
	public void complexTypeRestriction() throws Throwable {
		runTest(new XJCExtensionLogic() {
			@Override
			protected File getXsd() {
				return new File(extensionsResourceDir, "BookExampleRestriction.xsd");
			}

			@Override
			protected void validateModel(Model m) {
				CClassInfo phyicalAddressAB = getInfoFromModel(m, "test.physicalAddressTypeIF");
				CClassInfo simpleAddressAB = getInfoFromModel(m, "test.simpleAddressTypeIF");

				CClassInfo phyicalAddress = getInfoFromModel(m, "test.physicalAddressType");
				CClassInfo simpleAddress = getInfoFromModel(m, "test.simpleAddressType");

				Assert.assertTrue(phyicalAddress.getBaseClass() == phyicalAddressAB);
				Assert.assertTrue(simpleAddress.getBaseClass() == simpleAddressAB);

				Assert.assertTrue(simpleAddressAB.getBaseClass() == phyicalAddressAB);

			}
		});
	}

	@Test
	public void complexExtensionFieldChecking() throws Throwable {
		runTest(new XJCExtensionLogic() {
			@Override
			protected File getXsd() {
				return new File(extensionsResourceDir, "BookExampleExtension.xsd");
			}

			@Override
			protected void validateModel(Model m) {
				CClassInfo simpleAddress = getInfoFromModel(m, "test.simpleAddressType");
				Assert.assertEquals("Incorrect property list size", 4, simpleAddress.getProperties().size());

			}
		});
	}

	@Test
	public void mixedExtension() throws Throwable {
		runTest(new XJCExtensionLogic() {
			@Override
			protected File getXsd() {
				return new File(extensionsResourceDir, "MixedExtension.xsd");
			}

			@Override
			protected void validateModel(Model m) {
				CClassInfo simpleAddress = getInfoFromModel(m, "test.mixedExtension");
				Assert.assertEquals("Incorrect property list size", 1, simpleAddress.getProperties().size());

			}
		});
	}

	@Test
	public void mixedExtensionBothSetMixed() throws Throwable {
		runTest(new XJCExtensionLogic() {
			@Override
			protected File getXsd() {
				return new File(extensionsResourceDir, "MixedExtensionBothSet.xsd");
			}

			protected boolean genCode() {
				return true;
			}

			@Override
			protected void validateModel(Model m) {
				CClassInfo simpleAddress = getInfoFromModel(m, "test.mixedExtension");
				Assert.assertEquals("Incorrect property list size", 1, simpleAddress.getProperties().size());
				CPropertyInfo propInfo = simpleAddress.getProperty("mixedExtension_Mixed");
				Assert.assertTrue(isMixed(propInfo));
			}
		});
	}

	@Test
	public void mixedExtensionBothSetMixedInterfaceChecking() throws Throwable {
		runTest(new XJCExtensionLogic() {
			@Override
			protected File getXsd() {
				return new File(extensionsResourceDir, "MixedExtensionBothSet.xsd");
			}

			@Override
			protected void validateModel(Model m) {
				CClassInfo mixedExtensionAB = getInfoFromModel(m, "test.mixedExtensionIF");
				CClassInfo mixedParentAB = getInfoFromModel(m, "test.mixedParentIF");

				CClassInfo mixedExtension = getInfoFromModel(m, "test.mixedExtension");
				CClassInfo mixedParent = getInfoFromModel(m, "test.mixedParent");

				Assert.assertTrue(mixedParent.getBaseClass() == mixedParentAB);
				Assert.assertTrue(mixedExtension.getBaseClass() == mixedExtensionAB);

				Assert.assertTrue(mixedExtensionAB.getBaseClass() == mixedParentAB);
			}
		});
	}

	private boolean isMixed(CPropertyInfo propInfo) {
		if (propInfo instanceof CReferencePropertyInfo) {
			return ((CReferencePropertyInfo) propInfo).isMixed();
		}

		return false;
	}

	private void assertClassInModel(Model m, String className) {
		for (CClassInfo info : m.beans().values()) {
			if (info.fullName().equals(className)) {
				return;
			}
		}
		Assert.fail(String.format("%s could not be found in model", className));
	}

	private CClassInfo getInfoFromModel(Model m, String className) {
		assertClassInModel(m, className);

		for (com.sun.tools.xjc.model.CClassInfo info : m.beans().values()) {
			if (info.fullName().equals(className)) {
				return info;
			}
		}
		throw new RuntimeException(String.format("%s could not be found in model", className));
	}

	private abstract class XJCExtensionLogic extends Logic {

		protected void loadBindingFiles(List<File> files) {
			files.add(new File(extensionsResourceDir, "extensionBindings.xjb"));
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
