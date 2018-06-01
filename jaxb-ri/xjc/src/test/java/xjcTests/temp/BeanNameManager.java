package xjcTests.temp;

import java.util.ArrayList;
import java.util.List;

import com.sun.tools.xjc.model.CClassInfo;

public class BeanNameManager {

	private final List<String> beanShortNames = new ArrayList<String>();
	private final List<String> beanFullNames = new ArrayList<String>();

	public List<String> getBeanShortNames() {
		return beanShortNames;
	}

	public List<String> getBeanFullNames() {
		return beanFullNames;
	}

	/**
	 * Add a bean to track through the CClassInfo interface
	 * 
	 * @param info
	 */
	public void addBean(CClassInfo info) {
		beanShortNames.add(info.shortName);
		beanFullNames.add(info.fullName());
	}

	/**
	 * Add a bean to track through the type's FQN
	 * 
	 * @param typeFqn
	 */
	public void addNewBeanName(String typeFqn) {
		String shortName = getShortNameFromFullName(typeFqn);

		beanShortNames.add(shortName);
		beanFullNames.add(typeFqn);
	}

	// TODO: this only checks if there are same fqns, this may need to change to
	// checking names in the parent(s)
	public boolean contains(String fullName) {
		for (String s : beanFullNames) {
			if (s.equalsIgnoreCase(fullName)) {
				return true;
			}
		}
		return false;
	}

	public String getUniqueFullName(String originalFullName) {
		String modName = originalFullName;
		int c = 1;

		while (contains(modName)) {
			modName = originalFullName + (++c);
		}
		return modName;
	}

	public String getShortNameFromFullName(String fullName) {
		String[] nameParts = fullName.split("\\.");
		String shortName = nameParts[nameParts.length - 1];
		return shortName;
	}

}
