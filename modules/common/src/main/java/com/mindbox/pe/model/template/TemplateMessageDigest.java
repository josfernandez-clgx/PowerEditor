package com.mindbox.pe.model.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.Persistent;

/**
 * Template message digest.
 * 
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public class TemplateMessageDigest implements ColumnReferenceContainer {

	private static final long serialVersionUID = 20040608100001L;

	public static String adjustColumnReferences(String messageText, int origColNo, int newColNo, boolean delete, boolean findOnly) {
		StringBuilder buf = new StringBuilder(messageText);
		int startIndex = buf.indexOf("%");
		while (startIndex != -1) {
			int endIndex = buf.indexOf("%", startIndex + 1);
			if (endIndex != -1) {
				String ref = buf.substring(startIndex, endIndex);
				int tagIndex = ref.toLowerCase().indexOf("column ");
				if (tagIndex != -1) {
					int colNum = -1;
					try {
						colNum = Integer.parseInt(UtilBase.trim(ref.substring(tagIndex + 7)));
					}
					catch (NumberFormatException x) {
					}
					if (colNum == origColNo) {
						if (findOnly) {
							return "true";
						}
						if (delete) {
							buf.delete(startIndex, endIndex + 1);
							startIndex = buf.indexOf("%", startIndex);
							continue;
						}
						else {
							String newRef = "%column " + newColNo + "%";
							buf.replace(startIndex, endIndex + 1, newRef);
							startIndex = buf.indexOf("%", startIndex + newRef.length());
							continue;
						}
					}
				}
				else {
					tagIndex = ref.toLowerCase().indexOf("columnmessages(");
					if (tagIndex != -1) {
						String[] tokens = UtilBase.trim(ref.substring(tagIndex + 15, ref.indexOf(")"))).split(",");
						List<Integer> newInts = new ArrayList<Integer>();
						boolean found = false;
						for (int i = 0; i < tokens.length; i++) {
							int colNum = -1;
							try {
								colNum = Integer.parseInt(UtilBase.trim(tokens[i]));
							}
							catch (NumberFormatException x) {
							}
							if (colNum == origColNo) {
								if (findOnly) {
									return "true";
								}
								found = true;
								if (!delete) {
									newInts.add(newColNo);
								}
							}
							else if (colNum > -1) {
								newInts.add(colNum);
							}
						}
						if (found) {
							if (newInts.size() > 0) {
								String newRef = "%columnMessages(";
								Iterator<Integer> it = newInts.iterator();
								while (it.hasNext()) {
									Integer c = it.next();
									newRef = newRef + c.intValue() + ",";
								}
								// this removes the last comma
								newRef = newRef.substring(0, newRef.length() - 1) + ")%";
								buf.replace(startIndex, endIndex + 1, newRef);
								startIndex = buf.indexOf("%", startIndex + newRef.length());
								continue;
							}
							else {
								// this is the case where we deleted the only reference
								buf.delete(startIndex, endIndex + 1);
								startIndex = buf.indexOf("%", startIndex);
								continue;
							}
						}
					}
				}
				startIndex = buf.indexOf("%", endIndex + 1);
			}
			else {
				startIndex = -1;
			}
		}
		if (!findOnly) {
			return buf.toString();
		}
		return null;
	}

	private int entityID = Persistent.UNASSIGNED_ID;
	private String text;
	private String channel; // for import backward-compatibility

	/**
	 * The delimeter to use between conditional clauses in the message text
	 */
	private String conditionalDelimiter;
	/**
	 * The delimeter to use between the last two conditional clauses in the message text
	 */
	private String conditionalFinalDelimiter;

	public TemplateMessageDigest() {
	}

	/**
	 * Creates a new instance of this that is an exact copy of the source.
	 * 
	 * @param source
	 * @since PowerEditor 4.3.2
	 */
	public TemplateMessageDigest(TemplateMessageDigest source) {
		this();
		this.entityID = source.entityID;
		this.text = source.text;
		this.conditionalDelimiter = source.conditionalDelimiter;
		this.conditionalFinalDelimiter = source.conditionalFinalDelimiter;
	}

	@Override
	public void adjustChangedColumnReferences(int originalColNo, int newColNo) {
		setText(adjustColumnReferences(this.getText(), originalColNo, newColNo, false, false));
	}

	@Override
	public void adjustDeletedColumnReferences(int colNo) {
		setText(adjustColumnReferences(this.getText(), colNo, -1, true, false));
	}

	@Override
	public boolean containsColumnReference(int colNo) {
		return adjustColumnReferences(this.getText(), colNo, -1, false, true) != null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof TemplateMessageDigest) {
			TemplateMessageDigest digest = (TemplateMessageDigest) obj;
			return this.entityID == digest.entityID && UtilBase.isSame(this.conditionalDelimiter, digest.conditionalDelimiter)
					&& UtilBase.isSame(this.conditionalFinalDelimiter, digest.conditionalFinalDelimiter);
		}
		else {
			return false;
		}
	}

	public String getChannel() {
		return channel;
	}

	/**
	 * This text is inserted between each clause for clauses 1 to n-1.
	 * 
	 * @return the delimeter to use between conditional clauses in the message text
	 */
	public String getConditionalDelimiter() {
		return conditionalDelimiter;
	}

	/**
	 * This text is inserted between each clause n-1 and clause n.
	 * 
	 * @return the delimeter to use between the last two conditional clauses in the message text.
	 */
	public String getConditionalFinalDelimiter() {
		return conditionalFinalDelimiter;
	}

	public int getEntityID() {
		return entityID;
	}

	/**
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @param delimiter
	 *            The delimeter to use between conditional clauses in the message text
	 */
	public void setConditionalDelimiter(String delimiter) {
		conditionalDelimiter = delimiter;
	}

	/**
	 * @param delimiter
	 *            The delimeter to use between the last two conditional clauses in the message text
	 */
	public void setConditionalFinalDelimiter(String delimiter) {
		conditionalFinalDelimiter = delimiter;
	}

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public void setEntityIDStr(String str) {
		try {
			this.entityID = Integer.parseInt(str);
		}
		catch (NumberFormatException ex) {
			this.entityID = Persistent.UNASSIGNED_ID;
		}
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Message[entityID=" + entityID + ",text=" + text + "]";
	}

}