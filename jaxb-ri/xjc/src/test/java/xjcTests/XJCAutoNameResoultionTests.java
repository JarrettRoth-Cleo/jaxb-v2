package xjcTests;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.sun.tools.xjc.Plugin;

import xjcTests.temp.BeanNameManager;
import xjcTests.temp.NameBindingsManager;
import xjcTests.temp.NameResolutionPlugin;

//TODO: attempt to utilize the generated annotations with a retry... this may need multiple logic instances
public class XJCAutoNameResoultionTests extends AbstractXJCTest {
	@Test
	public void xjcRunsPlugin_test() throws Throwable {
		BeanNameManager nm = new BeanNameManager();
		File f = new File(resourceDir, "simplifiedPrecision.xsd");
		NameResolutionPlugin p = new NameResolutionPlugin(nm);

		Logic l1 = getSetupLogic(f, p);
		runTest(l1);

		NameBindingsManager m = p.getBindingsManger();

		Assert.assertEquals(1, m.getSystemIds().size());
		String onlySystemId = m.getSystemIds().iterator().next();
		Map<Integer, String> bindings = m.getBindingsForSystemId(onlySystemId);

		Assert.assertEquals(2, bindings.size());
	}

	/**
	 * Build the logic that will be used to populate the name resolution list...
	 * 
	 * @param p
	 * @return
	 */
	private Logic getSetupLogic(final File xsd, final NameResolutionPlugin p) {
		return new Logic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "simplifiedPrecision.xsd");
			}

			@Override
			protected void loadPlugins(List<Plugin> plugins) {
				plugins.add(p);
			}

		};
	}

}
