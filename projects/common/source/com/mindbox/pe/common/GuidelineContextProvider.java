/*
 * Created on Jul 14, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.common;

import com.mindbox.pe.model.GuidelineContext;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public interface GuidelineContextProvider {
	GuidelineContext[] getGuidelineContexts();
}
