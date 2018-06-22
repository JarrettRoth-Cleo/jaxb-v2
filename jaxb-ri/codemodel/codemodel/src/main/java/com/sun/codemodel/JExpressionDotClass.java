package com.sun.codemodel;

/*
 * New JExpression class to expose reference classes in annotations and such.
 */
public class JExpressionDotClass extends JExpressionImpl {
	private final JClass cl;

	public JExpressionDotClass(JClass cl) {
		this.cl = cl;
	}

	@Override
	public void generate(JFormatter f) {
		JClass c;
		if (cl instanceof JNarrowedClass)
			c = ((JNarrowedClass) cl).basis;
		else
			c = cl;
		f.g(c).p(".class");
	}

	public JClass _getClass() {
		return cl;
	}

}
