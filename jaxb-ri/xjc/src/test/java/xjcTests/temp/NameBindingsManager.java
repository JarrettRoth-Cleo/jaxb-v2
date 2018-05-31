package xjcTests.temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NameBindingsManager {
	Map<String, List<LineBindingsProvider>> allBindings = new HashMap<>();

	public Set<String> getSystemIds() {
		return allBindings.keySet();
	}

	public List<LineBindingsProvider> getBindingsForSystemId(String systemId) {
		List<LineBindingsProvider> bindings = allBindings.get(systemId);
		if (bindings == null) {
			bindings = new ArrayList<>();
			allBindings.put(systemId, bindings);
		}
		return bindings;
	}

	public void addBindings(String systemId, LineBindingsProvider bindings) {
		getBindingsForSystemId(systemId).add(bindings);
	}

}
