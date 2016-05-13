/*
 * Created on 2003. 12. 16.
 */
package com.mindbox.pe.client.common.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.guidelines.manage.GuidelineContextSelectionPanel;
import com.mindbox.pe.client.common.context.DefaultContextDetailPanel;
import com.mindbox.pe.model.GuidelineContext;

/**
 * Guideline context edit dialog.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
public class GuidelineContextDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	/**
	 * Edit guideline context.
	 * @param contexts the context to edit
	 * @return result contexts
	 */
	public static GuidelineContext[] editContext(GuidelineContext[] contexts) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.edit.guideline.context"));
		GuidelineContextDialog instance = new GuidelineContextDialog(dialog, contexts);
		UIFactory.addToDialog(dialog, instance);

		dialog.setSize(Math.max(UIFactory.getScreenSize().width - 260, 640), Math.max(UIFactory.getScreenSize().height - 94, 470));
		UIFactory.centerize(dialog);
		dialog.setVisible(true);

		return instance.contexts;
	}

	private class AcceptL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (updateFromFields()) {
				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			contexts = null;
			dialog.dispose();
		}
	}

	private final JDialog dialog;


	private final DefaultContextDetailPanel contextPanel;
	private final GuidelineContextSelectionPanel selectionPanel;
	private GuidelineContext[] contexts;

	private GuidelineContextDialog(JDialog dialog, GuidelineContext[] contexts) {
		this.dialog = dialog;
		contextPanel = new DefaultContextDetailPanel();
		selectionPanel = new GuidelineContextSelectionPanel(contextPanel.getContextHolder());
		this.contexts = contexts;

		initDialog();

		if (contexts != null && contexts.length > 0) {
			contextPanel.getContextHolder().setContextElemens(contexts);
		}
		else {
			contextPanel.getContextHolder().clearContext();
		}
		//setSize(632,474);
	}

	private void initDialog() {
		selectionPanel.setMinimumSize(new Dimension(300, 100));
		contextPanel.setMinimumSize(new Dimension(200, 100));
		JSplitPane sp = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp.setTopComponent(selectionPanel);
		sp.setBottomComponent(contextPanel);

		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		setLayout(new BorderLayout(1, 1));
		add(sp, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		//setBorder(UIFactory.createTitledBorder("Guidelind Context"));
	}

	private boolean updateFromFields() {
		contexts = contextPanel.getContextHolder().getGuidelineContexts();
		return true;
	}

}