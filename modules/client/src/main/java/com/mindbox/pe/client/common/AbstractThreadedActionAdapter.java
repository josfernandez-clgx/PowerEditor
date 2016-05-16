/*
 * Created on 2003. 12. 15.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.MainApplication;
import com.mindbox.pe.client.applet.UIFactory;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.1.0
 */
public abstract class AbstractThreadedActionAdapter implements ActionListener {

	protected AbstractThreadedActionAdapter() {
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		final MainApplication rootFrame = ClientUtil.getParent();
		rootFrame.setCursor(UIFactory.getWaitCursor());
		final SwingWorker worker = new SwingWorker() {
			@Override
			public Object construct() {
				try {
					performAction(arg0);
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
				finally {
					rootFrame.setCursor(UIFactory.getDefaultCursor());
				}
				return this;
			}
		};
		worker.start();
	}

	/**
	 * Performs an action here.
	 * Deleteion of <code>actionPerformed(ActionEvent event)</code> method.
	 * @param event event
	 * @throws Exception on error
	 */
	public abstract void performAction(ActionEvent event) throws Exception;
}
