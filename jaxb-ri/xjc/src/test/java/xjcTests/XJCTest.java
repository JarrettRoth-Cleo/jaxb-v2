package xjcTests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.binding.StringFormatter;
import org.junit.Ignore;
import org.junit.Test;

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

	private String generateXjbHeader(String schemaLocation){
	    String versionEncoding = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String bindingHeader = String.format("<jxb:bindings schemaLocation=\"%s.xsd\" version=\"2.1\" xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">", schemaLocation);
        return String.format("%s \n %s ",versionEncoding, bindingHeader);
    }
    private String generateBinding(String element){
	    return String.format("");
    }
    private String generateXjbEnd(){
	    return "</jxb:bindings>";
    }
}

