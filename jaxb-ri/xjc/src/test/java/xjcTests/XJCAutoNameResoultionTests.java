package xjcTests;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;

import xjcTests.temp.BeanNameManager;
import xjcTests.temp.LineBindingsProvider;
import xjcTests.temp.NameResolutionBindingsProvider;

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

				BeanNameConflictPlugin ppPlugin = new BeanNameConflictPlugin(nm);
				plugins.add(ppPlugin);
			}

		};
		runTest(logic);

	}

	private class BeanNameConflictPlugin extends Plugin {
		private final BeanNameManager nameManager;

		BeanNameConflictPlugin(BeanNameManager nameManager) {
			this.nameManager = nameManager;
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

		@Override
		public void postProcessModel(Model m, ErrorHandler errorHandler) {
			super.postProcessModel(m, errorHandler);

			NameResolutionBindingsProvider provider = new NameResolutionBindingsProvider();
			Map<String, Map<Integer, String>> allBindings = new HashMap<>();

			for (CClassInfo info : m.beans().values()) {
				String fullName = info.fullName();
				if (nameManager.contains(fullName)) {
					fullName = nameManager.getUniqueFullName(fullName);
					String systemId = info.getLocator().getSystemId();
					Map<Integer, String> systemBindings = allBindings.get(systemId);
					if (systemBindings == null) {
						systemBindings = new HashMap<>();
						allBindings.put(systemId, systemBindings);
					}
					LineBindingsProvider bindings = provider.buildResolutions(nameManager.getShortNameFromFullName(fullName), info);
					bindings.addBindings(systemBindings);
				}
				nameManager.addNewBeanName(fullName);
			}

			// Delete this debugging nonsense:
			for (Entry<String, Map<Integer, String>> map : allBindings.entrySet()) {
				System.out.println(map.getKey() + ":");
				for (Entry<Integer, String> is : map.getValue().entrySet()) {
					System.out.println("\t" + is.getKey() + ": " + is.getValue());
				}
			}

		}

	}
}
