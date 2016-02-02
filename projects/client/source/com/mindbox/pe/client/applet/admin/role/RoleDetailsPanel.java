package com.mindbox.pe.client.applet.admin.role;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.IClientConstants;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.CheckList;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.exceptions.CanceledException;

public class RoleDetailsPanel extends PanelBase implements IClientConstants, PowerEditorTabPanel {

	protected static String NAME_FORM_LBL = "label.name";
	protected static String PRIVILEGES_FORM_LBL = "label.privileges";
	protected static String DETAILS_TITLE_LBL = "label.title.user.roles";
	
	class RoleDetailsAdapter extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent actionevent) {
			Object obj = actionevent.getSource();
			if (obj == saveRoleButton) {
				if (mRole != null && isDirty())
					saveDisplayRole();
				else
					ClientUtil.printInfo("Save not required!");
			}
			else {
				ClientUtil.printWarning("Unknown Action Event= " + actionevent);
			}
		}

		RoleDetailsAdapter() {}
	}

	protected void populateForm(Role role, boolean flag) {
		mRole = role;
		mIsViewOnly = flag;		
		if (role == null) {
			clearForm();
			mIsViewOnly = true;
			mIsNew = false;
			setEnabled(!flag);
			return;
		}
		ClientUtil.printInfo("Calling PopulateForm with " + role.toString());
		mNameField.setText(role.getName());
		List<Privilege> list = role.getPrivileges();
		privilegeListField.clearSelection();
		if (list != null) {
			Privilege privilege;
			for (Iterator<Privilege> iterator = list.iterator(); iterator.hasNext();) {
				privilege = iterator.next();
				if (privilege != null) {
					privilegeListField.setSelectedValue(privilege, true);
				}
			}
		}
	}

	public boolean hasUnsavedChanges() {
		return (mRole != null && isPrivilegesDirty());
	}
	
	public void saveChanges() throws CanceledException, ServerException {
		saveRole_aux();
	}
	
	private boolean isPrivilegesDirty() {
		boolean flag = false;
		Object aobj[] = privilegeListField.getSelectedValues();
		List<Privilege> list = mRole.getPrivileges();
		if (list == null || list.size() == 0)
			return aobj != null && aobj.length > 0;
		if (aobj.length != list.size())
			return true;
		for (int i = 0; i < aobj.length; i++) {
			Privilege privilege = (Privilege) aobj[i];
			if (list.contains(privilege))
				continue;
			flag = true;
			break;
		}
//ClientUtil.printInfo("isPrivDirty="+flag);
		return flag;
	}

	protected void uploadFromGUI() {
		mRole.setName(mNameField.getText());
//ClientUtil.printInfo("Selected Associations:");
		Object aobj[] = privilegeListField.getSelectedValues();
		LinkedList<Privilege> linkedlist = new LinkedList<Privilege>();
		for (int i = 0; i < aobj.length; i++) {
			Privilege privilege = (Privilege) aobj[i];
			linkedlist.add(privilege);
		}
		mRole.setPrivileges(linkedlist);
	}

	public RoleDetailsPanel() {
		super();
		mNameField = new JTextField(10);
		privilegeListField = new CheckList();
		saveRoleButton = null;
		mRole = null;
		mIsViewOnly = true;
		mIsNew = false;
		initComponents();
		addComponents();
		setEnabled(false);
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

	private boolean isViewOnly() {
		return mIsViewOnly;
	}

	public Role getDisplayRole() {
		return mRole;
	}

	private boolean saveDisplayRole() {
		try {
//ClientUtil.printInfo("In saveDisplayRole() going to saveRole_aux() and will return true");
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
		boolean isNew = mRole.getID() == -1;
		int roleID = ClientUtil.getCommunicator().save(mRole, false);
		mRole.setID(roleID);
		if (isNew) {
			EntityModelCacheFactory.getInstance().addRole(mRole);// adds role in client cache
			mIsNew = false;// (TT 1629) since save has been performed, set mIsnew flag to false
		}
		// TT 2127
		firePropertyChange("DetailUpdated", null, mRole);
		populateForm(mRole,true);
		setEnabled(false);
	}

	private boolean lockRoleOnServer(Role role) {
		try {
			ClientUtil.getCommunicator().lock(role.getID(), EntityType.ROLE);
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}
	}

	public void discardChanges() {
		clearForm();
	}

	protected void clearForm() {
		mRole = null;
		mNameField.setText("");
		privilegeListField.getSelectionModel().clearSelection();
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
		addComponent(this, bag, c, mNameField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createLabel(PRIVILEGES_FORM_LBL, SwingConstants.TOP));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		addComponent(this, bag, c, new JScrollPane(privilegeListField));

	}

	protected boolean isDirty() {
		if (mRole == null) {
			ClientUtil.printError("No Role set!");
//ClientUtil.printInfo("isDirty() from mRole == null flag="+false);
			return false;
		}
		if (isViewOnly()){
//ClientUtil.printInfo("isDirty() from isViewOnly() flag="+false);
			return false;
		}
		if (isNew()) {
//ClientUtil.printInfo("isDirty() from isNew() flag="+true);
			return true;

		}
		else {
			boolean flag = !mRole.getName().equals(mNameField.getText()) || isPrivilegesDirty();
//ClientUtil.printInfo("isDirty() flag="+flag);
			return flag;
		}
	}

	private boolean cancelEdit(int roleID) {
		if (roleID < 0) {
			return true;
		}
		try {
			ClientUtil.getCommunicator().unlock(roleID, EntityType.ROLE);
			return true;
		}
		catch (Exception exception) {
			ClientUtil.handleRuntimeException(exception);
			return false;
		}

	}

	public void displayRoleDetails(Role role, boolean flag, boolean flag1) {
		if (role != null) {
			if (getDisplayRole() != null) {
				if (role.getID() == getDisplayRole().getID()) {
					ClientUtil.printInfo("Same Role!");
					if (flag == isViewOnly())
						return;
				}
				if (isDirty() && !confirmProceed())
					return;
				if (!isViewOnly() && !cancelEdit(getDisplayRole().getID())) {
					ClientUtil.printError("Unable to Cancel Edit");
					return;
				}
			}
			mIsNew = flag1;
			if (!isNew() && !flag) {
				boolean flag2 = lockRoleOnServer(role);
				if (!flag2)
					return;
			}
			populateForm(role, flag);
			setEnabled(!flag);
		}
		else {
			JOptionPane.showMessageDialog(
				ClientUtil.getApplet(),
				ClientUtil.getInstance().getMessage("SelectItemFirstMsg"),
				ClientUtil.getInstance().getMessage("WarningMsgTitle"),
				2);
			clearForm();
		}
	}

	protected void initComponents() {
		String s = ClientUtil.getInstance().getLabel("button.save");
		JButton jbutton = UIFactory.createButton(s, "image.btn.small.save",null,null);//new JButton(s, ClientUtil.getInstance().makeImageIcon("image.btn.small.save"));
		saveRoleButton = jbutton;
		RoleDetailsAdapter roledetailsadapter = new RoleDetailsAdapter();
		jbutton.addActionListener(roledetailsadapter);
		
		privilegeListField.setModel(EntityModelCacheFactory.getInstance().getPrivilegeListModel());
		privilegeListField.setSelectionMode(2);
		privilegeListField.setVisibleRowCount(10);
	}

	public void setEnabled(boolean flag) {
		saveRoleButton.setEnabled(flag);
		mNameField.setEditable(flag);
		privilegeListField.setEnabled(flag);
		super.setEnabled(flag);
	}

	private boolean isNew() {
		return mIsNew;
	}

	private JTextField mNameField;
	private CheckList privilegeListField;
	JButton saveRoleButton;
	protected Role mRole;
	protected boolean mIsViewOnly;
	private boolean mIsNew;
}
