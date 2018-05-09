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

#### To build a new XJC jar for Clarify:

1. In command line: move into the xjc module
2. run ```mvn package```
3. The new jar should be found in the xjc/target directory

This new jar can be added to the Clarify XSD import wizard plugin to be used when generated XML schemas.

TODO: automate this packaging and placing the most recent version on a shared location for Clarify.