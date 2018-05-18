package xjcTests.modelModifications;

import java.util.ArrayList;
import java.util.List;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Model;

public class ChoiceModIntilizer {

	private List<String> existingBeanNames = new ArrayList<String>();

	public ChoiceModIntilizer(Model m) {
		// Cache the model's existing names
		for (CClassInfo info : m.beans().values()) {
			existingBeanNames.add(info.fullName());
		}
	}

	public ChoiceModHandler intitialize(CPropertyInfo info) {
		String baseName = info.getName(true) + "_Type";
		String typeName = getUniqueInterfaceName(baseName);

		existingBeanNames.add(typeName);
		return new ChoiceModHandler(info, typeName);
	}

	// TODO: use naming util
	private String getUniqueInterfaceName(String baseName) {
		return getUniqueNameFromList(baseName, existingBeanNames);
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
		int counter = 0;
		String modifiedName = baseName;
		// TODO: make case insensitive
		while (existingNames.contains(modifiedName)) {
			modifiedName += ++counter;
		}
		return modifiedName;
	}

}
