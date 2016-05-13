/*
 * Created on 2005. 2. 14.
 *
 */
package com.mindbox.pe.client.common.context;

import com.mindbox.pe.client.common.event.ContextChangeListener;
import com.mindbox.pe.common.GuidelineContextHolder;


/**
 * Context holder for client.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public interface GuidelineContextHolderForClient extends GuidelineContextHolder {

	void addContextChangeListener(ContextChangeListener l);

	void removeContextChangeListener(ContextChangeListener l);


}