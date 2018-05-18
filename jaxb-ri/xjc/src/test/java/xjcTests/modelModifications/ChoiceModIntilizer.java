package xjcTests.modelModifications;

import java.util.ArrayList;
import java.util.List;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Model;

public class ChoiceModIntilizer {

	// TODO: this might need to be moved to another class so other initializers
	// can use it
	private List<String> existingBeanNames = new ArrayList<String>();

	public ChoiceModIntilizer(Model m) {
		// Cache the model's existing names
		for (CClassInfo info : m.beans().values()) {
			// TODO: is just the Class name enough?
			existingBeanNames.add(info.shortName);
		}
	}

	public ChoiceModHandler intitialize(CPropertyInfo info) {
		String baseName = info.getName(true) + "_Type";
		String typeName = getUniqueNameFromList(baseName, existingBeanNames);

		existingBeanNames.add(typeName);
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
		int counter = 0;
		String modifiedName = baseName;
		// TODO: make case insensitive
		while (existingNames.contains(modifiedName)) {
			// TODO: this is wrong
			modifiedName += ++counter;
		}
		return modifiedName;
	}

}
