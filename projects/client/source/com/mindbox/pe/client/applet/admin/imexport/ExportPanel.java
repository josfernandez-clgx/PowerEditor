package com.mindbox.pe.client.applet.admin.imexport;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.FileChooserField;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.filter.panel.DataFilterPanel;
import com.mindbox.pe.client.common.filter.panel.GuidelineFilterPanel;
import com.mindbox.pe.model.filter.GuidelineReportFilter;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ExportPanel extends PanelBase {
	
	private static final long serialVersionUID = 4489985693297838626L;
	
	private class SelectToggleL extends AbstractThreadedActionAdapter {

		private boolean selectState = false;

		public synchronized void performAction(ActionEvent event) throws Exception {
			dataFilterPanel.setAllSelectionCriteria(selectState);
			guidelineFilterPanel.setEnabledPanel(selectState);
			
			// clear it so that nothing is filter out when select all button is selected, except for status
			if (selectState) {
				guidelineFilterPanel.clearSelectionCriteria();
				//guidelineFilterPanel.setThisStatusOrAboveStatus(Constants.DRAFT_STATUS);
			}
			
			selectState = !selectState;
			selectAllButton.setText(ClientUtil.getInstance().getLabel((selectState ? "button.select.all" : "button.select.none")));
		}
	}

	private class ExportL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			GuidelineReportFilter filter = dataFilterPanel.getFilter();		
						
			if (!dataFilterPanel.hasSelection()) {
				ClientUtil.getInstance().showWarning("msg.warning.empty.data.selection");
			}
			else {				
				// TT 2135
				if (outFileField.isEmpty()) {
					outFileField.showSelector();
				}

				if (!outFileField.isEmpty()) {
					byte[] ba = ClientUtil.getCommunicator().exportDataToClient(filter);
					if (ba == null || ba.length == 0) {
						ClientUtil.getInstance().showErrorDialog("msg.error.export.empty");
					}
					else {
						String filename = outFileField.getValue();
						if (filename.indexOf(".") < 0 || filename.endsWith(".")) {
							filename = filename + ".xml";
							outFileField.setValue(filename);
						}
						File file = new File(filename);
						PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, false)), true);
						BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(ba))));
						for (String line = reader.readLine(); line != null; line = reader.readLine()) {
							writer.println(line);
						}
						writer.flush();
						writer.close();
						reader.close();
						ClientUtil.getInstance().showInformation("msg.info.success.export");
					}
				}
			}			
		}
	}
 
	private final DataFilterPanel dataFilterPanel;
	private final GuidelineFilterPanel guidelineFilterPanel;
	private JButton exportButton, selectAllButton;
	private final FileChooserField outFileField;

	public ExportPanel() {
		super();
		guidelineFilterPanel = new GuidelineFilterPanel(true);	
		guidelineFilterPanel.setVisibleChangesOnDateField(false);
		guidelineFilterPanel.setVisibleAttributeField(false);
		guidelineFilterPanel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.policy.selection.criteria")));
		
		dataFilterPanel = new DataFilterPanel(guidelineFilterPanel, true);
		// TT 2136				
		outFileField = new FileChooserField(FileChooserField.Operation.SELECT, ClientUtil.getInstance().getLabel("d.title.select.file.export"));
		exportButton = UIFactory.createJButton("button.export", null, new ExportL(), null);
		selectAllButton = UIFactory.createJButton("button.select.none", null, new SelectToggleL(), null);
				
		initPanel();
		
		// default 
		dataFilterPanel.setAllSelectionCriteria(true);
//		guidelineFilterPanel.setThisStatusOrAboveStatus(Constants.DRAFT_STATUS);
	}
	
	private void initPanel() {
		
		GridBagLayout bag = new GridBagLayout();
		JPanel panel = UIFactory.createJPanel(bag);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;
		
		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(panel, bag, c, UIFactory.createFormLabel("label.file.target"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(panel, bag, c, outFileField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(panel, bag, c, exportButton);

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(panel, bag, c, selectAllButton);

		c.weightx = 0.1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(panel, bag, c, Box.createHorizontalGlue());
				
		// main panel
		this.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.export.data")));	
		setLayout(new BorderLayout(4, 10));		
		add(panel, BorderLayout.NORTH);
		
		JSplitPane splitPane = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataFilterPanel, guidelineFilterPanel);
		add(splitPane, BorderLayout.CENTER);
	}	
}