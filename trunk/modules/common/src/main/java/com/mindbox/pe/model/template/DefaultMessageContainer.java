package com.mindbox.pe.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * @author Geneho Kim
 * @since PowerEditor
 */
class DefaultMessageContainer implements MessageContainer, Serializable {

	private static final long serialVersionUID = 200503157000000L;

	private final List<TemplateMessageDigest> messageDigestList;

	public DefaultMessageContainer() {
		messageDigestList = new ArrayList<TemplateMessageDigest>();
	}

	DefaultMessageContainer(DefaultMessageContainer source) {
		this();
		copyFrom(source);
	}
	
	void copyFrom(MessageContainer messageContainer) {
		synchronized (messageDigestList) {
			messageDigestList.clear();
			for (TemplateMessageDigest element : messageContainer.getAllMessageDigest()) {
				messageDigestList.add(new TemplateMessageDigest(element));
			}
		}
	}

	/**
	 * Added for digest support.
	 * 
	 * @since PowerEditor 3.2.0
	 */
	public void addMessageDigest(TemplateMessageDigest digest) {
		synchronized (messageDigestList) {
			messageDigestList.add(digest);
		}
	}

	public void removeMessageDigest(TemplateMessageDigest digest) {
		synchronized (messageDigestList) {
			if (messageDigestList.contains(digest)) {
				messageDigestList.remove(digest);
			}
		}
	}

	public boolean hasMessageDigest() {
		return !messageDigestList.isEmpty();
	}

	public List<TemplateMessageDigest> getAllMessageDigest() {
		return Collections.unmodifiableList(messageDigestList);
	}

	public TemplateMessageDigest findMessageForEntity(int entityID) {
		for (Iterator<TemplateMessageDigest> iter = messageDigestList.iterator(); iter.hasNext();) {
			TemplateMessageDigest element = iter.next();
			if ((element.getEntityID() == entityID) || (entityID == -1 && element.getEntityID() < 1)) {
				return element;
			}
		}
		return (entityID < 1 ? null : findMessageForEntity(-1));
	}

	public boolean hasEntitySpecificMessage() {
		for (Iterator<TemplateMessageDigest> iter = messageDigestList.iterator(); iter.hasNext();) {
			TemplateMessageDigest element = iter.next();
			if (element.getEntityID() > 0) return true;
		}
		return false;
	}

}