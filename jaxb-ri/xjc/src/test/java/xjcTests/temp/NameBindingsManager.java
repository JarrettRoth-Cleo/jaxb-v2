package xjcTests.temp;

import java.util.HashMap;
import java.util.Map;

public class NameBindingsManager {
	Map<String, Map<Integer, String>> allBindings = new HashMap<>();

	private Map<Integer, String> getBindingsForSystemId(String systemId) {
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
