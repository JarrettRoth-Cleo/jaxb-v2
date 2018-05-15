package xjcTests;

import java.io.File;

import org.junit.Test;

public class XJCChoiceTest extends AbstractXJCTest {

	@Test
	public void runSimpleChoiceTest() throws Throwable {
		runTest(new Logic() {

			@Override
			protected File getXsd() {
				return new File(resourceDir, "ChoiceExample.xsd");
			}

		});
	}
}
