/*
 * Created on 2004. 3. 24.
 *
 */
package com.mindbox.pe.server.imexport.digest;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Message {

	private int id = -1;
	private int templateID = -1;
	private int columnID = -1;
	private String channel = "default";
	private String text;
	private ActivationDates activationDates = null;

	public ActivationDates getActivationDates() {
		return activationDates;
	}

	public void setActivationDates(ActivationDates dates) {
		//System.out.println(">>> setActivationDates: " + dates);
		activationDates = dates;
	}

	public String getChannel() {
		return channel;
	}

	public int getColumnID() {
		return columnID;
	}

	public int getId() {
		return id;
	}

	public int getTemplateID() {
		return templateID;
	}

	public String getText() {
		return text;
	}

	/**
	 * @param string
	 */
	public void setChannel(String string) {
		channel = ((string == null || string.length() < 1) ? "default" : string);
	}

	/**
	 * @param i
	 */
	public void setColumnID(int i) {
		columnID = (i == 0 ? -1 : i);
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	/**
	 * @param i
	 */
	public void setTemplateID(int i) {
		templateID = i;
	}

	/**
	 * @param string
	 */
	public void setText(String string) {
		text = string;
	}

	public String toString() {
		return "Message["
			+ id
			+ ",temp="
			+ templateID
			+ ",col="
			+ columnID
			+ ",act="
			+ activationDates
			+ ": "
			+ text
			+ "]";
	}

}
