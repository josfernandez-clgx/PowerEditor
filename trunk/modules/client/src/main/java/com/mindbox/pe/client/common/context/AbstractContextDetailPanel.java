/*
 * Created on Oct 9, 2003
 */
package com.mindbox.pe.client.common.context;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.event.ContextChangeListener;
import com.mindbox.pe.common.GuidelineContextHolder;

/**
 * 
 * @author Gene Kim
 * @author MindBox
 */
public abstract class AbstractContextDetailPanel extends PanelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private class RemoveL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			contextPanel.removeContext(contextPanel.getSelectedGenericEntities());
			contextPanel.removeContext(contextPanel.getSelectedGenericCategories());
		}
	}

	private class RemoveAllL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			contextPanel.clearContext();
		}
	}

	protected final GuidelineContextPanel contextPanel;
	private final JButton removeButton;
	private final JButton removeAllButton;

	protected AbstractContextDetailPanel() {
		super();

		this.contextPanel = new GuidelineContextPanel("button.edit.context", false);
		this.removeButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.remove.context"), "image.btn.small.back", new RemoveL(), null);
		this.removeAllButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.clear.context"), "image.btn.small.clear", new RemoveAllL(), null);

		initPanel();
	}

	public final void addContextChangeListener(ContextChangeListener ccListener) {
		this.contextPanel.addContextChangeListener(ccListener);
	}

	public final void removeContextChangeListener(ContextChangeListener ccListener) {
		this.contextPanel.removeContextChangeListener(ccListener);
	}

	private void initPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		buttonPanel.add(removeButton);
		buttonPanel.add(removeAllButton);

		JPanel nPanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		nPanel.add(buttonPanel, BorderLayout.NORTH);
		nPanel.add(contextPanel.getJPanel(), BorderLayout.CENTER);
		nPanel.setBorder(UIFactory.createTitledBorder("Selected Context"));

		setLayout(new BorderLayout(0, 0));
		add(nPanel, BorderLayout.CENTER);
	}

	public final GuidelineContextHolder getContextHolder() {
		return contextPanel;
	}
}