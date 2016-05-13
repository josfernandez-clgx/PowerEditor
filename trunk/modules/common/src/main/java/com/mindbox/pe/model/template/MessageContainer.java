package com.mindbox.pe.model.template;

import java.util.List;


/**
 * Message container.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 */
public interface MessageContainer {

	void addMessageDigest(TemplateMessageDigest digest);
	
	List<TemplateMessageDigest> getAllMessageDigest();
	
	public boolean hasMessageDigest();
	
	public boolean hasEntitySpecificMessage();
	
	public TemplateMessageDigest findMessageForEntity(int entityID);
	
	void removeMessageDigest(TemplateMessageDigest digest);
}
