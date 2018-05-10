package xjcTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.bind.api.impl.NameConverter;

/**
 * 
 * This class essentially defines how the wizard would use the XJC project
 *
 */
public class AbstractXJCTest {

	protected final File resourceDir = new File("src/test/resources");

	protected final File destRootDir = new File("C:/code/xjcFork/xsds/output");
	protected File outputDir;

	// Only leave the generated folders if the 'is.dev' property is set to true
	protected boolean shouldCleanUpAfter = false;// !Boolean.parseBoolean(System.getProperty("is.dev"));

	@Rule
	public TestName name = new TestName();

	@Before
	public void createOutputDir() {
		outputDir = new File(destRootDir, name.getMethodName());
	}

	/**
	 * Clean up the testing output folder. Java 7 Files could do this better
	 */
	@After
	public void cleanUp() {
		if (shouldCleanUpAfter) {
			cleanUpFolder(outputDir);
			outputDir.delete();
		}
	}

	private void cleanUpFolder(File f) {
		for (File file : f.listFiles()) {
			if (file.isDirectory()) {
				cleanUpFolder(file);
				file.delete();
			} else {
				file.delete();
			}
		}
	}

	protected void runTest(Logic l) throws Throwable {

		// System.out.println(outputDir.getAbsolutePath());
		SchemaCompiler compiler = XJC.createSchemaCompiler();

		Options ops = compiler.getOptions();

		addCustomNameCovnerter(ops);
		l.addOptions(ops);

		for (Plugin plugin : getPlugins(l)) {
			ops.activePlugins.add(plugin);
		}

		compiler.setErrorListener(new TestingErrorListener());

        InputSource inputSource = getInputSource(l.getXsd());

        for (File f : getBindings(l)) {
            compiler.getOptions().addBindFile(getInputSource(f));
        }

        compiler.parseSchema(inputSource);

        S2JJAXBModel model = compiler.bind();
        Assert.assertNotNull("model is null", model);

        JCodeModel jModel = model.generateCode(null, null);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        l.handleJCodeModel(jModel, outputDir);
	}

	private InputSource getInputSource(File file) {
		InputSource inputSource = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			inputSource = new InputSource(fileInputStream);
			inputSource.setSystemId(file.toURI().toString());
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		return inputSource;
	}

	private List<Plugin> getPlugins(Logic l) {
		List<Plugin> plugins = new ArrayList<Plugin>();
		l.loadPlugins(plugins);
		return plugins;
	}

	private List<File> getBindings(Logic l) {
		List<File> bindings = new ArrayList<File>();
		l.loadBindings(bindings);
		return bindings;
	}

	private void addCustomNameCovnerter(Options ops) {
		try {
			ops.setNameConverter(getTestingNameConverter(), getTestingNameConverterPlugin());
		} catch (BadCommandLineException e) {
			Assert.fail(e.getMessage());
		}
	}

	protected NameConverter getTestingNameConverter() {
		return new InternalNameConverter();
	}

	protected Plugin getTestingNameConverterPlugin() {
		return new LinkedPlugin();
	}

	private class TestingErrorListener implements ErrorListener {
		@Override
		public void error(SAXParseException exception) {
			System.out.println("ERROR: " + exception.getLocalizedMessage());
			exception.printStackTrace();
		}

		@Override
		public void fatalError(SAXParseException exception) {
			System.out.println("FATAL ERROR: " + exception.getLocalizedMessage());
		}

		@Override
		public void warning(SAXParseException exception) {
			System.out.println("WARNING: " + exception.getLocalizedMessage());
		}

		@Override
		public void info(SAXParseException exception) {
			System.out.println("INFO: " + exception.getLocalizedMessage());
		}
	}

	private class LinkedPlugin extends Plugin {

		@Override
		public String getOptionName() {
			return "linkedPlugin";
		}

		@Override
		public String getUsage() {
			return "EVERYWHERE";
		}

		@Override
		public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
			// always enable for internal calls.
			return true;
		}

	}

	private class InternalNameConverter extends com.sun.xml.bind.api.impl.Standard {
		@Override
		public String toClassName(String s) {
			return toMixedCaseName(toWordList(s), true);
		}

		@Override
		public String toVariableName(String s) {
			return s;
		}

		@Override
		public String toInterfaceName(String token) {
			return toClassName(token);
		}

		/**
		 * Override the base functionality of the capitalize method to return
		 * the same value so it matches the XSD closer
		 */
		@Override
		public String capitalize(String w) {
			return w;
		}
	}

	protected abstract class Logic {
		protected abstract File getXsd();

		protected void addOptions(Options ops) {
		}

		protected void loadBindings(List<File> files) {
		}

		protected void loadPlugins(List<Plugin> plugins) {
		}

		protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
			// no-op, consider test passing if it made it this far.
		}
	}

}
