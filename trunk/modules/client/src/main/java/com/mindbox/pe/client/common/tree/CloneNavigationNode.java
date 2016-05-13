/*
 * Created on 2004. 3. 3.
 *
 */
package com.mindbox.pe.client.common.tree;

import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.IDNameObject;
import com.mindbox.pe.model.filter.CloneGenericEntityFilter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class CloneNavigationNode extends NavigationNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private GenericEntityType entityType;

	public CloneNavigationNode(IDNameObject simpledatabean, GenericEntityType type) {
		super.explored = false;
		this.entityType = type;
		setUserObject(simpledatabean);
	}

	public boolean getAllowsChildren() {
		return !isLeaf();
	}

	public boolean isLeaf() {
		return isExplored() && getChildCount() <= 0;
	}

	public IDNameObject getEntity() {
		return (IDNameObject) getUserObject();
	}

	public void explore() {
		if (!isExplored()) {
			IDNameObject simpledatabean = getEntity();
			CloneGenericEntityFilter cloneFilter = new CloneGenericEntityFilter(entityType, simpledatabean.getID(), false);
			List<GenericEntity> list;
			try {
				list = ClientUtil.getCommunicator().search(cloneFilter);
				for (int i = 0; i < list.size(); i++) {
					GenericEntity simpledatabean1 = list.get(i);
					add(new CloneNavigationNode(simpledatabean1, entityType));
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			super.explored = true;
		}
	}

}
