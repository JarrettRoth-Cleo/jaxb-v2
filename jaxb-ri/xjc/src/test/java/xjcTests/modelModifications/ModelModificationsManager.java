package xjcTests.modelModifications;

import java.util.ArrayList;
import java.util.List;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.model.CPropertyInfo;

import xjcTests.modelModifications.ModelModHandler.ModelModificationException;

/**
 * Manager/Cache of all the JCodeModel modifications that need to be ran before
 * the Java Code is generated.
 */
public class ModelModificationsManager {

	private final List<ModelModHandler> handlers = new ArrayList<ModelModHandler>();

	// TODO: possibly abstract this and only accept handlers
	public void addChoice(CPropertyInfo propInfo) {
		handlers.add(new ChoiceModHandler(propInfo));
	}

	public void modify(JCodeModel jModel) throws ModelModificationException {
		for (ModelModHandler handler : handlers) {
			handler.handle(jModel);
		}
	}

}
