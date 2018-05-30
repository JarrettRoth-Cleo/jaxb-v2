package xjcTests;

import java.io.File;

import org.junit.Test;

public class XJCMixedTest extends AbstractXJCTest {

	@Test
	public void simpleMixedTest() throws Throwable {
		runTest(new Logic() {

			@Override
			protected File getXsd() {
				return new File(resourceDir, "Mixed.xsd");
			}

		});
	}
}
