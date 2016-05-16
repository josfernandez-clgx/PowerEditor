/*
 * Created on 2004. 4. 2.
 *
 */
package com.mindbox.pe.client.common;

import java.awt.Container;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;

/**
 * PE customzed file chooser field.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class FileChooserField extends AbstractDropSelectField {
	private static class HTMLFileFilter extends FileFilter {

		@Override
		public boolean accept(File file) {
			if (file.isFile()) {
				String[] strs = file.getName().split("\\.");
				return strs != null && strs.length > 1 && strs[strs.length - 1] != null && strs[strs.length - 1].equalsIgnoreCase("HTML");
			}
			else {
				return true;
			}
		}

		@Override
		public String getDescription() {
			return "PowerEditor Report File (.html)";
		}
	}

	public static class Operation {
		public static final Operation OPEN = new Operation();
		public static final Operation SAVE = new Operation();
		public static final Operation SELECT = new Operation();

		private int showDialog(JFileChooser chooser, Container container) {
			if (this == Operation.OPEN) {
				return chooser.showOpenDialog(container);
			}
			else if (this == Operation.SAVE) {
				return chooser.showSaveDialog(container);
			}
			else { //this == Operation.SELECT
				return chooser.showDialog(container, ClientUtil.getInstance().getLabel("button.ok"));
			}
		}
	}

	private static class XMLFileFilter extends FileFilter {

		@Override
		public boolean accept(File file) {
			if (file.isFile()) {
				String[] strs = file.getName().split("\\.");
				return strs != null && strs.length > 1 && strs[strs.length - 1] != null && strs[strs.length - 1].equalsIgnoreCase("XML");
			}
			else {
				return true;
			}
		}

		@Override
		public String getDescription() {
			return "PowerEditor Data File (.xml)";
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	private String filename = null;
	private String diagTitle = null;
	private Operation op;
	private final FileFilter xmlFilter = new XMLFileFilter();
	private final FileFilter htmlFilter = new HTMLFileFilter();
	private final boolean useHTML, useXML;

	/**
	 * Calling this is equivalent to calling <code>FileChooserField(op, false, true)</code>.
	 * @param op op
	 */
	public FileChooserField(Operation op) {
		this(op, false, true);
	}

	public FileChooserField(Operation op, boolean useHTML, boolean useXML) {
		super(false);
		this.op = op;
		this.useHTML = useHTML;
		this.useXML = useXML;
	}

	// TT 2136
	public FileChooserField(Operation op, String diagTitle) {
		this(op, false, true);
		this.diagTitle = diagTitle;
	}

	@Override
	protected JComponent createSelectorComponent() {
		return null;
	}

	private Container getContainerOfThis() {
		Container window = super.getWindow();
		return window == null ? (Container) ClientUtil.getApplet() : window;
	}

	@Override
	protected String getFindButtonImageKey() {
		return "image.btn.small.open";
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(340, super.getPreferredSize().height);
	}

	public final String getValue() {
		return this.filename;
	}

	public boolean isEmpty() {
		return filename == null;
	}

	private void refresh() {
		super.textField.setText((filename == null ? "" : filename));
	}

	@Override
	protected void selectorClosed() {
	}

	@Override
	protected void selectSelectedValues() {
	}

	public final void setValue(String filename) {
		this.filename = filename;
		refresh();
	}

	@Override
	public void showSelector() {
		ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());
		selectButton.setEnabled(false);
		deleteButton.setEnabled(false);
		try {
			JFileChooser chooser = new JFileChooser();
			if (useXML) chooser.addChoosableFileFilter(xmlFilter);
			if (useHTML) chooser.addChoosableFileFilter(htmlFilter);
			// TT 2136
			if (diagTitle != null) chooser.setDialogTitle(diagTitle);

			int returnVal = op.showDialog(chooser, getContainerOfThis());

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				filename = chooser.getSelectedFile().getAbsolutePath();
			}
			refresh();
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
		finally {
			selectButton.setEnabled(true);
			deleteButton.setEnabled(true);
			ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
		}
	}

	@Override
	protected void valueDeleted() {
		filename = null;
		refresh();
	}
}