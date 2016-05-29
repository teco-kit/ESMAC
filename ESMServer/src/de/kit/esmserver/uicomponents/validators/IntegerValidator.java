package de.kit.esmserver.uicomponents.validators;

@SuppressWarnings("serial")
public class IntegerValidator extends NotNullValidator {
	boolean allowNegative;
	private boolean allowNull;

	public IntegerValidator(boolean allowNegative, boolean allowNull) {
		this.allowNegative = allowNegative;
		this.allowNull = allowNull;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!allowNull) {
			super.validate(value);
		}
		try {
			if (!allowNegative && Integer.parseInt((String) value) < 0) {
				throw new InvalidValueException(
						"No negative values are allowed");
			}
		} catch (NumberFormatException e) {
			throw new InvalidValueException("Not an Integer");
		}

	}
}
