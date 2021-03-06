package com.mindbox.pe.model.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.server.parser.jtb.message.ParseException;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;

/**
 * Grid Template Column.
 * @author Geneho Kim
 * @author MindBox
 */
public class GridTemplateColumn extends AbstractTemplateColumn implements RuleMessageContainer, ColumnReferenceContainer {

	private static final long serialVersionUID = 2003120000000000L;


	/** used to store rule def string from template XML; for migration only */
	private transient String ruleDefinitionString;
	private RuleDefinition ruleDefinition;
	private final DefaultMessageContainer messageContainer;
	private final ColumnMessageFragmentList messageFragmentTextList;
	private final MessageConfiguration messageConfig;
	private final List<ColumnMessageFragmentDigest> messageFragmentDigestList;

	public GridTemplateColumn() {
		this(-1, "", null, 100, null);
	}

	/**
	 * Create a new instance of this that is an exact copy of the source.
	 * This performs deep-copy.
	 * @param source source
	 * @since PowerEditor 4.3.2
	 */
	public GridTemplateColumn(GridTemplateColumn source) {
		super(source);
		this.messageContainer = new DefaultMessageContainer();
		messageFragmentDigestList = new ArrayList<ColumnMessageFragmentDigest>();
		this.messageConfig = new MessageConfiguration(source.messageConfig);
		this.messageContainer.copyFrom(source);
		// deep copy messageFragmentDigestList
		for (ColumnMessageFragmentDigest element : source.messageFragmentDigestList) {
			if (element != null) {
				messageFragmentDigestList.add(new ColumnMessageFragmentDigest(element));
			}
		}
		// deep copy messageFragmentList
		messageFragmentTextList = new ColumnMessageFragmentList(source.messageFragmentTextList);
		this.ruleDefinitionString = source.ruleDefinitionString;
		this.ruleDefinition = (source.ruleDefinition == null ? null : new RuleDefinition(source.ruleDefinition));
		this.setColor(source.getColor());
		this.setFont(source.getFont());
		this.setDataSpecDigest(new ColumnDataSpecDigest(source.getColumnDataSpecDigest()));
		this.setTitle(source.getTitle());
	}

	/**
	 * 
	 * @param id id
	 * @param name name
	 * @param desc desc
	 * @param width width
	 * @param usageType usageType
	 */
	public GridTemplateColumn(int id, String name, String desc, int width, TemplateUsageType usageType) {
		super(id, name, desc, width, usageType);
		messageContainer = new DefaultMessageContainer();
		messageFragmentDigestList = new ArrayList<ColumnMessageFragmentDigest>();
		messageFragmentTextList = new ColumnMessageFragmentList();
		messageConfig = new MessageConfiguration();
	}

	/**
	 * The given MessageFragmentDigest can get added to both to the ColumnMessageFragmentList
	 * and the list of MessageConfiguration, depending in the values of the
	 * given MessageFragmentDigest.
	 * 
	 * This methode gets called from the TemplateXMLDigester
	 * @param msgDigest msgDigest
	 * @throws ParseException on parser error
	 * @since PowerEditor 3.3.0
	 */
	public void addColumnMessageFragment(ColumnMessageFragmentDigest msgDigest) throws ParseException {
		messageFragmentDigestList.add(msgDigest);
		if (msgDigest.getText() != null) {
			messageFragmentTextList.addText(msgDigest.getType(), msgDigest.getText(), msgDigest.getCellSelection());
		}
		messageConfig.updateMessageDigest(msgDigest);
	}

	/**
	 * Added for digest support.
	 * @since PowerEditor 3.2.0
	 */
	@Override
	public void addMessageDigest(TemplateMessageDigest digest) {
		messageContainer.addMessageDigest(digest);
	}

	@Override
	public void adjustChangedColumnReferences(final int originalColNo, final int newColNo) {
		if (ruleDefinition != null) {
			ruleDefinition.adjustChangedColumnReferences(originalColNo, newColNo);
		}

		for (Iterator<TemplateMessageDigest> iter = messageContainer.getAllMessageDigest().iterator(); iter.hasNext();) {
			TemplateMessageDigest element = iter.next();
			element.adjustChangedColumnReferences(originalColNo, newColNo);
		}
		for (ColumnMessageFragmentDigest element : messageFragmentDigestList) {
			element.adjustChangedColumnReferences(originalColNo, newColNo);
			try {
				if (element.getText() != null) {
					messageFragmentTextList.addText(element.getType(), element.getText(), element.getCellSelection());
				}
			}
			catch (ParseException x) {
				// This should never happen
			}
		}
	}

	@Override
	public void adjustDeletedColumnReferences(int colNo) {
		if (ruleDefinition != null) ruleDefinition.adjustDeletedColumnReferences(colNo);
		for (Iterator<TemplateMessageDigest> iter = messageContainer.getAllMessageDigest().iterator(); iter.hasNext();) {
			TemplateMessageDigest element = iter.next();
			element.adjustDeletedColumnReferences(colNo);
		}
		for (ColumnMessageFragmentDigest element : messageFragmentDigestList) {
			element.adjustDeletedColumnReferences(colNo);
			try {
				if (element.getText() != null) {
					messageFragmentTextList.addText(element.getType(), element.getText(), element.getCellSelection());
				}
			}
			catch (ParseException e) {
				// This should never happen
			}
		}
	}

	@Override
	public boolean containsColumnReference(int colNo) {
		return false;
	}

