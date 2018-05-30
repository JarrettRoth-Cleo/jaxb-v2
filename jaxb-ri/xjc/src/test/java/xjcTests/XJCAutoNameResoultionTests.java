package xjcTests;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;

import xjcTests.temp.BeanNameManager;

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

			for (CClassInfo info : m.beans().values()) {
				String fullName = info.fullName();
				if (nameManager.contains(fullName)) {
					// TODO: add some kind of resolution logic
					fullName = nameManager.getUniqueFullName(fullName);
				}
				nameManager.addNewBeanName(fullName);
			}

			// TODO: delete this debugging code.
			for (String s : nameManager.getBeanFullNames()) {
				System.out.println(s);
			}
		}

	}
}
