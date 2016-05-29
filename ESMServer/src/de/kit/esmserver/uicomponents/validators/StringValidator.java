package de.kit.esmserver.uicomponents.validators;

@SuppressWarnings("serial")
public class StringValidator extends NotNullValidator {

	@Override
	public void validate(Object value) throws InvalidValueException {
		super.validate(value);
		String inputString = (String) value;
		if (inputString.matches(".*\\d.*")) {
			throw new InvalidValueException("Please avoid Numbers");
		}
	}

}
