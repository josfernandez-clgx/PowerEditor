package com.mindbox.pe.communication;

import java.util.List;

/**
 * 
 * @since PowerEditor 1.0
 */
public class ListResponse<T> extends ResponseComm {

	private static final long serialVersionUID = 2003061611401000L;

	private final List<T> resultList;

	/**
	 * 
	 */
	public ListResponse(List<T> resultList) {
		super();
		this.resultList = resultList;
	}

	public List<T> getResultList() {
		return resultList;
	}

	@Override
	public String toString() {
		return String.format("ListResponse[size=%d;%s]", resultList.size(), resultList);
	}
}
