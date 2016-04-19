package com.mindbox.pe.client.applet.admin.role;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.IClientConstants;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.CheckList;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.exceptions.CanceledException;

public class RoleDetailsPanel extends PanelBase implements IClientConstants, PowerEditorTabPanel, ListSelectionListener {

	class RoleDetailsAdapter extends AbstractThreadedActionAdapter {
		RoleDetailsAdapter() {
		}

		public void performAction(ActionEvent actionevent) {
			Object obj = actionevent.getSource();
			if (obj == saveRoleButton) {
				if (theRole != null && isDirty())
					saveDisplayRole();
				else
					ClientUtil.printInfo("Save not required!");
			}
			else {
				ClientUtil.printWarning("Unknown Action Event= " + actionevent);
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	protected static String NAME_FORM_LBL = "label.name";
	protected static String PRIVILEGES_FORM_LBL = "label.privileges";

	protected static String DETAILS_TITLE_LBL = "label.title.user.roles";

	private JTextField nameField;
	private CheckList privilegeListField;
	private JButton saveRoleButton;
	protected Role theRole;
	protected boolean viewOnly;
	private boolean isRoleNew;

	public RoleDetailsPanel() {
		super();
		nameField = new JTextField(10);
		privilegeListField = new CheckList();
		saveRoleButton = null;
		theRole = null;
		viewOnly = true;
		isRoleNew = false;
		initComponents();
		addComponents();
		setEnabled(false);
	}

	protected void addComponents() {
		TitledBorder titledborder = UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel(DETAILS_TITLE_LBL));
		setBorder(titledborder);

		JPanel btnPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		btnPanel.add(saveRoleButton);

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, btnPanel);

		addFormSeparator(this, bag, c);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createLabel(NAME_FORM_LBL));

		c.gridwidth = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(this, bag, c, nameField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createLabel(PRIVILEGES_FORM_LBL, SwingConstants.TOP));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		addComponent(this, bag, c, new JScrollPane(privilegeListField));
	}

	private boolean cancelEdit(int roleID) {
		if (roleID < 0) {
			return true;
		}
		try {
			ClientUtil.getCommunicator().unlock(roleID, PeDataType.ROLE);
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}

	}

	protected void clearForm() {
		theRole = null;
		nameField.setText("");
		privilegeListField.getSelectionModel().clearSelection();
	}

	private boolean confirmProceed() {
		Boolean result = ClientUtil.getInstance().showSaveDiscardCancelDialog();
		if (result == null) {
			return false;
		}
		else {
			if (result.booleanValue()) {
				saveDisplayRole();
			}
			return true;
		}
	}

	private void deselectAllBut(final List<Privilege> privileges) {
		privilegeListField.removeListSelectionListener(this);
		try {
			privilegeListField.clearSelection();
			for (final Privilege privilege : privileges) {
				privilegeListField.setSelectedValue(privilege, true);
			}
		}
		finally {
			privilegeListField.addListSelectionListener(this);
		}
	}

	@Override
	public void discardChanges() {
		clearForm();
	}

	public void displayRoleDetails(Role role, boolean flag, boolean flag1) {
		if (role != null) {
			if (getDisplayRole() != null) {
				if (role.getID() == getDisplayRole().getID()) {
					ClientUtil.printInfo("Same Role!");
					if (flag == isViewOnly()) return;
				}
				if (isDirty() && !confirmProceed()) return;
				if (!isViewOnly() && !cancelEdit(getDisplayRole().getID())) {
					ClientUtil.printError("Unable to Cancel Edit");
					return;
				}
			}
			isRoleNew = flag1;
			if (!isNew() && !flag) {
				boolean flag2 = lockRoleOnServer(role);
				if (!flag2) return;
			}
			populateForm(role, flag);
			setEnabled(!flag);
		}
		else {
			JOptionPane.showMessageDialog(ClientUtil.getApplet(), ClientUtil.getInstance().getMessage("SelectItemFirstMsg"), ClientUtil.getInstance().getMessage("WarningMsgTitle"), 2);
			clearForm();
		}
	}

	public Role getDisplayRole() {
		return theRole;
	}

	@Override
	public boolean hasUnsavedChanges() {
		return (theRole != null && isPrivilegesDirty());
	}

