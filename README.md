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

### Changes in Fork

#### NameConverter

The  [NameConverter interface](jaxb-ri/core/src/main/java/com/sun/xml/bind/api/impl/NameConverter.java) has all instances moved from out to the [NameConverterProvider class](jaxb-ri/core/src/main/java/com/sun/xml/bind/api/impl/NameConverterProvider.java) definition.  

Now, the interface is just an interface and can be changed via the SchemaCompiler's options.  All previous references to NameConverter.standard are now going through the NameConverterProvider.getStandard() static method.


#### JCodeModel is slighty more mutable

Changes throughout the JCodeModel classes were needed to be made so post processing could be done to the instances.  

Some changes:
* [JBlock](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JBlock.java) content can be reset
* [JAssignment](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JAssignment.java) exposes right hand side field
* [JAnnotationUse](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JAnnotationUse.java) exposes itself and the class it models
* [JAnnotationStringValue](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JAnnotationStringValue.java) is exposed along with its value
* [JAnnotationArrayMember](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JAnnotationArrayMember.java) exposes the values
* [JExpressionDotClass](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JExpressionDotClass.java) new class that is used in the [JExpr](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JExpr.java) for classes found throughout the model.  This exposed itself to outside processes.
* [JInvocation](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JInvocation.java) allows to view the type and change the type
* [JMods](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JMods.java) is made public
* [JNarrowedClass](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JNarrowedClass.java) is made public along with getter methods for its values
* [JStringLiteral](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JStringLiteral.java) allows the str value to change through a setter method
* [JVar](jaxb-ri/codemodel/codemodel/src/main/java/com/sun/codemodel/JVar.java) enables setting annotations on itself and enables changing the mods

#### Ref instantiation is controlled via factory

A new class [RefFactory](jaxb-ri/xjc/src/main/java/com/sun/tools/xjc/reader/xmlschema/ref/RefFactory.java) was added that can be overridden in the SchemaCompiler's options so change how reference classes are instantiated.

The current implementations where originally found in the [RawTypeSetBuilder](jaxb-ri/xjc/src/main/java/com/sun/tools/xjc/reader/xmlschema/RawTypeSetBuilder.java).  Classes that referenced RawTypeSetBuilder.Ref are now referencing the new [Ref](jaxb-ri/xjc/src/main/java/com/sun/tools/xjc/reader/xmlschema/ref/Ref.java) class.

An example of how to change the functionality of the RefFactory can be found in the [AbstractXJCTest](jaxb-ri/xjc/src/test/java/xjcTests/AbstractXJCTest.java)#addCustomTestingOptions method

#### CTBuilder instantiation is controlled via factory

A new class [CTBuilderFactory](jaxb-ri/xjc/src/main/java/com/sun/tools/xjc/reader/xmlschema/ct/CTBuilderFactory.java) was added that can be overridden in the SchemaCompiler's options so change how complex type builders are instantiated.

The [CTBuilder](jaxb-ri/xjc/src/main/java/com/sun/tools/xjc/reader/xmlschema/ct/CTBuilder.java) has been updated to load its array of CTBuilders through the factory instead of an instance variable.

An example of how to change the functionality of the CTBuilderFactory can be found in the [AbstractXJCTest](jaxb-ri/xjc/src/test/java/xjcTests/AbstractXJCTest.java)#addCustomTestingOptions method
