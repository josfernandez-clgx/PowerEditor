package com.mindbox.pe.common.digest;

import com.mindbox.pe.common.config.AbstractDigestedObjectHolder;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class DigestedObjectHolder extends AbstractDigestedObjectHolder {

	private static final long serialVersionUID = 1246768110326268828L;

	public DigestedObjectHolder() {
	}

	public final void removeAll(Class<?> key) {
		super.removeObjects(key);
	}
}
