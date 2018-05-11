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
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

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

	protected void runTest(Logic logic) throws Throwable {
		InputSource inputSource = getInputSource(logic.getXsd());
		SchemaCompiler compiler = getInitializedSchemaCompiler(inputSource, logic);
        S2JJAXBModel model = compiler.bind();
        Assert.assertNotNull("model is null", model);

        if(logic.shouldGenerateFiles()){
        	generateFiles(model, logic);
		}

	}
	private void generateFiles(S2JJAXBModel model, Logic logic){
		JCodeModel jModel = model.generateCode(null, null);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		try {
            logic.handleJCodeModel(jModel, outputDir);
        } catch (IOException e){
		    e.printStackTrace();
        }
	}

	private SchemaCompiler getInitializedSchemaCompiler(InputSource xsd,Logic logic){
		SchemaCompiler compiler = XJC.createSchemaCompiler();
		compiler.setErrorListener(new TestingErrorListener());
		//TODO: THIS is how you activate a plugin for post porcessing modeling...
        for (Plugin plugin : getPlugins(logic)){
			compiler.getOptions().activePlugins.add(plugin);
		}
		for(File f : getBindings(logic)){
			compiler.getOptions().addBindFile(getInputSource(f));
		}
		compiler.parseSchema(xsd);
		return compiler;
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

	protected abstract class Logic {
	    private boolean shouldGenerateFiles;

	    public Logic(boolean shouldGenerateFiles){
	        this.shouldGenerateFiles = shouldGenerateFiles;
        }

		protected abstract File getXsd();

		protected void loadBindings(List<File> files) {
		}

		protected void loadPlugins(List<Plugin> plugins) {
		}

		protected void handleJCodeModel(JCodeModel jModel, File outputDir) throws IOException {
			// no-op, consider test passing if it made it this far.
		}
		protected boolean shouldGenerateFiles(){
		    return this.shouldGenerateFiles;
        }
	}

}
