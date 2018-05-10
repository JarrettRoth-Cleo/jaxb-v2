package xjcTests;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Ignore;
import org.junit.Test;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

public class XJCRestrictionTest extends AbstractXJCTest {

	@Ignore
	@Test
	public void restrictionIssue() throws Throwable {
		runTest(new Logic() {

			@Override
			public File getXsd() {
				return new File(resourceDir, "JustRestrictionIssue.xsd");
			}
		});
	}

	// @Ignore
	@Test
	public void fullRestrictionIssue() throws Throwable {
		runTest(new Logic() {

			@Override
			public File getXsd() {
				return new File("C:/code/xjcFork/xsds/RestrictionIssue", "Party.xsd");
			}
		});
	}

	@Ignore
	@Test
	public void codeWriterTests() throws Throwable {
		runTest(new Logic() {
			@Override
			public File getXsd() {
				return new File(resourceDir, "JustRestrictionIssue.xsd");
			}

			// @Override
			// protected void writeCode(JCodeModel jModel, File outputDir)
			// throws IOException {
			// jModel.build(new TestingCodeWriter());
			// }
		});
	}

	@Ignore

	@Test
	public void testFixedValue() throws Throwable {
		runTest(new Logic() {

			@Override
			protected File getXsd() {
				return new File(resourceDir, "NorwegiaExample.xsd");
			}

		});
	}

	private class TestingCodeWriter extends CodeWriter {

		@Override
		public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub

		}

	}

}
