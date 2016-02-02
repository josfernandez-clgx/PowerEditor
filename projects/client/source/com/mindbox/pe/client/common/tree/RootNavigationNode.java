/*
 * Created on 2004. 3. 3.
 *
 */
package com.mindbox.pe.client.common.tree;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class RootNavigationNode extends NavigationNode {
	
	public RootNavigationNode(Object obj) {
		super(obj);
		super.explored = true;
	}

	public void explore() {}
}
