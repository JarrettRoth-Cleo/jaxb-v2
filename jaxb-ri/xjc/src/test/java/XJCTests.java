import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.InputSource;
import com.sun.tools.xjc.api.S2JJAXBModel;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileInputStream;

public class XJCTests {
    private final File rootDir = new File("C:\\code\\xjcFork\\xsds");
    private final File destRootDir = new File("C:/code/xjcFork/xsds/output");

    @Ignore
    @Test
    public void simpleTest() throws Throwable{
        runTest(new File(rootDir,"ImageAttachment.xsd"), new File(destRootDir,"simple"));
        Assert.assertTrue(true);
    }

    @Test
    public void shouldFailTest() throws Throwable{
        System.out.println(rootDir.getAbsolutePath());
        runTest(new File(rootDir,"EADS_INVOICING_JUST_PRECISION.XSD"),new File(destRootDir,"shouldFail"));
        Assert.assertTrue(true);
    }

    private void runTest(File xsd,File dest) throws Throwable{

        SchemaCompiler compiler = XJC.createSchemaCompiler();
        compiler.setErrorListener(new TestingErrorListener());
        InputSource inputSource;
        try {
            FileInputStream fileInputStream = new FileInputStream(xsd);
            inputSource = new InputSource(fileInputStream);
            inputSource.setSystemId(xsd.toURI().toString());

            compiler.parseSchema(inputSource);

            S2JJAXBModel model = compiler.bind();

            model.generateCode(null,null);

        }catch(Exception e){
            Assert.fail(e.getMessage());
        }
    }

    private class TestingErrorListener implements ErrorListener {
        @Override
        public void error(SAXParseException exception) {
            System.out.println("ERROR: "+exception.getLocalizedMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) {
            System.out.println("FATAL ERROR: "+exception.getLocalizedMessage());
        }

        @Override
        public void warning(SAXParseException exception) {
            System.out.println("WARNING: "+exception.getLocalizedMessage());
        }

        @Override
        public void info(SAXParseException exception) {
            System.out.println("INFO: "+exception.getLocalizedMessage());
        }
    }

}
