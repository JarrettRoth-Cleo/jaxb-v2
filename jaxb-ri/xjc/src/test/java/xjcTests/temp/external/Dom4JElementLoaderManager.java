package xjcTests.temp.external;

import java.util.HashMap;
import java.util.Map;

import xjcTests.temp.Dom4JElementLoader;

public class Dom4JElementLoaderManager {
	private Map<String, Dom4JElementLoader> elementLoaders;

	public Dom4JElementLoaderManager() {
		elementLoaders = new HashMap<>();
	}
}
