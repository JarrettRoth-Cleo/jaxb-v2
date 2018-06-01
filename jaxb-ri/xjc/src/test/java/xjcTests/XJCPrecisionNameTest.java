package xjcTests;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.outline.Outline;

public class XJCPrecisionNameTest extends AbstractXJCTest {

	@Test
	@Ignore
	public void simpleSuccess_test() throws Throwable {
		runTest(new Logic() {
			@Override
			public File getXsd() {
				return (new File(resourceDir, "simpleTest.xsd"));
			}

			@Override
			protected void handleS2JJAXBModel(S2JJAXBModel model) {
				assertNotNull(model);
			}
		});
	}

	@Test
	@Ignore
	public void precisionIssueCausesFailure_test() throws Throwable {
		runTest(new Logic() {

			@Override
			public File getXsd() {
				return new File(resourceDir, "simplifiedPrecision.xsd");
			}

			@Override
			protected void handleS2JJAXBModel(S2JJAXBModel model) {
				assertNotNull(model);
			}

		});
	}

	@Test
	@Ignore
	public void precisionIssueFixedWithBindings_test() throws Throwable {
		runTest(new Logic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "simplifiedPrecision.xsd");
			}

			@Override
			public void loadBindingFiles(List<File> files) {
				files.add(new File(resourceDir, "simplifiedBindings.xjb"));
			}

			@Override
			protected void handleS2JJAXBModel(S2JJAXBModel model) {
				assertNotNull(model);
			}

