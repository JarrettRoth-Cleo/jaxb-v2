package xjcTests;

import java.io.File;
import java.util.List;

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

	@Ignore
	@Test
	public void shouldFailWithBindings3() throws Throwable {

		runTest(new Logic(false) {

			@Override
			public File getXsd() {
				return new File(resourceDir, "EADS_INVOICING_JUST_PRECISION.XSD");
			}

			@Override
			public void loadBindings(List<File> files) {
				files.add(new File(resourceDir, "Just_precision_bindings.xjb"));
			}

		});
	}

}
