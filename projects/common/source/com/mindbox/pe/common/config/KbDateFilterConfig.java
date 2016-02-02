package com.mindbox.pe.common.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

public class KbDateFilterConfig implements Serializable {

	private static final long serialVersionUID = 2010050300000L;

	private Date beginDate;
	private Date endDate;

	public KbDateFilterConfig(Date beginDate, Date endDate) {
		super();
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
	}

	public boolean hasEndDate() {
		return endDate != null;
	}
}