			@Override
			protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}
				jModel.build(outputDir);
			}
		});
	}

	@Test
	@Ignore
	public void precisionIssueFixedWithManuallyGeneratedBindingsFile_test() throws Throwable {
		runTest(new Logic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "simplifiedPrecision.xsd");
			}

			@Override
			protected void loadBindingFiles(List<File> files) {

				try {
					FileWriter writer = new FileWriter("src/test/resources/genBindings.xjb");
					BufferedWriter bw = new BufferedWriter(writer);
					bw.write(generateXjbHeader("simplifiedPrecision"));
					bw.write(generateXjbPropertyBinding("Precision2", "Precision", "semiramis"));
					bw.write(generateXjbClassBinding("Precision2", "Precision", "semiramis"));
					bw.write(generateXjbEnd());
					bw.close();
					files.add(new File(resourceDir, "genBindings.xjb"));
				} catch (IOException x) {
					System.err.println(x);
				}

			}

			@Override
			protected void handleS2JJAXBModel(S2JJAXBModel model) {
				assertNotNull(model);
			}

		});
	}

	@Test
	@Ignore
	public void xjcRunsPlugin_test() throws Throwable {
		Logic logic = new Logic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "simplifiedPrecision.xsd");
			}

			@Override
			protected void loadPlugins(List<Plugin> plugins) {
				XJCPostProcessPlugin ppPlugin = new XJCPostProcessPlugin();
				plugins.add(ppPlugin);
			}

		};
		runTest(logic);

	}

	/**
	 * Will generate the beginning portion (opening) of a xml based binding
	 * file.
	 *
	 * @param schemaLocation
	 *            - String containing the location from current operating
	 *            directory to the xsd schema
	 * @return - String containing the beginning parts of the binding file,
	 *         without the ending.
	 */
	private String generateXjbHeader(String schemaLocation) {
		String versionEncoding = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String bindingHeader = String.format(
				"<jxb:bindings schemaLocation=\"%s.xsd\" version=\"2.1\" xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">",
				schemaLocation);
		return String.format("%s \n%s \n", versionEncoding, bindingHeader);
	}

	/**
	 * Will generate a property portion of the binding file. This must be run
	 * for each property which causes a problem/ collision within the schema.
	 *
	 * @param correctedName
	 *            - String new name of the property that will resolve the
	 *            collision.
	 * @param currentName
	 *            - String name of the property that is causing the issue.
	 * @param namespace
	 *            - the namespace of the property
	 * @return String - containing one property binding section to resolve a
	 *         single collision.
	 */
	private String generateXjbPropertyBinding(String correctedName, String currentName, String namespace) {
		String xPath = String.format(
				"//*[local-name()='element' and @name='%s']/*[local-name()='complexType']/*[local-name()='sequence']/*[local-name()='element' and @name='%s']",
				namespace, currentName);
		String nodeBeginning = String.format("\t<jxb:bindings node=\"%s\">\n", xPath);
		String property = String.format("\t\t<jxb:property name=\"%s\"/>\n", correctedName);
		String nodeEnding = "\t</jxb:bindings>\n";

		return String.format("%s%s%s", nodeBeginning, property, nodeEnding);
	}

	/**
	 * Generates another property portion of the binding file, but with the
	 * addition of a class property and name attribute.
	 *
	 * TODO: this won't work 100% of the time because of the '//*' it uses at
	 * the beginning. there could be 2 values found with the same xpath
	 *
	 * @param correctedName
	 *            - String new name of the property that will resolve the
	 *            collision.
	 * @param currentName
	 *            - String name of the property that is causing the issue.
	 * @param namespace
	 *            - String the namespace of the property
	 * @return String - result containing the formatted xml version of the
	 *         binding section.
	 */
	private String generateXjbClassBinding(String correctedName, String currentName, String namespace) {
		String xPath = String.format("//*[local-name()='element' and @name='%s']" + "/*[local-name()='complexType']" + "/*[local-name()='sequence']"
				+ "/*[local-name()='element' and @name='%s']" + "/*[local-name()='complexType']", namespace, currentName);
		String nodeBeginning = String.format("\t<jxb:bindings node=\"%s\">\n", xPath);
		String propertyName = String.format("\t\t<jxb:property name=\"%s\"/>\n", correctedName);
		String className = String.format("\t\t<jxb:class name=\"%s\"/>\n", correctedName);
		String nodeEnding = "\t</jxb:bindings>\n";

		return String.format("%s%s%s%s", nodeBeginning, propertyName, className, nodeEnding);

	}

	/**
	 * Simple helper method to generate the final closing tags of an xjb file.
	 *
	 * @return - String containing the ending xml.
	 */
	private String generateXjbEnd() {
		return "</jxb:bindings>";
	}

	private class XJCPostProcessPlugin extends Plugin {
		List<String> names;

		@Override
		public void postProcessModel(Model model, ErrorHandler errorHandler) {
			super.postProcessModel(model, errorHandler);
			Map<NClass, Map<QName, CElementInfo>> elementMappings = model.getElementMappings();
			// List<Map<QName, CElementInfo>> maps = new ArrayList<Map<QName,
			// CElementInfo>>(x.values());
			List<String> classNameList = new ArrayList<>();
			getNamesOfMappedElements(elementMappings, classNameList);
			System.out.println("x");
			// filter(model);
		}

		@Override
		public String getOptionName() {
			return "test";
		}

		@Override
		public String getUsage() {
			return "-test";
		}

		@Override
		public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
			return true;
		}

		private void getNamesOfMappedElements(Map<NClass, Map<QName, CElementInfo>> rootMaps, List<String> accumulatedNames) {
			for (Map.Entry<NClass, Map<QName, CElementInfo>> m : rootMaps.entrySet()) {
				getNameAndSubElementNames(accumulatedNames, null, m.getValue());
			}
		}

		private void getNameAndSubElementNames(List<String> accumulatedNames, String elementName, Map<QName, CElementInfo> mapOfChildren) {
			if (elementName != null) {
				accumulatedNames.add(elementName);
			}

			if (mapOfChildren != null) {
				for (Map.Entry<QName, CElementInfo> e : mapOfChildren.entrySet()) {
					String simpleName = e.getKey().getNamespaceURI();
					getNamesOfMappedElements(((CClassInfo) e.getValue().parent).model.getElementMappings(), accumulatedNames);
				}
			}
		}

		private void filter(Model model) {
			Map<String, List<String>> packageMap = new HashMap<>();

			Map<NClass, Map<QName, CElementInfo>> elementMappings = model.getElementMappings();
			List<String> xx = new ArrayList<>();
			for (Map.Entry<NClass, Map<QName, CElementInfo>> element : elementMappings.entrySet()) {
				NClass key = element.getKey();
				if (key != null) {
					CClassInfo keyInfo = (CClassInfo) key;
					String packageName = keyInfo.shortName;
					String squeezedName = keyInfo.getSqueezedName();
					String x = keyInfo.getUserSpecifiedImplClass();
					xx.add(squeezedName);
					System.out.println("PackageName: " + packageName);
					System.out.println("SqueezedName: " + squeezedName);

					List<String> classNames = new ArrayList<>();

					for (Map.Entry<QName, CElementInfo> currentClassEntry : element.getValue().entrySet()) {
						QName qnameKey = currentClassEntry.getKey();
						classNames.add(qnameKey.getLocalPart());
						// System.out.println(packageName + " " +
						// qnameKey.toString());
					}
					packageMap.put(packageName, classNames);
				}
			}
			System.out.println("filter");

		}
	}
}
