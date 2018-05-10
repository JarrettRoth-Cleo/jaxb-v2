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

1. cd jaxb-ri
2. mvn package

This will run all tests and generate deliverables for each module.

Generating XJC dependencies:

1. cd jaxb-ri/xjc
2. mvn dependency:copy-dependencies -DincludeScope=runtime


After completing these 2 mvn calls, the XJC jar can be found in jaxb-ri/xjc/xjc/target and all dependencies can be found in jaxb-ri/xjc/xjc/target/dependencies.  All need to be added to the xsd.importwizard plugin.

