package com.mindbox.pe.model.template;

import java.io.Serializable;
import java.io.StringReader;

import com.mindbox.pe.common.config.AbstractMessageConfigSet;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.model.template.ColumnMessageFragmentList.ColumnMessageFragment;
import com.mindbox.pe.server.parser.jtb.message.MessageParser;
import com.mindbox.pe.server.parser.jtb.message.ParseException;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;


/**
 * This is a list of messageFragment text strings and its associated parsed elements.
 * One of these is associated with each GridColumnTemplate that has a MessageFragment
 * element. Although, in the XML, the messageFragment configuration information, the
 * configuration information is separated out, since it can be overriden.  Config information
 * can be found in MessageConfiguration objects.
 * 
 * A columnMessageFragmentList is a list of ColumnMessageFragments, a private class.
 * 
 * @author Beth
 * @author Mindbox
 * @since version 3.3.0
 */
public class ColumnMessageFragmentList extends AbstractMessageConfigSet<ColumnMessageFragment> implements Serializable {

	/**
	 * The private class ColumnMessageFragment holds a messageFragment and its parsed string.
	 * A columnMessageFragmentList is a list of ColumnMessageFragments.
	 */
	public static class ColumnMessageFragment implements Serializable {

		private static final long serialVersionUID = 2004071124020000L;

		private String unparsedMessageTextStr;
		private Message parsedMessageObj;

		public ColumnMessageFragment(ColumnMessageFragment source) {
			this.unparsedMessageTextStr = source.unparsedMessageTextStr;
			this.parsedMessageObj = source.parsedMessageObj;
		}

		public ColumnMessageFragment(String messageText) throws ParseException {
			setMessageText(messageText);
		}

		public void copyFrom(ColumnMessageFragment source) {
			this.unparsedMessageTextStr = source.unparsedMessageTextStr;
			this.parsedMessageObj = source.parsedMessageObj;
		}

		public synchronized Message getParsedMessage() {
			return parsedMessageObj;
		}

		public synchronized String getUnparsedMessage() {
			return unparsedMessageTextStr;
		}

		private void setMessageText(String messageText) throws ParseException {
			unparsedMessageTextStr = messageText;
			if (messageText != null && messageText.length() > 0) {
				this.parsedMessageObj = new MessageParser(new StringReader(messageText)).Message();
			}
			else {
				this.parsedMessageObj = null;
			}
		}

		public synchronized void updateMessage(String messageText) throws ParseException {
			setMessageText(messageText);
		}
	}

	private static final long serialVersionUID = 2004071124010000L;


	/**
	 * Default Constructor.
	 * This adds the default column message fragment, which is just the cell value.
	 * That the constructor is only called for a column if there is a &lt;MessageFragment&gt;
	 * associated with that column.  It could, however, be that the messageFragment
	 * statement only has config elements and no text, in which case the default element
	 * will be the only one in the ColumnMessageFragment list.
	 */
	public ColumnMessageFragmentList() {
		// add default enumSelection values.
		ColumnMessageFragment messageFragment = null;
		try {
			messageFragment = new ColumnMessageFragment((String) null);
		}
		catch (Exception ex) {
		}
		setDefaultConfig(messageFragment);
	}

	/**
	 * Creates a new instance of this that is an exact copy of the source.
	 * @param source source
	 * @since PowerEditor 4.3.2
	 */
	public ColumnMessageFragmentList(ColumnMessageFragmentList source) {
		super(source);
	}

	/**
	 * Adds the message text to this list, removing any others with the same key.
	 * @param messageConfigType messageConfigType
	 * @param unparsedMessageText text as appears in the XML
	 * @param cellSelectionType cell selection type
	 * @throws ParseException on parse error
	 */
	public void addText(final MessageConfigType messageConfigType, final String unparsedMessageText, final CellSelectionType cellSelectionType) throws ParseException {
		updateConfig(messageConfigType, new ColumnMessageFragment(unparsedMessageText), cellSelectionType);
	}

	@Override
	protected ColumnMessageFragment createCopy(ColumnMessageFragment source) {
		return new ColumnMessageFragment(source);
	}

	private ColumnMessageFragment getEnumFragment(boolean isExclusion, boolean isMultiSelect) {
		ColumnMessageFragment result = getEnumConfig(isExclusion, isMultiSelect);
		return result;
	}

	/**
	 * Gets the column message fragment for the specified cell value.
	 * @param cellValue cell value
	 * @return the column message fragment for <code>cellValue</code>, if found; <code>null</code>, otherwise
	 */
	private ColumnMessageFragment getFragment(Object cellValue) {
		ColumnMessageFragment result = null;
		if (cellValue instanceof EnumValues) {
			EnumValues<?> enumVal = (EnumValues<?>) cellValue;
			boolean isExclusion = enumVal.isSelectionExclusion();
			boolean isMultiSelect = enumVal.size() > 1;
			result = getEnumFragment(isExclusion, isMultiSelect);
		}
		else if (cellValue instanceof IRange) {
			result = getRangeConfig();
		}
		if (result == null) {
			result = getAnyConfig();
		}
		if (result == null) {
			result = getDefaultConfig();
		}
		return result;
	}

	/**
	 * Given a cellValue object from a table, returns the parsed message
	 * object that represents that value (e.g. "LTV of %cellValue%).  
	 * This method will always return a value.
	 * @param cellValue an object of type IRange, EnumValues, etc.
	 * @return the parsed message for <code>cellValue</code>
	 */
	public Message getParsedMessage(Object cellValue) {
		ColumnMessageFragment cmFragment = getFragment(cellValue);
		if (cmFragment == null) return null;

		return cmFragment.getParsedMessage();
	}

	/**
	 * Given a cellValue object from a table, returns the unparsed message
	 * string that represents that value (e.g. "LTV of %cellValue%).  
	 * This method will always return a value.
	 * @param cellValue an object of type IRange, EnumValues, etc.
	 * @return The String that is originally in the XML.
	 */
	public String getUnparsedMessage(Object cellValue) {
		return getFragment(cellValue).getUnparsedMessage();
	}

	@Override
	protected void setDefaults(ColumnMessageFragment source) {
		getDefaultConfig().copyFrom(source);
	}

	@Override
	protected void updateInvariants(ColumnMessageFragment target, ColumnMessageFragment source) {
		target.copyFrom(source);
	}

	/**
	 * Updates the message text to this list.
	 * @param messageConfigType messageConfigType
	 * @param unparsedMessageText text as appears in the XML
	 * @param cellSelectionType cell selection type
	 * @throws ParseException on parse error
	 */
	public void updateText(final MessageConfigType messageConfigType, final String unparsedMessageText, final CellSelectionType cellSelectionType) throws ParseException {
		updateConfig(messageConfigType, new ColumnMessageFragment(unparsedMessageText), cellSelectionType);
	}
}