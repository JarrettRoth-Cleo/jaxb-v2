package xjcTests.temp.autoNameResolution;

import java.util.HashMap;
import java.util.Map;

public class Dom4JElementLoaderManager {
	private Map<String, Dom4JElementLoader> elementLoaders;

	public Dom4JElementLoaderManager() {
		elementLoaders = new HashMap<>();
	}
}
