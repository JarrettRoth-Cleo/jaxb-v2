package xjcTests.temp;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;

public class NameResolutionPlugin extends Plugin {
	private final BeanNameManager nameManager;
	private NameBindingsManager bindingsManager;

	public NameResolutionPlugin(BeanNameManager nameManager) {
		this.nameManager = nameManager;
		bindingsManager = new NameBindingsManager();
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

		for (CClassInfo info : m.beans().values()) {
			String fullName = info.fullName();
			if (nameManager.contains(fullName)) {
				fullName = nameManager.getUniqueFullName(fullName);
				String systemId = info.getLocator().getSystemId();
				LineBindingsProvider bindings = provider.buildResolutions(nameManager.getShortNameFromFullName(fullName), info);

				bindingsManager.addBindings(systemId, bindings);
			}
			nameManager.addNewBeanName(fullName);
		}
	}

	public NameBindingsManager getBindingsManger() {
		return bindingsManager;
	}

}