	@Override
	public TemplateMessageDigest findMessageForEntity(int channelID) {
		return messageContainer.findMessageForEntity(channelID);
	}

	@Override
	public List<TemplateMessageDigest> getAllMessageDigest() {
		return messageContainer.getAllMessageDigest();
	}

	public List<ColumnMessageFragmentDigest> getAllMessageFragmentDigests() {
		return Collections.unmodifiableList(messageFragmentDigestList);
	}

	private ColumnMessageFragmentDigest getAnyMessageFragmentDigest() {
		for (ColumnMessageFragmentDigest element : messageFragmentDigestList) {
			if (element.getType() == MessageConfigType.ANY) {
				return element;
			}
		}
		return null;
	}

	public MessageConfiguration getMessageConfiguration() {
		return messageConfig;
	}

	private ColumnMessageFragmentDigest getMessageFragmentDigest(EnumValues<?> enumVal) {
		boolean isExclusion = enumVal.isSelectionExclusion();
		boolean isMultiSelect = enumVal.size() > 1;
		// check exact match first
		for (ColumnMessageFragmentDigest element : messageFragmentDigestList) {
			if (element.getType() == MessageConfigType.ENUM && element.getCellSelection() != null
					&& element.getCellSelection() == ConfigUtil.getCellSelectionType(isExclusion, isMultiSelect)) {
				return element;
			}
		}
		// check for default enum message fragment
		for (ColumnMessageFragmentDigest element : messageFragmentDigestList) {
			if (element.getType() == MessageConfigType.ENUM && element.getCellSelection() != null && element.getCellSelection() == CellSelectionType.DEFAULT) {
				return element;
			}
		}
		return null;
	}

	public ColumnMessageFragmentDigest getMessageFragmentDigest(Object cellValue) {
		if (messageFragmentDigestList.isEmpty()) return null;
		ColumnMessageFragmentDigest digest = null;
		if (cellValue instanceof EnumValues) {
			digest = getMessageFragmentDigest((EnumValues<?>) cellValue);
		}
		else if (cellValue instanceof IRange) {
			digest = getMessageFragmentDigestForRange();
		}
		return (digest == null ? getAnyMessageFragmentDigest() : digest);
	}

	private ColumnMessageFragmentDigest getMessageFragmentDigestForRange() {
		for (ColumnMessageFragmentDigest element : messageFragmentDigestList) {
			if (element.getType() == MessageConfigType.RANGE) {
				return element;
			}
		}
		return null;
	}

	public Message getParsedMessage(Object cellValue) {
		return messageFragmentTextList.getParsedMessage(cellValue);
	}

	/**
	 * @return Returns the ruleDefinition.
	 */
	@Override
	public RuleDefinition getRuleDefinition() {
		return ruleDefinition;
	}

	/**
	 * For migration use only
	 * @return rule definition string
	 * @since PowerEditor 4.0
	 */
	public String getRuleDefinitionString() {
		return ruleDefinitionString;
	}

	public String getUnparsedMessage(Object cellValue) {
		return messageFragmentTextList.getUnparsedMessage(cellValue);
	}

	@Override
	public boolean hasEntitySpecificMessage() {
		return messageContainer.hasEntitySpecificMessage();
	}

	@Override
	public boolean hasMessageDigest() {
		return messageContainer.hasMessageDigest();
	}

	public boolean hasMessageFragmentDigest() {
		return !messageFragmentDigestList.isEmpty();
	}

	@Override
	public void removeMessageDigest(TemplateMessageDigest digest) {
		messageContainer.removeMessageDigest(digest);
	}

	public void removeMessageFragmentDigest(ColumnMessageFragmentDigest digest) {
		messageFragmentDigestList.remove(digest);
		messageConfig.removeMessageDigest(digest);
	}

	/**
	 * Added for digest support.
	 * @param str str
	 * @since PowerEditor 3.2.0
	 */
	public void setColNum(String str) {
		try {
			setColumnNumber(Integer.parseInt(str));
		}
		catch (Exception ex) {
		}
	}

	private void setColumnNumber(int colNo) {
		super.setID(colNo);
	}

	/**
	 * @param ruleDefinition The ruleDefinition to set.
	 */
	@Override
	public void setRuleDefinition(RuleDefinition ruleDefinition) {
		this.ruleDefinition = ruleDefinition;
	}

	/**
	 * For migration use only
	 * @param ruleDefinitionString the rule definition string
	 * @since PowerEditor 4.0
	 */
	public void setRuleDefinitionString(String ruleDefinitionString) {
		this.ruleDefinitionString = ruleDefinitionString;
	}

	/**
	 * Update the column message fragment.
	 * @param msgDigest msgDigest
	 * @throws ParseException on parser error
	 */
	public void updateColumnMessageFragmentText(ColumnMessageFragmentDigest msgDigest) throws ParseException {
		messageFragmentTextList.updateText(msgDigest.getType(), msgDigest.getText(), msgDigest.getCellSelection());
		messageConfig.updateMessageDigest(msgDigest);
		for (ColumnMessageFragmentDigest columnMessageFragmentDigest : messageFragmentDigestList) {
			if (columnMessageFragmentDigest.getType() == msgDigest.getType() && columnMessageFragmentDigest.getCellSelection() == msgDigest.getCellSelection()) {
				columnMessageFragmentDigest.copyFrom(msgDigest);
				return;
			}
		}
	}

}