/*
 * Created on Jul 1, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.model.assckey;

import java.util.Date;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public interface TimedAssociationKeyFactory {

	TimedAssociationKey createInstance(int keyID, Date effDate, Date expDate);

}
