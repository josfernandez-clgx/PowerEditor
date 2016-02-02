/*
 * Created on 2004. 2. 5.
 *
 */
package com.mindbox.pe.client.common;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.popup.PopupJDialog;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public abstract class AbstractDropSelectField extends JPanel implements ActionListener {

	private static final int TASK_BAR_HEIGHT = 0;
	private static final int MIN_HEIGHT = 280;
	private static final int PREF_HEIGHT = 400;
	private static final int MIN_WIDTH = 220;

	final JButton selectButton, deleteButton, closeButton;
	private JDialog dialog = null;
	private Window window = null;
	protected final JTextField textField;
	protected final boolean forMultiSelect;

	protected AbstractDropSelectField(boolean forMultiSelect) {
		super(new BorderLayout(0, 0));
		this.forMultiSelect = forMultiSelect;

		textField = new JTextField(10);
		textField.setEditable(false);
		selectButton = UIFactory.createButton("", getFindButtonImageKey(), this, "button.tooltip.find", false);
		selectButton.setFocusable(false);
		deleteButton = UIFactory.createButton("", "image.btn.small.remove", this, "button.tooltip.clear.value", false);
		deleteButton.setFocusable(false);
		closeButton = UIFactory.createButton("Close", null, this, null, true);

		JPanel bp = UIFactory.createJPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		bp.add(selectButton);
		bp.add(deleteButton);

		add(textField, BorderLayout.CENTER);
		add(bp, BorderLayout.EAST);
	}

	protected String getFindButtonImageKey() {
		return "image.btn.small.find";
	}

	protected abstract JComponent createSelectorComponent();

	protected abstract void selectSelectedValues();

	protected abstract void selectorClosed();

	protected abstract void valueDeleted();

	public final void setAllowDelete(boolean flag) {
		deleteButton.setVisible(flag);
	}

	protected boolean hasValue() {
		return textField.getText() != null && textField.getText().trim().length() > 0;
	}

	protected final Window getWindow() {
		if (window == null) {
			window = findWindow(this.getParent());
		}
		return window;
	}

	private Window findWindow(Container container) {
		return (container == null || container instanceof Window) ? (Window) container : findWindow(container.getParent());
	}

	public final void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == selectButton) {
			showSelector();
		}
		else if (arg0.getSource() == deleteButton) {
			textField.setText("");
			valueDeleted();
		}
		else if (arg0.getSource() == closeButton) {
			closeWindow();
		}
	}

	void showSelector() {
		ClientUtil.getParent().setCursor(UIFactory.getWaitCursor());
		try {
			positionDialog(getDialog(), textField.getLocationOnScreen());

			selectSelectedValues();

			selectButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
		finally {
			ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
		}
		getDialog().setVisible(true);
	}

	private JDialog getDialog() {
		if (dialog == null) {
			if (getWindow() instanceof Dialog) {
				dialog = new PopupJDialog((Dialog) getWindow());
			}
			else if (getWindow() instanceof Frame) {
				dialog = new PopupJDialog((Frame) getWindow());
			}
			else {
				dialog = new PopupJDialog();
			}
			dialog.setResizable(true);
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.getContentPane().add(createSelectorComponent(), BorderLayout.CENTER);
			dialog.getContentPane().add(closeButton, BorderLayout.SOUTH);
            dialog.setModal(true);
		}
		return dialog;
	}

	private void positionDialog(JDialog dialog, Point point) {
		int remaining = UIFactory.getScreenSize().height - point.y - TASK_BAR_HEIGHT;
		if (remaining >= MIN_HEIGHT) {
			//if (minHeight )
			int height = Math.min(PREF_HEIGHT, remaining);
			dialog.setBounds(point.x, (point.y + textField.getHeight() + 0), this.getWidth(), //textField.getWidth(),
					height - textField.getHeight());
		}
		else {
			int height = Math.min(MIN_HEIGHT, (point.y - textField.getHeight()));
			dialog.setBounds(point.x, (point.y - height), this.getWidth()/*textField.getWidth()*/, height);
		}
	}

	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		return new Dimension(Math.max(dim.width, MIN_WIDTH), dim.height);
	}

	public Dimension getMinimumSize() {
		Dimension dim = super.getPreferredSize();
		return new Dimension(Math.max(dim.width, MIN_WIDTH), dim.height);
	}

	protected final void closeWindow() {
		selectorClosed();
		if (dialog != null) {
			dialog.setVisible(false);
		}
		selectButton.setEnabled(true);
		deleteButton.setEnabled(true);
	}

	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		selectButton.setEnabled(flag);
		deleteButton.setEnabled(flag);
	}

	private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	public final void addChangeListener(ChangeListener listener) {
		synchronized (changeListeners) {
			if (!changeListeners.contains(listener)) changeListeners.add(listener);
		}
	}

	public final void removeChangeListener(ChangeListener listener) {
		synchronized (changeListeners) {
			changeListeners.remove(listener);
		}
	}

	protected final void notifyChangeListeners() {
		synchronized (changeListeners) {
			for (Iterator<ChangeListener> it = changeListeners.iterator(); it.hasNext();) {
				it.next().stateChanged(new ChangeEvent(this));
			}
		}
	}

}
