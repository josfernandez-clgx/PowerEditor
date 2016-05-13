/*
 * Created on 2003. 12. 17.
 */
package com.mindbox.pe.client.common.dialog;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.DomainClassSelectField;
import com.mindbox.pe.client.common.tree.AttributeSelectionTree;

/**
 * Dialog factory.
 * @author Geneho Kim
 * @author MindBox
 */
public class DialogFactory {

	private static AttributeSelector attributeSelector = null;
	private static DomainClassSelector domainClassSelector = null;

	private static AttributeSelector getAttributeSelector() {
		if (attributeSelector == null) {
			attributeSelector = new AttributeSelector();
		}
		return attributeSelector;
	}

	private static DomainClassSelector getDomainClassSelector() {
		if (domainClassSelector == null) {
			domainClassSelector = new DomainClassSelector();
		}
		return domainClassSelector;
	}

	public static String showAttributeSelector(String prevReference) {
		return getAttributeSelector().showDialog((prevReference == null ? "" : prevReference));
	}


	private static class AttributeSelector {

		final AttributeSelectionTree classTree;

		private AttributeSelector() {
			classTree = new AttributeSelectionTree();
		}

		public String showDialog(String prevRef) {
			if (DomainModel.getInstance().isEmpty()) {
				ClientUtil.getInstance().showErrorDialog("msg.error.no.domain.class");
				return prevRef;
			}
			else {
				classTree.selectAttribute(prevRef);

				int option = JOptionPane.showConfirmDialog(
						ClientUtil.getApplet(),
						new JScrollPane(classTree),
						"Select Attribute",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (option == JOptionPane.OK_OPTION) {
					if (classTree.isAttributeNodeSelected()) { return classTree.getClassOfSelectedAttribute().getName() + "."
							+ classTree.getSelectedAttribute().getName(); }
				} else {
					return null;
				}
				return prevRef;
			} // else
		}
	}

	public static String showDomainClassSelector(String prevClassName) {
		return getDomainClassSelector().showDialog(prevClassName);//(prevClassName == null ? "" : prevClassName));
	}

	private static class DomainClassSelector {

		private final DomainClassSelectField field;

		private DomainClassSelector() {
			field = new DomainClassSelectField();
		}

		public String showDialog(String prevRef) {
			field.setValue(prevRef);

			int option = JOptionPane.showConfirmDialog(
					ClientUtil.getApplet(),
					field,
					"Select Domain Class",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (option == JOptionPane.OK_OPTION && field.hasValue()) { return field.getValue(); }
			return prevRef;

		}
	}


	private DialogFactory() {
	}
}