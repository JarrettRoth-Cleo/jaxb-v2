import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.tools.xjc.api.*;
import org.junit.*;
import org.junit.rules.TestName;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XJCTests {
	//TODO: there must be a better way to do this
	private final File resourceDir = new File("src/test/resources");
    private final File destRootDir = new File("src/test/output");
    @Rule
    public TestName name = new TestName();
    
    @Before
    public void setup(){
        File outputDir = new File(destRootDir, name.getMethodName());
    }

    @Test
    public void schemaShouldParseAndProduceModel_test(){
        File xsd = new File(resourceDir,"ImageAttachment.xsd");
        SchemaCompiler compiler = getInitializedSchemaCompiler(xsd, new ArrayList<File>());
        S2JJAXBModel model = compiler.bind();
        Assert.assertNotNull(model);
    }


    @Test
    public void schemaShouldParseAndFailAndNotProduceModel_test(){
        File xsd = new File(resourceDir, "simplified.xsd");
        SchemaCompiler compiler = getInitializedSchemaCompiler(xsd, new ArrayList<File>());
        S2JJAXBModel model = compiler.bind();
        Assert.assertNull(model);
    }

    @Test
    public void schemaShouldPraseAndProduceModelUsingBindings_test(){
        File xsd = new File(resourceDir, "simplified.xsd");
        List<File> bindings = new ArrayList<File>();
        File bindingFile = new File(resourceDir, "simplifiedbindings.xsd");
        bindings.add(bindingFile);
        SchemaCompiler compiler = getInitializedSchemaCompiler(xsd, bindings);
        S2JJAXBModel model = compiler.bind();
        Assert.assertNotNull(model);
    }

    @Ignore
    @Test
    public void shouldFailWithBindings3() throws Throwable{
        File xsd = new File(resourceDir, "simplified.xsd");
        //File bindingsFile = new File(resourceDir, "simplifiedBindings.xjb");
        List<File> bindings = new ArrayList<File>();
        bindings.add(new File(resourceDir, "simplifiedbindings.xsd"));
        //runTest(xsd,bindingsFile);
        Assert.assertTrue(true); 
    }


    private InputSource getInputSource(File file) {
        InputSource inputSource = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            inputSource = new InputSource(fileInputStream);
            inputSource.setSystemId(file.toURI().toString());
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return inputSource;
    }

    private SchemaCompiler getInitializedSchemaCompiler(File xsd, List<File> bindings){
        SchemaCompiler compiler = XJC.createSchemaCompiler();
        //TODO: THIS is how you activate a plugin for post porcessing modeling...
        compiler.getOptions().activePlugins.add(new TestingPlugin2());
        compiler.setErrorListener(new TestingErrorListener());
        for(File f : bindings){
            compiler.getOptions().addBindFile(getInputSource(f));
        }
        compiler.parseSchema(getInputSource(xsd));
        return compiler;
    }

    private S2JJAXBModel getModel(File ... bindings){
        //return compiler.bind();
        /*
        This can be ignored for now as it is the final step in building the schema which
        means it is used to actually generate files.  Its useless without a S2JJAXBmodel
       -------
        Assert.assertNotNull("model is null",model);

        JCodeModel jModel = model.generateCode(null,null);
        if(!outputDir.exists()){
            outputDir.mkdirs();
        }
        jModel.build(outputDir);
        //TODO: delete
        */
        return null;
    }

    /**
     * This code should be inserted in order to show how to fully generate files using the schema compiler
     * maybe make this another test
     *
     */
    private class NullModelException extends Exception{
        public NullModelException(String message) {
            super(message);
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


    private class TestingPlugin extends Plugin{

        @Override
        public String getOptionName() {
            return "Quick Test";
        }

        @Override
        public String getUsage() {
            return "PostProcess";
        }

        @Override
        public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
            return true;
        }

        @Override
		public void postProcessModel(Model model, ErrorHandler errorHandler) {
            //oh boi
            Map<NClass,CClassInfo> changesToMake = new HashMap<NClass, CClassInfo>();
            List<String> usedNames = new ArrayList<String>();
            Map o = model.beans();
            for(Map.Entry<NClass, CClassInfo> entry : model.beans().entrySet()){
                String name  = entry.getValue().toString();
                if(usedNames.contains(name)){
                    //TODO: this seems to be adding the correct change to the correct class info.
                    // Now, how can this be used when generating code?
                    /*
                    TODO: will a wrapper be better? The process could build a list of entries to modify, remove them from the model, and add them back in
                     */
                    name = findUniquName(usedNames,name);
                    entry.getValue().getCustomizations().add(new CPluginCustomization(new Woof(buildShortName(name)) ,entry.getValue().getLocator()));
                    changesToMake.put(entry.getKey(),entry.getValue());
                    usedNames.add(name);
                }else{
                    usedNames.add(name);
                }
            }
            for(Map.Entry<NClass,CClassInfo> entry : changesToMake.entrySet()){
                model.beans().remove(entry.getKey());
                //TODO
               String val = entry.getValue().getCustomizations().get(0).element.getAttribute("anything");
               buildNewClassInfoWithNewShortName(entry.getValue(),val);
//                model.beans().put(entry.getKey(),);
            }
            System.out.println("Plugin modifying...");
        }

    }

    
    private class TestingPlugin2 extends Plugin{

        @Override
        public String getOptionName() {
            return "Quick Test";
        }

        @Override
        public String getUsage() {
            return "PostProcess";
        }

        @Override
        public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
            return true;
        }

        @Override
		public void postProcessModel(Model model, ErrorHandler errorHandler) {
            //oh boi
        	Map<NClass,CClassInfo> conflicts = findConflicts(model);
          for(CElementInfo info :  model.getAllElements()){
        	  org.w3c.dom.Element woof = new Woof("todo");
        	       	  
//        	  info.getCustomizations().add(new CPluginCustomization(woof ,info.getLocator()));
          }
        }

    }
    
    private Map<NClass,CClassInfo> findConflicts(Model m){
    	Map<NClass,CClassInfo> changesToMake = new HashMap<NClass, CClassInfo>();
        List<String> usedNames = new ArrayList<String>();
        for(Map.Entry<NClass, CClassInfo> entry : m.beans().entrySet()){
            String name  = entry.getValue().toString();
            if(usedNames.contains(name)){
     
                name = findUniquName(usedNames,name);
                entry.getValue().getCustomizations().add(new CPluginCustomization(new Woof(buildShortName(name)) ,entry.getValue().getLocator()));
                changesToMake.put(entry.getKey(),entry.getValue());
                usedNames.add(name);
            }else{
                usedNames.add(name);
            }
        }
        return changesToMake;
    }
    
    private void updateNewClassInfoUseages(NClass newCLass){

    }

    private String buildShortName(String fullName){
        String[] parts = fullName.split("\\.");
        return parts[parts.length-1];
    }

    private String findUniquName(List<String> names,String baseName){
        String modifiedName = baseName;
        int counter = 1;
        while(doesListContain(names, modifiedName)){
            modifiedName = baseName + Integer.toString(++counter);
        }
        return modifiedName;
    }

    private boolean doesListContain(List<String> values, String value){
        for(String s : values){
            if(s.equalsIgnoreCase(value)){
                return true;
            }
        }
        return false;
    }


    private CClassInfo buildNewClassInfoWithNewShortName(final CClassInfo info, String shortName){
        return new CClassInfo(info.model,info.parent(),shortName,info.getLocator(),info.getTypeName(),info.getElementName(),info.getSchemaComponent(),info.getCustomizations());
    }

//    private class Woof2 extends CClassInfo{
//        public String shortName;
//        public Woof2(CClassInfo w){
//            super(w.model,w.getOwnerPackage(),w.)
//        }
//    }


    //TODO: there HAS to be a better way than fudging it like this...
    private class Woof implements org.w3c.dom.Element{

        private String overrideName;
        public Woof(String overrideName){
            this.overrideName = overrideName;
        }

        @Override
        public String getTagName() {
            return "CLEO_CUSTOM_TAG_HOPE_THIS_DOESNT_BREAK_THINGS";
        }

        @Override
        public String getAttribute(String name) {
            return overrideName;
        }

        @Override
        public void setAttribute(String name, String value) throws DOMException {

        }

        @Override
        public void removeAttribute(String name) throws DOMException {

        }

        @Override
        public Attr getAttributeNode(String name) {
            return null;
        }

        @Override
        public Attr setAttributeNode(Attr newAttr) throws DOMException {
            return null;
        }

        @Override
        public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
            return null;
        }

        @Override
        public NodeList getElementsByTagName(String name) {
            return null;
        }

        @Override
        public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
            return null;
        }

        @Override
        public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {

        }

        @Override
        public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {

        }

        @Override
        public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
            return null;
        }

        @Override
        public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
            return null;
        }

        @Override
        public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
            return null;
        }

        @Override
        public boolean hasAttribute(String name) {
            return false;
        }

        @Override
        public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
            return false;
        }

        @Override
        public TypeInfo getSchemaTypeInfo() {
            return null;
        }

        @Override
        public void setIdAttribute(String name, boolean isId) throws DOMException {

        }

        @Override
        public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {

        }

        @Override
        public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {

        }

        @Override
        public String getNodeName() {
            return null;
        }

        @Override
        public String getNodeValue() throws DOMException {
            return null;
        }

        @Override
        public void setNodeValue(String nodeValue) throws DOMException {

        }

        @Override
        public short getNodeType() {
            return 0;
        }

        @Override
        public Node getParentNode() {
            return null;
        }

        @Override
        public NodeList getChildNodes() {
            return null;
        }

        @Override
        public Node getFirstChild() {
            return null;
        }

        @Override
        public Node getLastChild() {
            return null;
        }

        @Override
        public Node getPreviousSibling() {
            return null;
        }

        @Override
        public Node getNextSibling() {
            return null;
        }

        @Override
        public NamedNodeMap getAttributes() {
            return null;
        }

        @Override
        public Document getOwnerDocument() {
            return null;
        }

        @Override
        public Node insertBefore(Node newChild, Node refChild) throws DOMException {
            return null;
        }

        @Override
        public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
            return null;
        }

        @Override
        public Node removeChild(Node oldChild) throws DOMException {
            return null;
        }

        @Override
        public Node appendChild(Node newChild) throws DOMException {
            return null;
        }

        @Override
        public boolean hasChildNodes() {
            return false;
        }

        @Override
        public Node cloneNode(boolean deep) {
            return null;
        }

        @Override
        public void normalize() {

        }

        @Override
        public boolean isSupported(String feature, String version) {
            return false;
        }

        @Override
        public String getNamespaceURI() {
            return null;
        }

        @Override
        public String getPrefix() {
            return null;
        }

        @Override
        public void setPrefix(String prefix) throws DOMException {

        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public boolean hasAttributes() {
            return false;
        }

        @Override
        public String getBaseURI() {
            return null;
        }

        @Override
        public short compareDocumentPosition(Node other) throws DOMException {
            return 0;
        }

        @Override
        public String getTextContent() throws DOMException {
            return null;
        }

        @Override
        public void setTextContent(String textContent) throws DOMException {

        }

        @Override
        public boolean isSameNode(Node other) {
            return false;
        }

        @Override
        public String lookupPrefix(String namespaceURI) {
            return null;
        }

        @Override
        public boolean isDefaultNamespace(String namespaceURI) {
            return false;
        }

        @Override
        public String lookupNamespaceURI(String prefix) {
            return null;
        }

        @Override
        public boolean isEqualNode(Node arg) {
            return false;
        }

        @Override
        public Object getFeature(String feature, String version) {
            return null;
        }

        @Override
        public Object setUserData(String key, Object data, UserDataHandler handler) {
            return null;
        }

        @Override
        public Object getUserData(String key) {
            return null;
        }
    }



}
