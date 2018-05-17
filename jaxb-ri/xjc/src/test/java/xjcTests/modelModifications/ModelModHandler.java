package xjcTests.modelModifications;

import com.sun.codemodel.JCodeModel;

public interface ModelModHandler {

	void handle(JCodeModel model) throws ModelModificationException;

	public static class ModelModificationException extends Exception {

	}
}
