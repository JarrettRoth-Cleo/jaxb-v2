package xjcTests;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.binding.StringFormatter;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.Ignore;
import org.junit.Test;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class XJCTest extends AbstractXJCTest {

	@Ignore
	@Test
	public void simpleSuccess_test() throws Throwable {
		runTest(new Logic(false) {

			@Override
			public File getXsd() {
				return (new File(resourceDir, "simpleTest.xsd"));
			}
		});
	}

	@Ignore
	@Test
	public void precisionIssueCausesFailure_test() throws Throwable {
		runTest(new Logic(false) {

			@Override
			public File getXsd() {
				return new File(resourceDir, "simplifiedPrecision.xsd");
			}

		});
	}

	@Test
	public void precisionIssueFixedWithBindings_test() throws Throwable{
		runTest(new Logic(false) {
			@Override
			protected File getXsd() {
				return new File(resourceDir, "simplifiedPrecision.xsd");
			}

			@Override
			public void loadBindings(List<File> files) {
				files.add(new File(resourceDir, "simplifiedBindings.xjb"));
			}
		});
	}

	@Test
    public void precisionIssueFixedWithManuallyGeneratedBindingsFile_test() throws Throwable{
	    runTest(new Logic(false){
            @Override
            protected File getXsd() {
                return new File(resourceDir, "simplifiedPrecision.xsd");
            }

            @Override
            protected void loadBindings(List<File> files) {

                try {
                    FileWriter writer = new FileWriter("src/test/resources/genBindings.xjb");
                    BufferedWriter bw = new BufferedWriter(writer);
                    bw.write(generateXjbHeader("simplifiedPrecision"));
                    bw.write(generateXjbPropertyBinding("Precision2","Precision","semiramis"));
                    bw.write(generateXjbClassBinding("Precision2", "Precision", "semiramis"));
                    bw.write(generateXjbEnd());
                    bw.close();
                    files.add(new File(resourceDir, "genBindings.xjb"));
                } catch (IOException x) {
                    System.err.println(x);
                }

            }
        });

    }
	/**
	 * Will generate the beginning portion (opening) of a  xml based binding file.
	 * @param schemaLocation - String containing the location from current operating directory to the xsd schema
	 * @return - String containing the beginning parts of the binding file, without the ending.
	 */
	private String generateXjbHeader(String schemaLocation){
	    String versionEncoding = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String bindingHeader = String.format("<jxb:bindings schemaLocation=\"%s.xsd\" version=\"2.1\" xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">", schemaLocation);
        return String.format("%s \n%s \n",versionEncoding, bindingHeader);
    }

	/**
	 * Will generate a property portion of the binding file. This must be run for each property which causes a problem/
	 * collision within the schema.
	 * @param correctedName - String new name of the property that will resolve the collision.
	 * @param currentName - String name of the property that is causing the issue.
	 * @param namespace - the namespace of the property
	 * @return String - containing one property binding section to resolve a single collision.
	 */
	private String generateXjbPropertyBinding(String correctedName, String currentName, String namespace){
		String xPath = String.format("//*[local-name()='element' and @name='%s']/*[local-name()='complexType']/*[local-name()='sequence']/*[local-name()='element' and @name='%s']",namespace, currentName);
		String nodeBeginning = String.format("\t<jxb:bindings node=\"%s\">\n", xPath);
		String property = String.format("\t\t<jxb:property name=\"%s\"/>\n", correctedName);
		String nodeEnding = "\t</jxb:bindings>\n";

	    return String.format("%s%s%s",nodeBeginning, property, nodeEnding);
    }

    /**
     * Generates another property portion of the binding file, but with the addition of a class property and name attribute.
     * @param correctedName - String new name of the property that will resolve the collision.
     * @param currentName - String name of the property that is causing the issue.
     * @param namespace - String the namespace of the property
     * @return String - result containing the formatted xml version of the binding section.
     */
    private String generateXjbClassBinding(String correctedName, String currentName, String namespace){
        String xPath = String.format(
                "//*[local-name()='element' and @name='%s']" +
                "/*[local-name()='complexType']" +
                "/*[local-name()='sequence']"+
                "/*[local-name()='element' and @name='%s']"+
                "/*[local-name()='complexType']",namespace, currentName);
        String nodeBeginning = String.format("\t<jxb:bindings node=\"%s\">\n", xPath);
        String propertyName = String.format("\t\t<jxb:property name=\"%s\"/>\n", correctedName);
        String className = String.format("\t\t<jxb:class name=\"%s\"/>\n", correctedName);
        String nodeEnding = "\t</jxb:bindings>\n";

        return String.format("%s%s%s%s",nodeBeginning, propertyName, className, nodeEnding);

    }

    /**
     * Simple helper method to generate the final closing tags of an xjb file.
     * @return - String containing the ending xml.
     */
    private String generateXjbEnd(){
	    return "</jxb:bindings>";
    }
}

