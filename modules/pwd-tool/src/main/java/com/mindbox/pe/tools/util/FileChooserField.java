/*
 * Created on 2005. 2. 24.
 *
 */
package com.mindbox.pe.tools.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * PE customzed file chooser field, for migration tool.
 * This is copied exactly from the com.mindbox.pe.client.common.FileChooserField, on 2/24/2005,
 * and then simplified for our purposes.
 * @author Inna Nill
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
public class FileChooserField extends JPanel implements ActionListener {

	private static class HTMLFileFilter extends FileFilter {

		public boolean accept(File file) {
			if (file.isFile()) {
				String[] strs = file.getName().split("\\.");
				return strs != null && strs.length > 1 && strs[strs.length - 1] != null && strs[strs.length - 1].equalsIgnoreCase("HTML");
			}
			else {
				return true;
			}
		}

		public String getDescription() {
			return "PowerEditor Report File (.html)";
		}
	}

	private static class XMLFileFilter extends FileFilter {

		public boolean accept(File file) {
			if (file.isFile()) {
				String[] strs = file.getName().split("\\.");
				return strs != null && strs.length > 1 && strs[strs.length - 1] != null && strs[strs.length - 1].equalsIgnoreCase("XML");
			}
			else {
				return true;
			}
		}

		public String getDescription() {
			return "PowerEditor Data File (.xml)";
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1368807955372168743L;

	private String filename = null;
	private final FileFilter xmlFilter = new XMLFileFilter();
	private final FileFilter htmlFilter = new HTMLFileFilter();
	private final boolean forOpen, useHTML, useXML;
	protected final JTextField textField;
	final JButton selectButton, deleteButton;
	private File lastDirectory = null;

	/**
	 * Calling this is equivalent to calling <code>FileChooserField(forOpen, false, true)</code>.
	 * @param forOpen forOpenReportGenerato
	 */
	public FileChooserField(boolean forOpen) {
		this(forOpen, false, true);
	}

	/**
	 * 
	 * @param forOpen indicate whether is this for opening file; <code>false</code> for saving file
	 * @param useHTML useHTMLReportGenerato
	 * @param useXML useXMLReportGenerato
	 */
	public FileChooserField(boolean forOpen, boolean useHTML, boolean useXML) {
		super(new BorderLayout(0, 0));
		this.forOpen = forOpen;
		this.useHTML = useHTML;
		this.useXML = useXML;
		textField = new JTextField(30);
		textField.setEditable(false);
		selectButton = new JButton("Select");
		selectButton.setFocusable(false);
		selectButton.addActionListener(this);
		deleteButton = new JButton("Delete");
		deleteButton.setFocusable(false);
		deleteButton.addActionListener(this);

		JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		bp.add(selectButton);
		bp.add(deleteButton);

		add(textField, BorderLayout.CENTER);
		add(bp, BorderLayout.EAST);
	}


	public final void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == selectButton) {
			showSelector();
		}
		else if (arg0.getSource() == deleteButton) {
			textField.setText("");
			valueDeleted();
		}
	}

	protected JComponent createSelectorComponent() {
		return null;
	}

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
		textField.setText((filename == null ? "" : filename));
	}

	protected void selectorClosed() {
	}

	protected void selectSelectedValues() {
	}

	public final void setValue(String filename) {
		this.filename = filename;
		refresh();
	}

	void showSelector() {
		try {
			JFileChooser chooser = null;
			if (lastDirectory == null)
				chooser = new JFileChooser();
			else
				chooser = new JFileChooser(lastDirectory);

			if (useXML) chooser.addChoosableFileFilter(xmlFilter);
			if (useHTML) chooser.addChoosableFileFilter(htmlFilter);

			int returnVal;
			if (forOpen) {
				returnVal = chooser.showOpenDialog(this.getParent());
			}
			else {
				returnVal = chooser.showSaveDialog(this.getParent());
			}

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				lastDirectory = chooser.getCurrentDirectory();
				filename = chooser.getSelectedFile().getAbsolutePath();
			}
			refresh();
		}
		catch (Exception ex) {
			System.out.println("Exception: " + ex.toString());
		}
		finally {
			;
		}
	}

	protected void valueDeleted() {
		filename = null;
		refresh();
	}

}