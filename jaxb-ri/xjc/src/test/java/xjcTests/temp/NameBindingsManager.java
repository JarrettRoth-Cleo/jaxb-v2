package xjcTests.temp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NameBindingsManager {
	Map<String, Map<Integer, String>> allBindings = new HashMap<>();

	public Set<String> getSystemIds() {
		return allBindings.keySet();
	}

	public Map<Integer, String> getBindingsForSystemId(String systemId) {
		Map<Integer, String> bindings = allBindings.get(systemId);
		if (bindings == null) {
			bindings = new HashMap<>();
			allBindings.put(systemId, bindings);
		}
		return bindings;
	}

	public void addBindings(String systemID, LineBindingsProvider provider) {
		provider.addBindings(getBindingsForSystemId(systemID));
	}

}
