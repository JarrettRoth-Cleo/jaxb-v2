package xjcTests.temp;

import com.sun.tools.xjc.model.CClassInfo;

/**
 * Hopefully provide a container for a change that needs to be made during a
 * retry for name resolution
 *
 */
public class NameResolutionBindingsProvider {

	private final String newFullName;
	private final CClassInfo bean;

	public NameResolutionBindingsProvider(String newFullName, CClassInfo bean) {
		this.newFullName = newFullName;
		this.bean = bean;
	}

	// TODO: return a bindings request for a name change to the bean
	public String buildBindings() {

		return "";
	}

}
