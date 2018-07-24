package xjcTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.util.FileUtils;
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

import xjcTests.CtBuilders.TestingBaseClassManager;
import xjcTests.CtBuilders.TestingCTBuilderFactory;
import xjcTests.ref.TestingRefFactory;

/**
 * 
 * This class essentially defines how the wizard would use the XJC project
 *
 */
public class AbstractXJCTest {

	protected final File resourceDir = new File("src/test/resources");
	protected final File destRootDir = new File("src/test/output");
	protected File outputDir;

	// Only leave the generated folders if the 'is.dev' property is set to true
	protected boolean shouldCleanUpAfter = !Boolean.parseBoolean(System.getProperty("is.dev"));

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
	public void tearDown() {
		if (shouldCleanUpAfter) {
			FileUtils.delete(outputDir);
			File f = new File("src/test/resources/genBindings.xjb");
			FileUtils.delete(f);
		}
	}

	protected void runTest(Logic logic) {
		InputSource inputSource = getInputSource(logic.getXsd());
		SchemaCompiler compiler = getInitializedSchemaCompiler(inputSource, logic);
		S2JJAXBModel model = compiler.bind();

		logic.handleS2JJAXBModel(model);

		JCodeModel jcodeModel = model.generateCode(null, null);
		try {
			logic.handleJCodeModel(jcodeModel, outputDir);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	private SchemaCompiler getInitializedSchemaCompiler(InputSource xsd, Logic logic) {
		SchemaCompiler compiler = XJC.createSchemaCompiler();
		compiler.setErrorListener(new TestingErrorListener());

		for (Plugin plugin : getPlugins(logic)) {
			compiler.getOptions().activePlugins.add(plugin);
		}
		for (InputSource f : getBindingSources(logic)) {
			compiler.getOptions().addBindFile(f);
		}

		addCustomTestingOptions(compiler.getOptions());

		compiler.parseSchema(xsd);
		return compiler;
	}

	private List<Plugin> getPlugins(Logic l) {
		List<Plugin> plugins = new ArrayList<Plugin>();
		l.loadPlugins(plugins);
		return plugins;
	}

	private List<InputSource> getBindingSources(Logic l) {
		List<InputSource> sources = new ArrayList<>();
		l.loadBindingSources(sources);
		for (File f : getBindings(l)) {
			sources.add(getInputSource(f));
		}
		return sources;
	}

	private List<File> getBindings(Logic l) {
		List<File> bindings = new ArrayList<File>();
		l.loadBindingFiles(bindings);
		return bindings;
	}

	private InputSource getInputSource(File file) {
		InputSource inputSource = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			inputSource = new InputSource(fileInputStream);
			inputSource.setSystemId(file.toURI().toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return inputSource;
	}

	private void addCustomTestingOptions(Options ops) {
		try {
			ops.setNameConverter(getTestingNameConverter(), getTestingNameConverterPlugin());
			TestingBaseClassManager manager = new TestingBaseClassManager();
			ops.ctBuilderFactory = new TestingCTBuilderFactory(manager);
			ops.refFactory = new TestingRefFactory(manager);
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
		private List<String> errorItems = new ArrayList<>();

		public List<String> getErrorItems() {
			return this.errorItems;
		}

		public void addErrorItems(String item) {
			this.errorItems.add(item);
		}

		@Override
		public void error(SAXParseException exception) {
			System.out.println("ERROR: " + exception.getLocalizedMessage());
			String errorItem = exception.getLocalizedMessage();
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
			return "tests";
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

		// maintain the underscores...
		@Override
		protected boolean isPunct(char c) {
			return super.isPunct(c) && c != '_';
		}
	}

	protected abstract class Logic {

		protected abstract File getXsd();

		protected void loadBindingFiles(List<File> files) {
		}

		protected void loadBindingSources(List<InputSource> sources) {

		}

		protected void loadPlugins(List<Plugin> plugins) {
		}

		protected void handleS2JJAXBModel(S2JJAXBModel model) {

		}

		protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
			// no-op, consider test passing if it made it this far.
		}

	}

}
