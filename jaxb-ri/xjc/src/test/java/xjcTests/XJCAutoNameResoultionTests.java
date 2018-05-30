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
				XJCPostProcessPlugin ppPlugin = new XJCPostProcessPlugin();
				plugins.add(ppPlugin);
			}

		};
		runTest(logic);

	}

	private class XJCPostProcessPlugin extends Plugin {
		BeanNameManager nameManager = new BeanNameManager();

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
				nameManager.addBean(info);
			}
		}

		private void init(Model m) {
			for (CClassInfo info : m.beans().values()) {
				nameManager.addBean(info);
			}
		}
	}
}
