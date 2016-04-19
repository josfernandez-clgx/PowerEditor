package com.mindbox.pe.client.applet.admin.imexport;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.model.ImportSpec;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ImportPanel extends PanelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

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

	private final XMLFileFilter xmlFilter = new XMLFileFilter();

	private class AddFileL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) {
			String filename = getFilename(ClientUtil.getInstance().getLabel("d.title.select.file.import"));
			if (filename != null) {
				listModel.addElement(filename);
			}
		}
	}

	private class RemoveFileL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) {
			if (removeFileButton.isEnabled()) {
				Object[] selections = filenameList.getSelectedValues();
				if (selections != null) {
					for (int i = 0; i < selections.length; i++) {
						listModel.removeElement(selections[i]);
					}
				}
			}
		}
	}

	private class SelectionL implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent arg0) {
			Object[] selections = filenameList.getSelectedValues();
			removeFileButton.setEnabled(selections != null && selections.length > 0);
		}
	}

	private class ImportL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			importButton.setEnabled(false);
			try {
				// build import spec
				ImportSpec spec = new ImportSpec(mergeTemplateCheckbox.isSelected());
				Object[] selections = listModel.toArray();
				for (int i = 0; i < selections.length; i++) {
					try {
						spec.addContent((String) selections[i], ClientUtil.getFileContent(new File((String) selections[i])));
					}
					catch (IOException ex) {
						ClientUtil.getInstance().showErrorDialog("msg.error.failure.import.file", new Object[] { selections[i], ex.getMessage() });
						return;
					}
				}

				ImportResult result = ClientUtil.getCommunicator().importData(spec);
				EntityModelCacheFactory.getInstance().reloadCache();
				if (result != null && result.isTemplateImported()) ClientUtil.getParent().reloadTemplates();
				ImportResultDialog.showResult(result);
			}
			finally {
				importButton.setEnabled(true);
			}
		}
	}

	private final JButton importButton;
	private final JButton addFileButton, removeFileButton;
	private final JLabel instructionLabel;
	private final JList filenameList;
	private final DefaultListModel listModel;
	private final JCheckBox mergeTemplateCheckbox;

	public ImportPanel() {
		super();
		addFileButton = UIFactory.createJButton("button.add.file", null, new AddFileL(), null);
		removeFileButton = UIFactory.createJButton("button.remove.file", null, new RemoveFileL(), null);
		importButton = UIFactory.createJButton("button.import", null, new ImportL(), null);
		instructionLabel = UIFactory.createLabel("label.import.instruction");
		mergeTemplateCheckbox = UIFactory.createCheckBox("checkbox.merge.template");
		mergeTemplateCheckbox.setSelected(false);

		listModel = new DefaultListModel();
		filenameList = new JList(listModel);
		filenameList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		filenameList.addListSelectionListener(new SelectionL());

		removeFileButton.setEnabled(false);

		initPanel();
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		JPanel panel = UIFactory.createJPanel(bag);
		panel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.import")));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(panel, bag, c, instructionLabel);

		JPanel buttonPanel = UIFactory.createFlowLayoutPanelLeftAlignment(2, 0);
		buttonPanel.add(addFileButton);
		buttonPanel.add(removeFileButton);
		c.insets.left = 2;
		c.insets.right = 2;
		addComponent(panel, bag, c, buttonPanel);

		c.weighty = 1.0;
		addComponent(panel, bag, c, new JScrollPane(filenameList));

		c.weighty = 0.0;

		addComponent(panel, bag, c, mergeTemplateCheckbox);
		addComponent(panel, bag, c, new JSeparator());

		addComponent(panel, bag, c, importButton); //buttonPanel);

		setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
		add(panel);
	}

	private String getFilename(String title) {
		String filename = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(xmlFilter);
		chooser.setDialogTitle(title);
		int returnVal = chooser.showOpenDialog(ImportPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			filename = chooser.getSelectedFile().getAbsolutePath();
		}
		return filename;
	}

}