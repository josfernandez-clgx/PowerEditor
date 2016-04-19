/*
 * Created on Nov 18, 2005
 *
 */
package com.mindbox.pe.server.model;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class ServerStats {

	private int userCount;
	private String startedDate;
	private String version;

	public String getStartedDate() {
		return startedDate;
	}

	public int getUserCount() {
		return userCount;
	}

	public String getVersion() {
		return version;
	}

	public void setStartedDate(String startedDate) {
		this.startedDate = startedDate;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
