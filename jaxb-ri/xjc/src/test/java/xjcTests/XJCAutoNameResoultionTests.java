package xjcTests;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.sun.tools.xjc.Plugin;

import xjcTests.temp.BeanNameManager;
import xjcTests.temp.NameResolutionPlugin;

//TODO: attempt to utilize the generated annotations with a retry... this may need multiple logic instances
public class XJCAutoNameResoultionTests extends AbstractXJCTest {
	@Test
	public void xjcRunsPlugin_test() throws Throwable {
		Logic logic = new Logic() {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "simplifiedPrecision.xsd");
			}

			@Override
			protected void loadPlugins(List<Plugin> plugins) {
				BeanNameManager nm = new BeanNameManager();

				NameResolutionPlugin ppPlugin = new NameResolutionPlugin(nm);
				plugins.add(ppPlugin);
			}

		};
		runTest(logic);

	}

}
