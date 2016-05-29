package de.kit.esmserver.uicomponents.validators;

import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class NotNullValidator implements Validator {

	public NotNullValidator() {
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (value == null) {
			throw new InvalidValueException("Null not allowed");
		} else if (value.equals("")) {
			throw new InvalidValueException("Null not allowed");
		}
	}

}
