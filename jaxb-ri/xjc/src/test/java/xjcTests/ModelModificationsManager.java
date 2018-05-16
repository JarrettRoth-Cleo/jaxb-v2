package xjcTests;

import java.util.ArrayList;
import java.util.List;

import com.sun.tools.xjc.model.CPropertyInfo;

public class ModelModificationsManager {

	private final List<CPropertyInfo> choices = new ArrayList<CPropertyInfo>();

	public void addChoice(CPropertyInfo propInfo) {
		choices.add(propInfo);
	}

	public List<CPropertyInfo> getChoices() {
		return choices;
	}

}
