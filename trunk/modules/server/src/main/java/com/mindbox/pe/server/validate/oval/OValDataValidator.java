package com.mindbox.pe.server.validate.oval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.common.validate.oval.OValValidationViolation;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.server.validate.DataValidator;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.MessageRenderer;
import net.sf.oval.internal.util.StringUtils;
import net.sf.oval.localization.MessageResolver;

public final class OValDataValidator implements DataValidator {

	private static class MessageResolverImpl extends net.sf.oval.localization.MessageResolverImpl implements MessageResolver {

		@Override
		public String getMessage(String arg0) {
			String message = ResourceUtil.getInstance().getResource(arg0);
			if (message.equals(arg0)) {
				message = super.getMessage(arg0);
			}
			return message;
		}
	}

	private static class ValidatorAux extends Validator {
		private String getContextName(OValContext context) {
			if (context instanceof FieldContext) {
				return ((FieldContext) context).getField().getName();
			}
			else if (context instanceof MethodReturnValueContext) {
				return ((MethodReturnValueContext) context).getMethod().getName();
			}
			else {
				return context.toString();
			}
		}

		@Override
		protected String renderMessage(OValContext context, Object value, String messageKey, Map<String, String> messageValues) {
			String message = MessageRenderer.renderMessage(messageKey, messageValues);
			if (message.indexOf('{') != -1) {
				message = StringUtils.replaceAll(message, "{context}", getContextName(context));
				message = StringUtils.replaceAll(message, "{invalidValue}", value == null ? "null" : value.toString());
			}
			return message;
		}
	}

	private static OValDataValidator instance;

	public static OValDataValidator getInstance() {
		if (instance == null) {
			Validator.setMessageResolver(new MessageResolverImpl());
			instance = new OValDataValidator();
		}
		return instance;
	}

	private static final List<ValidationViolation> toValidationViolationList(List<ConstraintViolation> violations) {
		List<ValidationViolation> validationViolations = new ArrayList<ValidationViolation>();
		for (ConstraintViolation cv : violations) {
			validationViolations.add(new OValValidationViolation(cv));
		}
		return validationViolations;
	}

	private final ValidatorAux validatorAux = new ValidatorAux();

	private OValDataValidator() {
	}

	@Override
	public List<ValidationViolation> validate(Object objectToValidate) {
		return validateObject(objectToValidate);
	}

	private final List<ValidationViolation> validateObject(Object objectToValidate) {
		List<ConstraintViolation> violations = validatorAux.validate(objectToValidate);
		return toValidationViolationList(violations);
	}
}
