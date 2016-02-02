package com.mindbox.pe.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDomainElement implements Serializable {

	private String name;
	private String displayLabel;

	/** @since PowerEditor 3.2.0 */
	protected final List<DomainView> domainViewList;

	protected AbstractDomainElement() {
		domainViewList = new ArrayList<DomainView>();
	}
	
	public final void setName(String s) {
		name = s;
	}

	public final String getName() {
		return name;
	}

	public final void setDisplayLabel(String s) {
		displayLabel = s;
	}

	public final String getDisplayLabel() {
		return displayLabel;
	}

	/**
	 * Adds the specified domain view digest object for this attribute.
	 * @param digest domain view digest object
	 * @since PowerEditor 3.2.0
	 */
	public final void addDomainViewDigest(DomainViewDigest digest) {
		DomainView domainView = DomainView.forName(digest.getViewType());
		synchronized (domainViewList) {
			if (!domainViewList.contains(domainView)) {
				domainViewList.add(domainView);
			}
		}
	}

	/**
	 * Adds the specified domain view for this attribute.
	 * @param domainView domain view to add
	 * @since PowerEditor 3.2.0
	 */
	public final void addDomainView(DomainView domainView) {
		synchronized (domainViewList) {
			if (!domainViewList.contains(domainView)) {
				domainViewList.add(domainView);
			}
		}
	}

	/**
	 * Tests if this attribute is enabled for the specified domain view.
	 * @param domainView the domain view to check
	 * @return <code>true</code> if this is enabled for <code>domainView</code>; <code>false</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public final boolean hasDomainView(DomainView domainView) {
		synchronized (domainViewList) {
			return domainViewList.contains(domainView);
		}
	}

	/**
	 * Tests if this contains no domain view.
	 * @return <code>true</code> if this contains no domain view; <code>false</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public final boolean isDomainViewEmpty() {
		synchronized (domainViewList) {
			return domainViewList.isEmpty();
		}
	}
}
