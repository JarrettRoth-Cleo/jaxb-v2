package xjcTests.temp;

import com.sun.codemodel.JCodeModel;

/**
 * Simple interface for handling JCodeModel changes before the JavaCode is
 * generated.
 */
public interface ModelModHandler {

	void handle(JCodeModel model) throws ModelModificationException;

	public static class ModelModificationException extends Exception {

	}
}
