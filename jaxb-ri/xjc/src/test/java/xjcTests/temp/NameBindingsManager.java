package xjcTests.temp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xjcTests.temp.Dom4JElementLoader.DomLoadingException;

public class NameBindingsManager {
	Map<String, BindingsContainer> allBindings = new HashMap<>();

	public Set<String> getSystemIds() {
		return allBindings.keySet();
	}

	public Set<Map.Entry<String, BindingsContainer>> getAllBindings() {
		return allBindings.entrySet();
	}

	public BindingsContainer getBindingsForSystemId(String systemId) {
		BindingsContainer bindings = allBindings.get(systemId);
		if (bindings == null) {
			bindings = new BindingsContainer(loadFileFromSystemId(systemId));
			allBindings.put(systemId, bindings);
		}
		return bindings;
	}

	private File loadFileFromSystemId(String systemId) {
		File f = null;
		try {
			f = new File(new URL(systemId).toURI());
		} catch (MalformedURLException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}

	public void addBindings(String systemId, LineBindingsProvider bindings) {
		getBindingsForSystemId(systemId).addBindings(bindings);
	}

	public static class BindingsContainer {
		private final File forFile;
		private final List<LineBindingsProvider> bindings;
		private Dom4JElementLoader loader;

		private BindingsContainer(File f) {
			this.forFile = f;
			bindings = new ArrayList<>();
		}

		private void addBindings(LineBindingsProvider p) {
			bindings.add(p);
		}

		public List<LineBindingsProvider> getBindings() {
			return bindings;
		}

		/**
		 * Lazy-load the Dom4JElementLoader
		 * 
		 * @return
		 */
		public Dom4JElementLoader getElementLoader() {
			if (loader == null) {
				try {
					loader = new Dom4JElementLoader(forFile);
				} catch (DomLoadingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return loader;
		}
	}

}
