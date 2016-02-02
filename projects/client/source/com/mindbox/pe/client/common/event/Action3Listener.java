/*
 * Created on 2004. 8. 6.
 */
package com.mindbox.pe.client.common.event;

import java.awt.event.ActionEvent;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
public interface Action3Listener {
	
	void newPerformed(ActionEvent e);
	void editPerformed(ActionEvent e);
	void deletePerformed(ActionEvent e);

}
