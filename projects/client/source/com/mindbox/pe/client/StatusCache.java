package com.mindbox.pe.client;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.model.TypeEnumValue;

/**
 * Caches various status for performance.
 * 
 * @author Gene Kim
 * @since 5.8.0
 */
final class StatusCache {

	private final List<TypeEnumValue> allStatuses;
	private TypeEnumValue highestStatusEnum;
	private TypeEnumValue lowestStatusEnum;

	StatusCache(List<TypeEnumValue> allStatuses) {
		this.allStatuses = new ArrayList<TypeEnumValue>();
		if (allStatuses != null) {
			this.allStatuses.addAll(allStatuses);
			findHighestStatusEnum(allStatuses);
			findLowestStatusEnum(allStatuses);
		}
	}

	public boolean isHighestStatus(String status) {
		return getHighestStatus() == null ? false : getHighestStatus().equals(status);
	}

	public String getHighestStatus() {
		return highestStatusEnum.getValue();
	}

	public String getHighestStatusDisplayLabel() {
		return highestStatusEnum.getDisplayLabel();
	}

	public String getLowestStatus() {
		return lowestStatusEnum.getValue();
	}

	public String getStatusDisplayLabel(String status) {
		for (TypeEnumValue statusEnum : allStatuses) {
			if (statusEnum.getName().equals(status)) return statusEnum.getDisplayLabel();
		}
		return null;
	}

	private void findHighestStatusEnum(List<TypeEnumValue> allStatuses) {
		TypeEnumValue highestEnum = null;
		for (TypeEnumValue statusEnum : allStatuses) {
			if (highestEnum == null || statusEnum.getID() > highestEnum.getID()) {
				highestEnum = statusEnum;
			}
		}
		highestStatusEnum = highestEnum;
	}

	private void findLowestStatusEnum(List<TypeEnumValue> allStatuses) {
		TypeEnumValue lowestEnum = null;
		for (TypeEnumValue statusEnum : allStatuses) {
			if (lowestEnum == null || statusEnum.getID() < lowestEnum.getID()) {
				lowestEnum = statusEnum;
			}
		}
		lowestStatusEnum = lowestEnum;
	}
}
