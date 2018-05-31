package xjcTests.temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Map;

/**
 * Add annotations to xsd as prescribed lines.
 * 
 * This process will make a copy of the file in the same directory and recreate
 * the xsd with annotations defined in the NameBindingsManager
 * 
 * TODO: this logic needs to add the correct namespaces to the root....
 * 
 * TODO: this should attempt to use DOM4J to modify the XML
 * 
 */
public class AnnotationsAdder2 {

	private File f;
	private Map<Integer, String> bindings;

	// TODO: make work with the NameBindingsManager...
	public AnnotationsAdder2(File f, Map<Integer, String> bindings) {
		this.f = f;
		this.bindings = bindings;
	}

	public void doIt() throws Exception {
		File original = new File(f.getParent(), f.getName() + "_org");
		cloneFile(f, original);

		try (BufferedReader reader = new BufferedReader(new FileReader(original)); PrintWriter writer = new PrintWriter(f);) {
			String s;
			String bindingsStr;
			Integer lineCounter = 0;
			while ((s = reader.readLine()) != null) {
				if ((bindingsStr = bindings.get(lineCounter)) != null) {
					writer.print(bindingsStr);
				}
				writer.println(s);
				lineCounter++;
			}
		}
	}

	private void cloneFile(File src, File dest) throws Exception {
		resetFile(dest);

		try (OutputStream output = new FileOutputStream(dest)) {
			Files.copy(src.toPath(), output);
		}
	}

	private void resetFile(File f) throws Exception {
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
	}

}
