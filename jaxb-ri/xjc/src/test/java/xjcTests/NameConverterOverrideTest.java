package xjcTests;

import java.io.File;

import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.bind.api.impl.NameConverterProvider.Standard;

public class NameConverterOverrideTest extends AbstractXJCTest {

	@Test
	public void testOverrideNameConverter() throws Throwable {

		runTest(new Logic() {

			@Override
			protected File getXsd() {
				// can be schema
				return new File(resourceDir, "Mixed.xsd");
			}

			@Override
			protected void addOptions(Options ops) {
				try {
					ops.setNameConverter(new InternalNameConverter(), new LinkedPlugin());
				} catch (BadCommandLineException e) {
					// TODO: what is this?
					e.printStackTrace();
				}
			}

		});
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

	private class InternalNameConverter extends Standard {
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

		@Override
		public String capitalize(String w) {
			return w;
		}
	}

}
