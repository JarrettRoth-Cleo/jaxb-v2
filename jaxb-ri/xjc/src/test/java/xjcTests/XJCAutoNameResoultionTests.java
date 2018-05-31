package xjcTests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Plugin;

import xjcTests.temp.AnnotationsAdder;
import xjcTests.temp.BeanNameManager;
import xjcTests.temp.NameBindingsManager;
import xjcTests.temp.NameResolutionPlugin;

//TODO: attempt to utilize the generated annotations with a retry... this may need multiple logic instances
public class XJCAutoNameResoultionTests extends AbstractXJCTest {
	private File workingDir = new File("C:/temp/xjc/bindingsTesting");

	@Test
	public void xjcRunsPlugin_test() throws Throwable {
		BeanNameManager nm = new BeanNameManager();
		File f = new File(resourceDir, "simplifiedPrecision.xsd");
		NameResolutionPlugin p = new NameResolutionPlugin(nm);

		Logic l1 = buildTestLogic(f, p, false);
		runTest(l1);

		NameBindingsManager m = p.getBindingsManger();

		// Assert.assertEquals(2, m.getSystemIds().size());
		String onlySystemId = m.getSystemIds().iterator().next();
		Map<Integer, String> bindings = m.getBindingsForSystemId(onlySystemId);

		// Assert.assertEquals(1, bindings.size());

		File modifiedFile = addAnnotationsToSchema(f, m);
		nm = new BeanNameManager();
		p = new NameResolutionPlugin(nm);

		l1 = buildTestLogic(modifiedFile, p, true);

		runTest(l1);

		m = p.getBindingsManger();
		Assert.assertEquals(0, m.getSystemIds().size());

	}

	private File addAnnotationsToSchema(File originalFile, NameBindingsManager m) throws Exception {
		File copiedFile = new File("C:/temp/xjc/bindingsTesting", originalFile.getName());
		refreshFile(copiedFile);
		try (OutputStream o = new FileOutputStream(copiedFile)) {
			Files.copy(originalFile.toPath(), o);
		}

		String orgSystemId = getSystemIDForFile(originalFile);
		Map<Integer, String> bindings = m.getBindingsForSystemId(orgSystemId);

		AnnotationsAdder adder = new AnnotationsAdder(copiedFile, bindings);
		adder.doIt();
		return copiedFile;
	}

	/**
	 * Convert a File instance into a system ID generted by XJC
	 * 
	 * @param f
	 * @return
	 */
	private String getSystemIDForFile(File f) {
		URI uri = f.toURI();
		return uri.toString();
	}

	private void refreshFile(File f) throws IOException {
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
	}

	/**
	 * Build the logic that will be used to populate the name resolution list...
	 * 
	 * @param p
	 * @return
	 */
	private Logic buildTestLogic(final File xsd, final NameResolutionPlugin p, final boolean genCode) {
		return new Logic() {
			@Override
			protected File getXsd() {
				return xsd;
			}

			@Override
			protected void loadPlugins(List<Plugin> plugins) {
				plugins.add(p);
			}

			@Override
			protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
				if (genCode) {
					if (!outputDir.exists()) {
						outputDir.mkdirs();
					}
					jModel.build(outputDir);
				}
			}

		};
	}

}
