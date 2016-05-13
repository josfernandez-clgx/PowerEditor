package com.mindbox.pe.server.validate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.common.validate.DefaultValidationViolation;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.validate.oval.OValDataValidator;

/**
 * Provides validation of various object that cannot be done via OVAL because of separation of codes among common, client, and server.
 * These validations include:<ol>
 * <li>Verify that all generic entity or category ids are valid (exists in the cache)
 * </ol>
 * @author kim
 * @see OValDataValidator
 */
public final class DefaultDataValidator implements DataValidator {

	private OValDataValidator ovalValidator = OValDataValidator.getInstance();

	@Override
	public List<ValidationViolation> validate(Object objectToValidate) {
		return ovalValidator.validate(objectToValidate);
	}

	public List<ValidationViolation> validate(GenericEntityCompatibilityData objectToValidate) {
		List<ValidationViolation> validationViolations = new ArrayList<ValidationViolation>();
		validationViolations.addAll(ovalValidator.validate(objectToValidate));
		checkIDExists(objectToValidate.getGenericEntityType(), objectToValidate.getAssociableID(), objectToValidate, validationViolations);
		checkIDExists(objectToValidate.getSourceType(), objectToValidate.getSourceID(), objectToValidate, validationViolations);
		return validationViolations;
	}

	public List<ValidationViolation> validate(GenericCategory objectToValidate) {
		List<ValidationViolation> validationViolations = new ArrayList<ValidationViolation>();
		validationViolations.addAll(ovalValidator.validate(objectToValidate));
		GenericEntityType entityType = GenericEntityType.forCategoryType(objectToValidate.getType());
		if (entityType != null) {
			for (Iterator<MutableTimedAssociationKey> iter = objectToValidate.getParentKeyIterator(); iter.hasNext();) {
				MutableTimedAssociationKey key = iter.next();
				checkIDExists(objectToValidate.getType(), key.getAssociableID(), objectToValidate, validationViolations);
			}
			for (Iterator<MutableTimedAssociationKey> iter = objectToValidate.getChildrenKeyIterator(); iter.hasNext();) {
				MutableTimedAssociationKey key = iter.next();
				checkIDExists(objectToValidate.getType(), key.getAssociableID(), objectToValidate, validationViolations);
			}
		}
		return validationViolations;
	}

	public List<ValidationViolation> validate(GenericEntity objectToValidate) {
		List<ValidationViolation> validationViolations = new ArrayList<ValidationViolation>();
		validationViolations.addAll(ovalValidator.validate(objectToValidate));
		if (objectToValidate.getType() != null) {
			for (Iterator<MutableTimedAssociationKey> iter = objectToValidate.getCategoryIterator(); iter.hasNext();) {
				MutableTimedAssociationKey key = iter.next();
				checkIDExists(objectToValidate.getType().getCategoryType(), key.getAssociableID(), objectToValidate, validationViolations);
			}
		}
		if (objectToValidate.getParentID() != Persistent.UNASSIGNED_ID) {
			checkIDExists(objectToValidate.getType(), objectToValidate.getParentID(), objectToValidate, validationViolations);
		}
		return validationViolations;
	}

	private void checkIDExists(GenericEntityType entityType, int id, Object validatedObject, List<ValidationViolation> violations) {
		if (!EntityManager.getInstance().hasGenericEntity(entityType, id)) {
			violations.add(new DefaultValidationViolation(
					validatedObject,
					"No entity of id " + id + " of type " + entityType + " found",
					id));
		}
	}

	private void checkIDExists(int categoryType, int id, Object validatedObject, List<ValidationViolation> violations) {
		if (!EntityManager.getInstance().hasGenericCategory(categoryType, id)) {
			violations.add(new DefaultValidationViolation(validatedObject, "No category of id " + id + " of type " + categoryType
					+ " found", id));
		}
	}

}
