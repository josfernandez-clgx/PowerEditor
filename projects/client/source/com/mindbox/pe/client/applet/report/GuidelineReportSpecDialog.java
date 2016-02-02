/*
 * Created on 2004. 6. 29.
 */
package com.mindbox.pe.client.applet.report;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.report.GuidelineReportSpec;

/**
 * 
 *
 * @author kim
 * @since PowerEditor  
 */
public class GuidelineReportSpecDialog extends JPanel {


	public static GuidelineReportSpec newGuidelineReportSpec(Frame owner) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.new.report.guideline"));
		GuidelineReportSpecDialog instance = new GuidelineReportSpecDialog(dialog, null);
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.reportSpec;
	}


	private class AcceptL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (updateSpec()) {
				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			detailPanel.setGuidelineReportSpec(null);
			reportSpec = null;
			dialog.dispose();
		}
	}

	private final JDialog dialog;
	private final GuidelineReportSpecPanel detailPanel;
	private GuidelineReportSpec reportSpec;

	private GuidelineReportSpecDialog(JDialog dialog, GuidelineReportSpec reportSpec) {
		this.dialog = dialog;
		this.reportSpec = reportSpec;
		this.detailPanel = GuidelineReportSpecPanel.getInstance();
		
		setSize(440,320);
		initPanel();
		
		detailPanel.setGuidelineReportSpec(reportSpec);
	}

	private void initPanel() {
		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());
		
		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);
		
		setLayout(new BorderLayout(4,4));
		add(detailPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private boolean updateSpec() {
		GuidelineReportSpec reportSpecFromPanel = detailPanel.getGuidelineReportSpec();

		if (reportSpecFromPanel.getLocalFilename() == null || reportSpecFromPanel.getLocalFilename().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] {
				ClientUtil.getInstance().getLabel("label.file.target") });
			return false;
		}
		this.reportSpec = reportSpecFromPanel;
		
		return true;

	}
}