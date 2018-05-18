package xjcTests.modelModifications;

import java.util.List;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Model;

public class ChoiceModInitializer {

	private final BeanNameManager nameManager;

	public ChoiceModInitializer(Model m, BeanNameManager nameManager) {
		this.nameManager = nameManager;
		init(m);
	}

	private void init(Model m) {
		for (CClassInfo info : m.beans().values()) {
			nameManager.addBean(info);
		}
	}

	public ChoiceModHandler intitialize(CPropertyInfo info) {
		String baseName = info.getName(true) + "_Type";
		String typeName = getUniqueNameFromList(baseName, nameManager.getBeanShortNames());

		String typeFqn = ((CClassInfo) info.parent()).fullName() + "." + typeName;
		nameManager.addNewBeanName(typeFqn);
		return new ChoiceModHandler(info, typeName);
	}

	/**
	 * Build a unique name using the baseName + counter
	 * 
	 * TODO: use NamingUtil in Clarify
	 * 
	 * @param baseName
	 * @param existingNames
	 * @return
	 */
	private String getUniqueNameFromList(String baseName, List<String> existingNames) {
		int counter = 1;
		String modifiedName = baseName;
		// TODO: make case insensitive
		while (existingNames.contains(modifiedName)) {
			counter++;
			modifiedName = baseName + Integer.toString(counter);
		}
		return modifiedName;
	}

}
