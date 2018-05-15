package xjcTests;

import java.io.File;

import org.junit.Test;

public class NameConverterOverrideTest extends AbstractXJCTest {

	@Test
	public void testOverrideNameConverter() throws Throwable {

		runTest(new Logic() {

			@Override
			protected File getXsd() {
				// can be any schema
				return new File(resourceDir, "Mixed.xsd");
			}

		});
	}

}
