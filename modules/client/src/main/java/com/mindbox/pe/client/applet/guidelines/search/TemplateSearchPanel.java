/*
 * Created on 2004. 3. 5.
 *
 */
package com.mindbox.pe.client.applet.guidelines.search;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.AttributeReferenceSelectField;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.table.TemplateIDNameTable;
import com.mindbox.pe.client.common.table.TemplateIDNameTableModel;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.model.filter.TemplateFilter;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class TemplateSearchPanel extends PanelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private class SearchL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			try {
				List<GridTemplate> filteredList = new ArrayList<GridTemplate>();
				List<GridTemplate> templateList = ClientUtil.getCommunicator().search(getSearchFilter());
				if (templateList != null) {
					for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
						GridTemplate element = iter.next();
						if (ClientUtil.checkViewOrEditGuidelinePermissionOnUsageType(element.getUsageType())) {
							filteredList.add(element);
						}
					}
				}
				templateTable.setDataList(filteredList);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	private final TemplateIDNameTable templateTable;
	private final TemplateIDNameTableModel templateTableModel;
	private final JButton searchButton;
	private final AttributeReferenceSelectField attributeField;

	public TemplateSearchPanel(JButton buttons[]) {
		templateTableModel = new TemplateIDNameTableModel();
		templateTable = new TemplateIDNameTable(templateTableModel);
		templateTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		searchButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.search.template"), null, new SearchL(), null);

		attributeField = new AttributeReferenceSelectField();

		initPanel(buttons);
	}

	public void addMouseListenerToTable(MouseListener ml) {
		templateTable.addMouseListener(ml);
	}

	public List<GridTemplate> getSelectedTemplates() {
		List<GridTemplate> list = new ArrayList<GridTemplate>();
		int[] rows = templateTable.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			list.add((GridTemplate) templateTable.getModel().getValueAt(rows[i], -1));
		}
		return list;
	}

	private SearchFilter<GridTemplate> getSearchFilter() {
		TemplateFilter filter = new TemplateFilter(null);

		if (attributeField.hasValue()) {
			String[] names = attributeField.getValue().split("\\.");
			if (names.length > 0) {
				filter.setClassName(names[0]);
			}
			if (names.length > 1) {
				filter.setAttributeName(names[1]);
			}
		}
		return filter;
	}

	private void initPanel(JButton[] buttons) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;
		GridBagLayout bag = new GridBagLayout();

		setLayout(bag);
		//JPanel searchPanel = UIFactory.createJPanel(bag);

		JLabel label = UIFactory.createFormLabel("label.attribute");
		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, attributeField);

		addComponent(this, bag, c, searchButton);

		addComponent(this, bag, c, new JSeparator());

		JPanel tnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		tnPanel.add(UIFactory.createFormLabel("label.template"));

		JPanel btnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.RIGHT, 1, 1));
		for (int i = 0; i < buttons.length; i++) {
			btnPanel.add(buttons[i]);
		}

		JPanel nPanel = UIFactory.createJPanel(new GridLayout(1, 2, 0, 0));
		nPanel.add(tnPanel);
		nPanel.add(btnPanel);

		addComponent(this, bag, c, nPanel);

		c.weighty = 1.0;
		addComponent(this, bag, c, new JScrollPane(templateTable));

		//this.setLayout(new BorderLayout(0, 0));
		//this.add(this, BorderLayout.CENTER);

		/*
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;

		c.weighty = 0.0;
		c.gridheight = 1;
		GridBagLayout bag = new GridBagLayout();

		JPanel searchPanel = UIFactory.createJPanel(bag);

		JLabel label = UIFactory.createFormLabel("label.attribute");
		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(searchPanel, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(searchPanel, bag, c, attributeField);

		addComponent(searchPanel, bag, c, searchButton);

		addComponent(searchPanel, bag, c, new JSeparator());

		JPanel tnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		tnPanel.add(UIFactory.createFormLabel("label.template"));

		JPanel btnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.RIGHT, 1, 1));
		for (int i = 0; i < buttons.length; i++) {
			btnPanel.add(buttons[i]);
		}

		JPanel nPanel = UIFactory.createJPanel(new GridLayout(1, 2, 0, 0));
		nPanel.add(tnPanel);
		nPanel.add(btnPanel);

		addComponent(searchPanel, bag, c, nPanel);

		c.weighty = 1.0;
		addComponent(searchPanel, bag, c, new JScrollPane(templateTable));

		this.setLayout(new BorderLayout(0, 0));
		this.add(searchPanel, BorderLayout.CENTER);
		*/
	}

}
