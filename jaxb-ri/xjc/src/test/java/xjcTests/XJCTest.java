package xjcTests;
import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class XJCTest extends AbstractXJCTest {

    @Ignore
    @Test
    public void simpleTest() throws Throwable{
        runTest(new Logic(){

			@Override
			public File getXsd() {
				return (new File(resourceDir,"ImageAttachment.xsd"));
			}    		
    	});
    }

    
    @Ignore
    @Test
    public void shouldFailTest() throws Throwable{
        runTest(new Logic(){

			@Override
			public File getXsd() {
				return new File(resourceDir,"EADS_INVOICING_JUST_PRECISION.XSD");
			}

    		
    	});
    }

    @Ignore
    @Test
    public void shouldFailWithBindings3() throws Throwable{

        runTest(new Logic(){

			@Override
			public File getXsd() {
				return new File(resourceDir,"EADS_INVOICING_JUST_PRECISION.XSD");
			}


			@Override
			public void loadBindings(List<File> files) {
				files.add(new File(resourceDir,"Just_precision_bindings.xjb"));
				
			}

    		
    	});
    }

}
