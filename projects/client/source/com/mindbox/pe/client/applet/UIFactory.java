package com.mindbox.pe.client.applet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.ShowHideImageButton;
import com.mindbox.pe.client.common.TypeEnumMultiSelectPanel;

import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.event.Action3Listener;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TypeEnumValue;

/**
 * Factory for various UI element that conform to PowerEditor look and feel.
 * 
 * @since PowerEditor 1.0
 */
public final class UIFactory {

	private static final Insets BUTTON_INSETS = new Insets(2, 4, 2, 4);

	private static Cursor waitCursor = null;
	private static Cursor normalCursor = null;
	private static Dimension sScreenSize = null;

	public static ButtonPanel create3ButtonPanel(final Action3Listener a3l, boolean showEditButton) {
		JButton newButton, deleteButton, editButton = null;
		if (a3l == null) throw new NullPointerException("Action3Listener cannot be null");
		newButton = UIFactory.createButton("", "image.btn.small.add", new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				a3l.newPerformed(e);
			}
		}, "button.tooltip.create.new");
		if (showEditButton) {
			editButton = UIFactory.createButton("", "image.btn.small.edit", new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					a3l.editPerformed(e);
				}
			}, "button.tooltip.edit");
		}
		deleteButton = UIFactory.createButton("", "image.btn.small.delete", new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				a3l.deletePerformed(e);
			}
		}, "button.tooltip.delete");
		newButton.setEnabled(true);

		return new ButtonPanel((showEditButton ? new JButton[] { newButton, editButton, deleteButton } : new JButton[] {
				newButton,
				deleteButton }), FlowLayout.LEFT);
	}

	public static JPanel createTogglePanel(JLabel label, JComponent component, boolean wrapComponentWithScrollPane) {
		if (label == null) throw new NullPointerException("label cannot be null");

		JPanel buttonPanel = UIFactory.createBorderLayoutPanel(0, 0);
		label.setFont(PowerEditorSwingTheme.boldFont);

		buttonPanel.add(label, BorderLayout.CENTER);
		buttonPanel.add(new ShowHideImageButton(component), BorderLayout.WEST);
		buttonPanel.setBackground(PowerEditorSwingTheme.blueShadowColor);

		JPanel panel = UIFactory.createBorderLayoutPanel(2, 2);
		panel.setBorder(BorderFactory.createLineBorder(PowerEditorSwingTheme.blueShadowColor, 2));
		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.add((wrapComponentWithScrollPane ? new JScrollPane(component) : component), BorderLayout.CENTER);

		component.setMinimumSize(new Dimension(20, 0));
		return panel;
	}

	/**
	 * Places the specified component at the center of ths screen.
	 * 
	 * @param comp
	 *            the component to centerize
	 */
	public static void centerize(Component comp) {
		Dimension screenSize = getScreenSize();
		Dimension compSize = comp.getSize();
		int widthPad = (int) (screenSize.width - compSize.width) / 2;
		int heightPad = (int) (screenSize.height - compSize.height) / 2;
		comp.setBounds(widthPad, heightPad, compSize.width, compSize.height);
	}

	public static Dimension getScreenSize() {
		if (sScreenSize == null) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			if (ge != null) {
				sScreenSize = new Dimension(ge.getMaximumWindowBounds().width, ge.getMaximumWindowBounds().height);
			}
			if (sScreenSize == null) {
				sScreenSize = new JFrame().getToolkit().getScreenSize();
			}
		}
		return sScreenSize;
	}

	public static boolean isEmpty(JTextField field) {
		return field.getText() == null || field.getText().trim().length() == 0;
	}

	public static boolean isEmpty(JPasswordField field) {
		return field.getPassword() == null || new String(field.getPassword()).trim().length() == 0;
	}

	public static final void addComponent(JPanel panel, GridBagLayout bag, GridBagConstraints c, Component component) {
		bag.setConstraints(component, c);
		panel.add(component);
	}

	public static final void addComponent(Container container, GridBagLayout bag, GridBagConstraints c, Component component) {
		bag.setConstraints(component, c);
		container.add(component);
	}

	public static TitledBorder createTitledBorder(String title) {
		TitledBorder titledBorder = new TitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title);
		titledBorder.setTitleFont(PowerEditorSwingTheme.windowtitlefont);
		return titledBorder;
	}

	public static JSplitPane createSplitPane(int newOrientation) {
		JSplitPane sp = new JSplitPane(newOrientation);
		sp.setDividerSize(PowerEditorSwingTheme.DIVIDER_SIZE);
		sp.setOneTouchExpandable(true);
		setLookAndFeel(sp);
		return sp;
	}

	public static JSplitPane createSplitPane(int newOrientation, Component comp1, Component comp2) {
		JSplitPane sp = new JSplitPane(newOrientation, comp1, comp2);
		sp.setDividerSize(PowerEditorSwingTheme.DIVIDER_SIZE);
		sp.setOneTouchExpandable(true);
		return sp;
	}

	public static JTree createTree(TreeModel treeModel, TreeCellRenderer renderer) {
		JTree tree = new JTree(treeModel);
		tree.setCellRenderer(renderer);
		tree.setRootVisible(false);
		tree.setScrollsOnExpand(true);
		tree.setDoubleBuffered(true);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		return tree;
	}

	/**
	 * Constructs a new JButton conforming to the application's look and feel.
	 * 
	 * @param label
	 *            the label of the button
	 * @param iconKey
	 *            the icon file key for the button. If <code>null</code> the
	 *            button has no icon
	 * @param al
	 *            the action listener for the button
	 * @param tooltipKey
	 *            the tooltip text resource key. If <code>null</code> the
	 *            button has not tool tip text
	 * @return new JButton instance with the specified details
	 */
	public static JButton createButton(String label, String iconKey, ActionListener al, String tooltipKey, boolean showBorder) {
		JButton button = null;
		if (iconKey == null) {
			button = new JButton(label);
		}
		else {
			button = new JButton(label, ClientUtil.getInstance().makeImageIcon(iconKey));
		}

		if (al != null) button.addActionListener(al);
		if (tooltipKey != null) button.setToolTipText(ClientUtil.getInstance().getLabel(tooltipKey));

		button.setMargin(BUTTON_INSETS);
		button.setOpaque(true);
		button.setBackground(PowerEditorSwingTheme.buttonBackgroundColor);
		button.setBorderPainted(showBorder);
		return button;
	}

	public static JButton createButton(String label, String iconKey, ActionListener al, String tooltipKey) {
		return createButton(label, iconKey, al, tooltipKey, true);
	}

	public static JButton createJButton(String labelKey, String iconKey, ActionListener al, String tooltipKey) {
		return createButton(ClientUtil.getInstance().getLabel(labelKey), iconKey, al, tooltipKey, true);
	}

	public static JPasswordField createPasswordField() {
		JPasswordField field = new JPasswordField();
		setLookAndFeel(field);
		return field;
	}

	public static final void populateComboBox(JComboBox jcombobox, Object as[]) {
		for (int i = 0; i < as.length; i++)
			jcombobox.addItem(as[i]);
	}

	public static TypeEnumValueComboBox createStatusComboBox(boolean hasEmpty) {
		return createTypeEnumComboBox(TypeEnumValue.TYPE_STATUS, hasEmpty, false);
	}

	public static JComboBox createComboBox() {
		JComboBox combo = new JComboBox();
		setLookAndFeel(combo);
		return combo;
	}

	public static TypeEnumValueComboBox createTypeEnumComboBox(String typeKey) {
		return createTypeEnumComboBox(typeKey, false, true);
	}

	public static TypeEnumValueComboBox createTypeEnumComboBox(String typeKey, boolean hasEmptyValue, boolean sortValues) {
		TypeEnumValueComboBox combo = new TypeEnumValueComboBox(EntityModelCacheFactory.getInstance().getTypeEnumComboModel(
				typeKey,
				hasEmptyValue,
				sortValues));
		return combo;
	}

	public static TypeEnumValueComboBox createTypeEnumComboBoxForAttributeMap(String attributeMap, boolean hasEmptyValue, boolean sortValues) {
		TypeEnumValueComboBox combo = new TypeEnumValueComboBox(
				EntityModelCacheFactory.getInstance().getTypeEnumComboModelForDomainAttribute(attributeMap, hasEmptyValue, sortValues));
		return combo;
	}

	public static TypeEnumMultiSelectPanel createTypeEnumMultiSelectPanel(String propName, String typeKey, boolean required,
			boolean sortValues) {
		TypeEnumMultiSelectPanel panel = new TypeEnumMultiSelectPanel(
				propName,
				EntityModelCacheFactory.getInstance().getTypeEnumComboModel(typeKey, false, sortValues),
				required);
		return panel;
	}

	public static TypeEnumMultiSelectPanel createTypeEnumMultiSelectPanelForAttributeMap(String propName, String attributeMap,
			boolean required, boolean sortValues) {
		TypeEnumMultiSelectPanel panel = new TypeEnumMultiSelectPanel(
				propName,
				EntityModelCacheFactory.getInstance().getTypeEnumComboModelForDomainAttribute(attributeMap, false, sortValues),
				required);
		return panel;
	}

	public static JCheckBox createCheckBox(String labelKey) {
		JCheckBox cb = new JCheckBox(((labelKey == null || labelKey.length() == 0)
				? ""
				: ClientUtil.getInstance().getLabel(labelKey.trim())));
		setLookAndFeel(cb);
		cb.setBorderPaintedFlat(true);
		return cb;
	}

	public static JRadioButton createRaiodButton(String labelKey) {
		JRadioButton b = new JRadioButton(((labelKey == null || labelKey.length() == 0) ? "" : ClientUtil.getInstance().getLabel(
				labelKey.trim())));
		setLookAndFeel(b);
		return b;
	}

	public static JLabel createLabel(String labelKey) {
		return new JLabel(ClientUtil.getInstance().getLabel(labelKey));
	}

	public static JLabel createLabel(String labelKey, Object[] params) {
		return new JLabel(ClientUtil.getInstance().getLabel(labelKey, params));
	}

	public static JLabel createLabel(GenericEntityType type) {
		return new JLabel(ClientUtil.getInstance().getLabel(type));
	}

	public static JLabel createFormLabel(String labelKey) {
		return createFormLabel(labelKey, false);
	}

	public static JLabel createFormLabel(String labelKey, boolean isRequired) {
		return createFormLabel(labelKey, labelKey, isRequired);
	}

	public static JLabel createFormLabel(String labelKey, String defaultLabel, boolean isRequired) {
		return createFormLabel(labelKey, defaultLabel, SwingConstants.TOP, isRequired);
	}

	public static JLabel createFormLabel(String labelKey, String defaultLabel, int verticalAlignment, boolean isRequired) {
		JLabel label = new JLabel(ClientUtil.getInstance().getLabel(labelKey, defaultLabel) + ":");
		label.setVerticalAlignment(verticalAlignment);
		if (isRequired) {
			label.setFont(PowerEditorSwingTheme.boldFont);
		}
		return label;
	}

	public static JLabel createFormLabel(GenericEntityType type) {
		return new JLabel(ClientUtil.getInstance().getLabel(type) + ":");
	}

	public static JLabel createFormLabel(CategoryTypeDefinition def) {
		return new JLabel(ClientUtil.getInstance().getLabel(def) + ":");
	}

	public static JLabel createLabel(String labelKey, int verticalAlignment) {
		JLabel label = createLabel(labelKey);
		label.setVerticalAlignment(verticalAlignment);
		return label;
	}

	public static JPanel createJPanel() {
		JPanel panel = new JPanel();
		setLookAndFeel(panel);
		return panel;
	}

	public static JPanel createBorderLayoutPanel(int x, int y) {
		return createJPanel(new BorderLayout(x, y));
	}

	public static JPanel createFlowLayoutPanel(int alignment, int x, int y) {
		return createJPanel(new FlowLayout(alignment, x, y));
	}

	public static JPanel createFlowLayoutPanelLeftAlignment(int x, int y) {
		return createJPanel(new FlowLayout(FlowLayout.LEFT, x, y));
	}

	public static JPanel createFlowLayoutPanelCenterAlignment(int x, int y) {
		return createJPanel(new FlowLayout(FlowLayout.CENTER, x, y));
	}

	public static JPanel createJPanel(LayoutManager layout) {
		JPanel panel = new JPanel(layout);
		setLookAndFeel(panel);
		return panel;
	}

	public static JPanel createTitledPanel(String title) {
		JPanel panel = createJPanel();
		panel.setBorder(createTitledBorder(title));
		return panel;
	}

	public static JPanel createTitledPanel(String title, LayoutManager layout) {
		JPanel panel = createJPanel(layout);
		panel.setBorder(createTitledBorder(title));
		return panel;
	}

	public static void setLookAndFeel(JComponent component) {
		component.setOpaque(true);
		component.setDoubleBuffered(true);
	}

	public static JList createList() {
		JList list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		return list;
	}

	public static JList createList(ListModel model) {
		JList list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		return list;
	}

	public static Cursor getDefaultCursor() {
		if (normalCursor == null) {
			normalCursor = Cursor.getDefaultCursor();
		}
		return normalCursor;
	}

	public static Cursor getWaitCursor() {
		if (waitCursor == null) {
			waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		}
		return waitCursor;
	}

	public static void addToToolbar(JToolBar toolBar, Action action, String tooltip) {
		JButton button = new JButton();
		button.setAction(action);
		button.setBorderPainted(false);
		button.setFocusable(false);
		if (tooltip != null) {
			button.setToolTipText(tooltip);
		}
		toolBar.add(button);
	}

	public static interface DialogButtonListener {

		public boolean acceptAction(String actionValue);
	}

	public static JDialog createAsModelDialog(String titleKey, JPanel panel) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle(ClientUtil.getInstance().getLabel(titleKey));
		UIFactory.addToDialog(dialog, panel);
		return dialog;
	}


	public static void addToDialog(JDialog dialog, JPanel panel) {
		dialog.getContentPane().setLayout(new BorderLayout(4, 4));
		dialog.getContentPane().add(panel, BorderLayout.CENTER);
		dialog.setSize(panel.getWidth() + 8, panel.getHeight() + 8);
		centerize(dialog);
	}

	private UIFactory() {
		super();
	}

	/*	
	 *  Recursively enabling/disabling a container and all its components
	*/
	public static List<Component> disableContainerComponents(Container root) {
		// generate the enabled list first, then disable the components.
		List<Component> enabledComponetList = findEnabledContainerComponents(root, new ArrayList<Component>());
		for (Component comp : enabledComponetList) {
			comp.setEnabled(false);
		}
		return enabledComponetList;
	}

	public static void enableContainerComponents(List<Component> enabledComponetList) {
		for (Component comp : enabledComponetList) {
			comp.setEnabled(true);
		}
	}

	private static List<Component> findEnabledContainerComponents(Container root, List<Component> enabledComponetList) {
		// recurse root and adding all components found which are currently enabled
		int cnt = root.getComponentCount();
		for (int i = 0; i < cnt; i++) {
			Component child = root.getComponent(i);
			if (child instanceof Container) {
				findEnabledContainerComponents((Container) child, enabledComponetList);
			}
			else {
				if (child.isEnabled()) enabledComponetList.add(child);
			}
		}
		if (root.isEnabled()) enabledComponetList.add(root);
		return enabledComponetList;
	}

	public static List<Component> disableContainerComponents(Container root, List<Component> excludeList) {
		if (excludeList == null) return null;
		// generate the enabled list first, then disable the components.
		List<Component> enabledComponetList = findEnabledContainerComponents(root, new ArrayList<Component>(), excludeList);
		for (Component comp : enabledComponetList) {
			comp.setEnabled(false);
		}
		return enabledComponetList;
	}

	private static List<Component> findEnabledContainerComponents(Container root, List<Component> enabledComponetList,
			List<Component> excludeList) {
		// recurse root and adding all components found which are currently enabled
		int cnt = root.getComponentCount();
		for (int i = 0; i < cnt; i++) {
			Component child = root.getComponent(i);

			if (!excludeList.contains(child)) {
				if (child instanceof Container) {
					findEnabledContainerComponents((Container) child, enabledComponetList, excludeList);
				}
				else {
					if (child.isEnabled()) enabledComponetList.add(child);
				}
			}
		}
		if (root.isEnabled()) enabledComponetList.add(root);
		return enabledComponetList;
	}

}