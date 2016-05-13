package com.mindbox.pe.server.model;

import com.mindbox.pe.model.TemplateUsageType;

public class ProductGridFilter {

	public void setChannelId(int i) {
		mChannelId = i;
	}

	public int getChannelId() {
		return mChannelId;
	}

	public String toString() {
		return "ProductGridFilter for type=" + getTemplateType();
	}

	public ProductGridFilter(TemplateUsageType s, int ai[]) {
		setTemplateType(s);
		setProductIds(ai);
	}

	public void setTemplateType(TemplateUsageType s) {
		mTemplateType = s;
	}

	public TemplateUsageType getTemplateType() {
		return mTemplateType;
	}

	public void setProductIds(int ai[]) {
		mProductIds = ai;
	}

	public int[] getProductIds() {
		return mProductIds;
	}
	
	public int getInvestorID() {
		return investorID;
	}

	public void setInvestorID(int investorID) {
		this.investorID = investorID;
	}
	
	private int mChannelId;
	private TemplateUsageType mTemplateType;
	private int mProductIds[];
	private int investorID = -1;
}