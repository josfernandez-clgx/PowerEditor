/*
 * Created on 2004. 3. 8.
 *
 */
package com.mindbox.pe.client.common;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.tree.DomainClassSelectionTree;
import com.mindbox.pe.model.DomainClass;

/**
 * Field for selecting a domain class.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 */
public class DomainClassSelectField extends AbstractDropSelectField implements TreeSelectionListener {

	private DomainClassSelectionTree classTree = null;
	private String className = null;
	private boolean flat;

	public DomainClassSelectField() {
		this(false);
	}
	
	/**
	 * Constructs a new domain class select field with specified tree view mode.
	 * @param flat if <code>true</code>, domain tree view is flattend out to be a list.
	 */
	public DomainClassSelectField(boolean flat) {
		super(false);
		this.flat = flat;
	}

	protected String getFindButtonImageKey() {
		return "image.btn.find.class";
	}

	public String getValue() {
		return className;
	}

	public boolean hasValue() {
		return (className != null);
	}

	private void refreshTextField() {
		DomainClass dc = null;
		if (className != null) {
			dc = DomainModel.getInstance().getDomainClass(className);
		}
		textField.setText(dc == null ? "" : dc.getDisplayLabel());
	}

	public void setValue(String className) {
		if (className != null) {
			this.className = className;
		}
		else {
			this.className = null;
		}
		refreshTextField();
	}

	protected JComponent createSelectorComponent() {
		classTree = new DomainClassSelectionTree(flat);
		classTree.getSelectionModel().addTreeSelectionListener(this);
		return new JScrollPane(classTree);
	}

	protected void selectSelectedValues() {
		classTree.selectAttribute(className);
	}

	protected void selectorClosed() {
		if (classTree.isClassNodeSelected()) {
			className = classTree.getSelectedClass().getName();
		}
		else {
			className = null;
		}
		refreshTextField();
	}

	protected void valueDeleted() {
		className = null;
	}

	public void valueChanged(TreeSelectionEvent arg0) {
		if (classTree.isClassNodeSelected()) {
			try {
				Thread.sleep(450);
			}
			catch (InterruptedException e) {}
			closeWindow();
		}
	}

}
