/*
 * Created on Jan 26, 2006
 *
 */
package com.mindbox.pe.client.applet.report;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.model.report.CustomReportSpec;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class CustomReportPanel extends PanelBase {

	private class ViewL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			String name = (String) reportList.getSelectedValue();
			if (name != null) {
				ClientUtil.showInWebBrowser(ClientUtil.getCommunicator().generateReportURL(new CustomReportSpec(name), null));
			}
		}
	}

	private class RefreshL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			List<String> reportNameList = ClientUtil.getCommunicator().fetchCustomReportNames();
			Collections.sort(reportNameList);
			reportList.clearSelection();
			DefaultListModel model = (DefaultListModel) reportList.getModel();
			model.removeAllElements();
			for (Iterator<String> iter = reportNameList.iterator(); iter.hasNext();) {
				String element = iter.next();
				model.addElement(element);
			}
		}
	}

	private final JButton loadButton, viewButton;
	private final JList reportList;

	public CustomReportPanel() {
		loadButton = UIFactory.createJButton("button.refresh", null, new RefreshL(), null);
		viewButton = UIFactory.createJButton("button.view", null, new ViewL(), null);
		viewButton.setEnabled(false);
		reportList = new JList(new DefaultListModel());
		reportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		reportList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent arg0) {
				viewButton.setEnabled(reportList.getSelectedIndex() >= 0);
			}

		});
		initPanel();
	}

	private void initPanel() {
		JPanel bp = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
		bp.add(loadButton);
		bp.add(viewButton);

		setLayout(new BorderLayout());
		add(bp, BorderLayout.NORTH);
		add(new JScrollPane(reportList), BorderLayout.CENTER);
	}
}