	protected void initComponents() {
		String s = ClientUtil.getInstance().getLabel("button.save");
		JButton jbutton = UIFactory.createButton(s, "image.btn.small.save", null, null);
		saveRoleButton = jbutton;
		RoleDetailsAdapter roledetailsadapter = new RoleDetailsAdapter();
		jbutton.addActionListener(roledetailsadapter);

		privilegeListField.setModel(EntityModelCacheFactory.getInstance().getPrivilegeListModel());
		privilegeListField.setSelectionMode(2);
		privilegeListField.setVisibleRowCount(10);
		privilegeListField.addListSelectionListener(this);
	}

	protected boolean isDirty() {
		if (theRole == null) {
			ClientUtil.printError("No Role set!");
			return false;
		}
		if (isViewOnly()) {
			return false;
		}
		if (isNew()) {
			return true;
		}
		else {
			boolean flag = !theRole.getName().equals(nameField.getText()) || isPrivilegesDirty();
			return flag;
		}
	}

	private boolean isNew() {
		return isRoleNew;
	}

	private boolean isPrivilegesDirty() {
		boolean flag = false;
		Object aobj[] = privilegeListField.getSelectedValues();
		List<Privilege> list = theRole.getPrivileges();
		if (list == null || list.size() == 0) {
			return aobj != null && aobj.length > 0;
		}
		if (aobj.length != list.size()) {
			return true;
		}
		for (int i = 0; i < aobj.length; i++) {
			Privilege privilege = (Privilege) aobj[i];
			if (list.contains(privilege)) continue;
			flag = true;
			break;
		}
		return flag;
	}

	private boolean isViewOnly() {
		return viewOnly;
	}

	private boolean lockRoleOnServer(Role role) {
		try {
			ClientUtil.getCommunicator().lock(role.getID(), PeDataType.ROLE);
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	protected void populateForm(Role role, boolean flag) {
		theRole = role;
		viewOnly = flag;
		if (role == null) {
			clearForm();
			viewOnly = true;
			isRoleNew = false;
			setEnabled(!flag);
			return;
		}
		ClientUtil.printInfo("Calling PopulateForm with " + role.toString());
		nameField.setText(role.getName());
		List<Privilege> list = role.getPrivileges();

		try {
			privilegeListField.removeListSelectionListener(this);

			privilegeListField.clearSelection();
			if (list != null) {
				for (final Privilege privilege : list) {
					if (privilege != null) {
						privilegeListField.setSelectedValue(privilege, true);
					}
				}
			}
		}
		finally {
			privilegeListField.addListSelectionListener(this);
		}
	}

	@Override
	public void saveChanges() throws CanceledException, ServerException {
		saveRole_aux();
	}

	private boolean saveDisplayRole() {
		try {
			saveRole_aux();
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	private void saveRole_aux() throws ServerException {
		uploadFromGUI();
		boolean isNew = theRole.getID() == -1;
		int roleID = ClientUtil.getCommunicator().save(theRole, false);
		theRole.setID(roleID);
		if (isNew) {
			EntityModelCacheFactory.getInstance().addRole(theRole);// adds role in client cache
			isRoleNew = false;// (TT 1629) since save has been performed, set mIsnew flag to false
		}
		// TT 2127
		firePropertyChange("DetailUpdated", null, theRole);
		populateForm(theRole, true);
		setEnabled(false);
	}

	private List<Privilege> searchPrivilegesToKeepForValueChanged() {
		final List<Privilege> list = new ArrayList<Privilege>();
		for (final Object selectedValue : privilegeListField.getSelectedValues()) {
			final Privilege privilege = Privilege.class.cast(selectedValue);
			if (privilege.getName().equals(PrivilegeConstants.PRIV_MANAGE_ROLES) || privilege.getName().equals(PrivilegeConstants.PRIV_MANAGE_USERS)) {
				list.add(privilege);
			}
		}
		return list;
	}

	@Override
	public void setEnabled(boolean flag) {
		saveRoleButton.setEnabled(flag);
		nameField.setEditable(flag);
		privilegeListField.setEnabled(flag);
		super.setEnabled(flag);
	}

	protected void uploadFromGUI() {
		theRole.setName(nameField.getText());
		Object aobj[] = privilegeListField.getSelectedValues();
		LinkedList<Privilege> linkedlist = new LinkedList<Privilege>();
		for (int i = 0; i < aobj.length; i++) {
			Privilege privilege = (Privilege) aobj[i];
			linkedlist.add(privilege);
		}
		theRole.setPrivileges(linkedlist);
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		// TT-54: if manage roles or manage users is selected, remove all the other privileges but manager roles/users
		final List<Privilege> privilegesToKeep = searchPrivilegesToKeepForValueChanged();
		if (!privilegesToKeep.isEmpty()) {
			deselectAllBut(privilegesToKeep);
		}
	}
}
