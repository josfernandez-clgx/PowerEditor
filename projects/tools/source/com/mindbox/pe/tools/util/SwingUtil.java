/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mindbox.pe.tools.CanceledException;

/**
 * Swing Utility methods for PowerEditor Tools.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public final class SwingUtil {

	private static boolean sIsLookAndFeelSet = false;
	private static String sTitle = "PowerEditor Migration Tool";
	private static Component sParent = null;
	private static Dimension sScreenSize = null;

	public static boolean isEmpty(JTextField field) {
		return field.getText() == null || field.getText().trim().length() == 0;
	}

	public static boolean isEmpty(JPasswordField field) {
		return field.getPassword() == null || new String(field.getPassword()).trim().length() == 0;
	}

	/** Places the specified component at the center of ths screen.
	 * @param comp the component to centerize
	 */
	public static void centerize(java.awt.Component comp) {
		Dimension screenSize = getScreenSize();
		Dimension compSize = comp.getSize();
		int widthPad = (int) (screenSize.width - compSize.width) / 2;
		int heightPad = (int) (screenSize.height - compSize.height) / 2;
		comp.setBounds(widthPad, heightPad, compSize.width, compSize.height);
	}

	private static Dimension getScreenSize() {
		if (sScreenSize == null) {
			sScreenSize = new JFrame().getToolkit().getScreenSize();
		}
		return sScreenSize;
	}

	/** Gets the parent component
	 * @return the parent component
	 */
	public static Component getParent() {
		return sParent;
	}

	/**
	 * Sets the Swing user interface look and feel to that of the OS.
	 */
	public static void setLookAndFeelToMulti() {
		// setting the Windows look and feel
		try {
			if (sIsLookAndFeelSet) {
				javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				sIsLookAndFeelSet = false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the Swing user interface look and feel to that of the OS.
	 */
	public static void setLookAndFeelToOS() {
		// setting the Windows look and feel
		try {
			if (!sIsLookAndFeelSet) {
				javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
				sIsLookAndFeelSet = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Sets the parent componet to be used with dialogs to be displayed.
	 * @param parent the parent component
	 */
	public static void setParent(Component parent) {
		sParent = parent;
	}

	public static final void setCursorToNormal(JComponent panel) {
		if (panel == null) return;
		synchronized (panel) {
			panel.setCursor(Cursor.getDefaultCursor());
		}
	}

	public static final void setCursorToWait(JComponent panel) {
		if (panel == null) return;
		synchronized (panel) {
			panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
	}

	/** Sets the title of dialogs to be displayed to the specified title.
	 * @param title the new title
	 */
	public static void setTitle(String title) {
		sTitle = new String(title);
	}

	/** Displays an information dialog with the specified message.
	 * @param message the info message
	 */
	public static void showInfo(Object message) {
		JOptionPane.showMessageDialog(sParent, message, sTitle, JOptionPane.INFORMATION_MESSAGE);
	}

	/** Displays a warning dialog with the specified message.
	 * @param message the warning message
	 */
	public static void showWarning(Object message) {
		JOptionPane.showMessageDialog(sParent, message, sTitle, JOptionPane.WARNING_MESSAGE);
	}

	/** Displays a warning dialog with the specified message.
	 * @param message the warning message
	 */
	public static void showError(Object message) {
		JOptionPane.showMessageDialog(sParent, message, sTitle, JOptionPane.ERROR_MESSAGE);
	}

	/** Displays a confirmation dialog with yes and no buttons, asking the specified question
	 * and tests if the user has selected the yes option.
	 * @param question the question to ask
	 * @return <CODE>true</CODE> if user selected the yes option; <CODE>false</CODE>, otherwise
	 */
	public static boolean yesorno(Object question) {
		int result = JOptionPane.showConfirmDialog(sParent, question, sTitle, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		return (result == JOptionPane.YES_OPTION);
	}

	/** Displays a confirmation dialog with yes, no, and cancel buttons, asking the specified question
	 * and tests if the user has selected the yes option.
	 * A {@link CanceledException} is thrown if user select the cancel option.
	 * @param question the question to ask
	 * @return <CODE>true</CODE> if user selected the yes option; <CODE>false</CODE>, otherwise
	 * @throws CanceledException if user has selected the cancel option
	 */
	public static boolean yesnocancel(Object question) throws CanceledException {
		int result = JOptionPane.showConfirmDialog(sParent, question, sTitle, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (result == JOptionPane.CANCEL_OPTION) {
			throw new CanceledException();
		}
		else {
			return (result == JOptionPane.YES_OPTION);
		}
	}

	public static final void addComponent(JPanel panel, GridBagLayout bag, GridBagConstraints c, Component component) {
		bag.setConstraints(component, c);
		panel.add(component);
	}


	/**
	 * Displays the specified component in a new frame (JFrame), inside a scroll pane. 
	 * The title of the frame is the generatl title (set by
	 * {@link #setTitle} method) appended with the name of the component.
	 * @param comp the component to be displayed in a new frame
	 */
	public static void showAsNewFrame(JComponent comp, WindowListener wl) {

		JFrame frame = new JFrame(sTitle);
		frame.setSize(comp.getPreferredSize().width + 12, comp.getPreferredSize().height + 4);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(1, 1));
		frame.getContentPane().add(comp, BorderLayout.CENTER);
		frame.addWindowListener(wl);
		centerize(frame);
		frame.setVisible(true);
	}

	/**
	 * Displays the specified component in a new dialog (JDialog).
	 * Title of the dialog is set as the name of <code>comp</code>.
	 * @param comp the component to be displayed in a new frame
	 * @param model true for a modal dialog, false for one that allows other windows to be active at the same time
	 */
	public static void showAsNewDialog(JComponent comp, boolean model, WindowListener wl) {
		JDialog dialog = new JDialog();
		dialog.setModal(model);
		dialog.setTitle(comp.getName());

		dialog.getContentPane().setLayout(new BorderLayout(0, 0));
		dialog.getContentPane().add(comp, BorderLayout.CENTER);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		if (wl != null) {
			dialog.addWindowListener(wl);
		}

		dialog.setSize(comp.getPreferredSize().width + 12, comp.getPreferredSize().height + 4);
		centerize(dialog);

		dialog.setVisible(true);
	}

	/** Creates new SwingUtil */
	private SwingUtil() {
	}

}