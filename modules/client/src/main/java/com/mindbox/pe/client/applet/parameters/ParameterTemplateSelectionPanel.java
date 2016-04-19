/*
 * Created on 2003. 12. 15.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.applet.parameters;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.IDNameObjectCellRenderer;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.model.template.ParameterTemplate;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class ParameterTemplateSelectionPanel extends PanelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private class TemplateListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());
			try {
				if (templateList.getSelectedIndex() != -1) {
					setSelection((ParameterTemplate) templateList.getModel().getElementAt(templateList.getSelectedIndex()));
				}
				else {
					clearSelection();
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
			}
		}
	}

	private final ParameterDetailPanel detailPanel;
	private final Logger logger;
	private final JPanel viewPanel;

	private final JList templateList;

	protected ParameterTemplateSelectionPanel(ParameterDetailPanel detailPanel) {
		this.detailPanel = detailPanel;
		this.logger = Logger.getLogger(getClass());

		this.templateList = new JList(EntityModelCacheFactory.getInstance().getParameterTemplateComboModel(false));
		templateList.setCellRenderer(new IDNameObjectCellRenderer("image.node.template"));

		viewPanel = new JPanel(new BorderLayout(0, 0));
		initPanel();

		templateList.addListSelectionListener(new TemplateListListener());
	}

	private void clearSelection() {
		try {
			detailPanel.setTemplate(null);
		}
		catch (Exception ex) {
			logger.error(ex);
			ClientUtil.handleRuntimeException(ex);
		}
	}

	private void initPanel() {
		viewPanel.add(new JScrollPane(templateList), BorderLayout.CENTER);
		setLayout(new BorderLayout(2, 2));
		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.title.parameter.template.selection")));
		add(viewPanel, BorderLayout.CENTER);
	}

	private void setSelection(ParameterTemplate td) {
		try {
			detailPanel.setTemplate(td);
		}
		catch (Exception ex) {
			logger.error("Failed to check rule existence for columns in " + td, ex);
			ClientUtil.handleRuntimeException(ex);
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.templateList.setEnabled(enabled);
	}

}
