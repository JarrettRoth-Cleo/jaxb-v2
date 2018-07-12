package xjcTests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;

public class XJCRestrictionIssueTest extends AbstractXJCTest {
	@Test
	// TODO: get actual testing here...
	public void simpleTypeRestrictionAndExtensionTest() throws Throwable {
		runTest(new Logic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "JustRestrictionIssue.xsd");
			}

			protected void loadBindingFiles(List<File> files) {
				files.add(new File(resourceDir, "restrictionBindings.xjb"));
			}

			protected void loadPlugins(List<Plugin> plugins) {
				plugins.add(new PostProcessPlugin());
			}

			protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
				System.out.println(outputDir.getAbsolutePath());
				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}
				jModel.build(outputDir);
			}
		});
	}

	@Test
	// TODO: get actual testing here...
	public void complexTypeRestriction() throws Throwable {
		runTest(new Logic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "BookExample.xsd");
			}

			protected void loadBindingFiles(List<File> files) {
				files.add(new File(resourceDir, "restrictionBindings.xjb"));
			}

			protected void loadPlugins(List<Plugin> plugins) {
				plugins.add(new PostProcessPlugin());
			}

			protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
				System.out.println(outputDir.getAbsolutePath());
				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}
				jModel.build(outputDir);
			}
		});
	}

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
			for (CClassInfo info : model.beans().values()) {
				System.out.println(info.fullName());
				// if
				// ("com.boehringer_ingelheim.finance._1.PartyIdentifierType".equals(info.fullName()))
				// {
				// System.out.println("");
				// }
				for (com.sun.tools.xjc.model.CPropertyInfo prop : info.getProperties()) {
					System.out.println("\t" + prop.getName(true));
				}
			}
		}
	}
}
