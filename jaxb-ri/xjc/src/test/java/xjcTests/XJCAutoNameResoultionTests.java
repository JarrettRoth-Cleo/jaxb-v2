package xjcTests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.InputSource;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Plugin;

import xjcTests.temp.BeanNameManager;
import xjcTests.temp.autoNameResolution.BindingsProvider;
import xjcTests.temp.autoNameResolution.ExternalBindingsBuilder;
import xjcTests.temp.autoNameResolution.NameBindingsManager;
import xjcTests.temp.autoNameResolution.NameResolutionPlugin;

//TODO: attempt to utilize the generated annotations with a retry... this may need multiple logic instances
public class XJCAutoNameResoultionTests extends AbstractXJCTest {

	@Test
	@Ignore
	public void xjcRunsPlugin_test() throws Throwable {
		BeanNameManager nm = new BeanNameManager();
		final File f = new File(resourceDir, "simplifiedPrecision.xsd");
		NameResolutionPlugin p = new NameResolutionPlugin(nm);

		Logic l1 = buildTestLogic(f, p, false);
		runTest(l1);

		NameBindingsManager m = p.getBindingsManger();

		// Assert.assertEquals(2, m.getSystemIds().size());
		String onlySystemId = m.getSystemIds().iterator().next();
		List<BindingsProvider> bindings = m.getBindingsForSystemId(onlySystemId).getBindings();

		// Assert.assertEquals(1, bindings.size());

		final List<InputSource> bindingSources = buildBindings(f, m);
		nm = new BeanNameManager();
		final NameResolutionPlugin another = new NameResolutionPlugin(nm);

		l1 = new Logic() {

			@Override
			protected File getXsd() {
				return f;
			}

			@Override
			protected void loadPlugins(List<Plugin> plugins) {
				plugins.add(another);
			}

			@Override
			protected void loadBindingSources(List<InputSource> sources) {
				sources.addAll(bindingSources);
			}

			@Override
			protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}
				jModel.build(outputDir);
			}

		};

		runTest(l1);

		m = another.getBindingsManger();
		Assert.assertEquals(0, m.getSystemIds().size());

	}

	private List<InputSource> buildBindings(File originalFile, NameBindingsManager m) throws Exception {
		ExternalBindingsBuilder ebb = new ExternalBindingsBuilder(m);
		List<InputSource> bindings = ebb.buildDoc();
		return bindings;
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
