/*
 * Created on 2004. 3. 8.
 *
 */
package com.mindbox.pe.client.common;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.tree.AttributeSelectionTree;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Reference;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AttributeReferenceSelectField extends AbstractDropSelectField implements TreeSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private String attribName = null;
	private AttributeSelectionTree attribTree = null;
	private String className = null;
	private int[] genericDataTypes = null;

	public AttributeReferenceSelectField() {
		super(false);
	}

	protected String getFindButtonImageKey() {
		return "image.btn.find.attribute";
	}

	public String getAttributeName() {
		return attribName;
	}

	public String getClassName() {
		return className;
	}

	public String getValue() {
		return (attribName == null ? null : className + "." + attribName);
	}

	public boolean hasValue() {
		return (attribName != null);
	}

	private void refreshTextField() {
		String prevValue = textField.getText();
		DomainClass dc = null;
		DomainAttribute da = null;
		if (className != null) {
			dc = DomainModel.getInstance().getDomainClass(className);
			if (dc != null && attribName != null) da = dc.getDomainAttribute(attribName);
		}
		textField.setText(da == null ? "" : dc.getDisplayLabel() + " : " + da.getDisplayLabel());
		if (!ClientUtil.isSame(prevValue, textField.getText())) {
			notifyChangeListeners();
		}
	}


	public void clearValue() {
		setValue(null, null);
	}

	public void setValue(Reference ref) {
		this.className = ref.getClassName();
		this.attribName = ref.getAttributeName();
		refreshTextField();
	}

	public void setValue(String className, String attributeName) {
		if (className != null && attributeName != null) {
			this.className = className;
			this.attribName = attributeName;
		}
		else {
			this.className = null;
			this.attribName = null;
		}
		refreshTextField();
	}

	protected JComponent createSelectorComponent() {
		attribTree = new AttributeSelectionTree(genericDataTypes);
		attribTree.getSelectionModel().addTreeSelectionListener(this);
		return new JScrollPane(attribTree);
	}

	protected void selectSelectedValues() {
		if (attribTree != null && className != null && attribName != null) attribTree.selectAttribute(className, attribName);
	}

	protected void selectorClosed() {
		if (attribTree.isAttributeNodeSelected()) {
			className = attribTree.getClassOfSelectedAttribute().getName();
			attribName = attribTree.getSelectedAttribute().getName();
		}
		else {
			className = null;
			attribName = null;
		}
		refreshTextField();
	}

	protected void valueDeleted() {
		className = null;
		attribName = null;
		refreshTextField();
		notifyChangeListeners();
	}

	public void valueChanged(TreeSelectionEvent arg0) {
		if (attribTree.isAttributeNodeSelected()) {
			try {
				Thread.sleep(450);
			}
			catch (InterruptedException e) {
			}
			closeWindow();
		}
	}

	public void filterAttributes(int[] genericDataTypes) {
		setGenericDataTypes(genericDataTypes);
		if (attribTree != null) attribTree.filterAttributes(genericDataTypes);
	}


	/**
	 * @return Returns the genericDataTypes.
	 */
	public int[] getGenericDataTypes() {
		return genericDataTypes;
	}

	/**
	 * @param genericDataTypes The genericDataTypes to set.
	 */
	public void setGenericDataTypes(int[] genericDataTypes) {
		this.genericDataTypes = genericDataTypes;
	}
}
