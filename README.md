### Licensing and Governance

JAXB is licensed under a dual license - CDDL 1.1 and GPL 2.0 with Class-path Exception. 
That means you can choose which one of the two suits your needs better and use it under those terms.

We use <a href="http://glassfish.java.net/public/GovernancePolicy.html">GlassFish Governance Policy</a>, 
which means we can only accept contributions under the 
terms of <a href="http://oracle.com/technetwork/goto/oca">OCA</a>.

### More Info

Follow <a href="http://twitter.com/gfmetro">@gfmetro</a> (TBD) on Twitter to get JAXB and wider Metro (WSIT, JAX-WS, ...) related updates. See the Metro 
website at http://metro.java.net to access Metro documentation and release information. 

If you run into any issues 
or have questions, ask at our user forum: <a href="mailto:users@metro.java.net">users@metro.java.net</a>, or file an issue at one of the issue trackers
* http://java.net/jira/browse/JAXB
* http://java.net/jira/browse/JAX_WS
* http://java.net/jira/browse/WSIT


### Clarify changes

Clarify needed changes to various aspects of the XJC parsing/generation process.  Most notably:

* Standard NameConverter needs to preserve how items are represented in the XSD
* ObjectFactory needs to honor bindings values for fields/classes
* ComplexType restriction needs to be enabled

#### Add generated code to Clarify

To add all necessary classes to the XSD import wizard, you must build the project to generate the XJC deliverables.  Then, you will need to generate the dependencies for the project's runtime.

*Note*: this process is multipart and generates more jars than there were before, but I am not sure how the XJC team generates the smaller jar that just contains XSD and DTD dependencies.

Generating the project's deliverables:

This process can be defined as "clunky" at best.

First, rebuild the entire project so new deliverables are generated:

1. cd jaxb-ri
2. mvn package

Next, download the dependencies for the xjc project:
1. cd jaxb-ri/xjc
2. remove any jars located in the target/dependency dircectory
3. Copy dependencies into the target/dependency directory: `mvn dependency:copy-dependencies -DincludeScope=runtime`

These next steps are where this process starts to fall apart.  The maven logic for copying dependencies seems to only download the libraries from the local maven repo, not the project like we want.

First, copy the XJC library `xjc/target/jaxb-xjc-2.2.11.jar`
Next, copy the XJC dependencies that did not get generated:
* dtd-parser
* jaxb-api
* istack-commons-runtime
* istack-commons-tools
* relaxngDatatype
* rngom
* xml-apis
* xsom

*Note*: if these values are already in the product, they shouldn't need to be changed

Lastly, copy out the generated dependencies from the other modules to satisfy the XJC dependecies:
*Note*: these paths are relative to the jaxb-ri project root

* codemodel\codemodel\target\codemodel-2.2.11.jar
* core\target\jaxb-core-2.2.11.jar
* txw\runtime\target\txw2-2.2.11.jar

All of these libraries need to be placed in the Clairfy XSD import wizard plugin for all of the changes to take effect.



After completing these 2 mvn calls, the XJC jar can be found in jaxb-ri/xjc/xjc/target and all dependencies can be found in jaxb-ri/xjc/xjc/target/dependencies.  All need to be added to the xsd.importwizard plugin.


### Overriding the Default NameConverter

Changes were made to the XJC logic to no longer utilize the hard coded NameConverter.standard NameConverter instance.  This allows us to change how the class/interface/field names are generated without having to rebuild the entire XJC project.

This [class](jaxb-ri/core/src/main/java/com/sun/xml/bind/api/impl/NameConverterProvider.java) was added to be the source of each kind of NameConverter instance available to the XJC project.  

This [test suite](jaxb-ri/xjc/src/test/java/xjcTests/NameConverterOverrideTest.java) was created to represent how the standard NameConverter can be implemented.

The current instance of the Standard NameConverter used in the test attempts to remove any automatic camel casing so it matches the XSD closer.  This doesn't follow Java naming conventions, but it does match the data closer.

TODO: Override the `Standard#removeIllegalIdentifierChars` method in in the new  NameConverter in order to map Illegal Identifier characters to their full names.
Ex: '*' is found in an element name, replace it with 'asterisk'



