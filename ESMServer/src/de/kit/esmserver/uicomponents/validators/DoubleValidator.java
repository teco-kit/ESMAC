package de.kit.esmserver.uicomponents.validators;

@SuppressWarnings("serial")
public class DoubleValidator extends NotNullValidator {
	private boolean allowNull;
	private boolean allowNegative;

	public DoubleValidator(boolean allowNull, boolean allowNegative) {
		this.allowNull = allowNull;
		this.allowNegative = allowNegative;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!allowNull) {
			super.validate(value);
		}
		try {
			if (!(value == null || value.equals(""))) {
				Double.parseDouble((String) value);
				if (!allowNegative && Double.parseDouble((String) value) < 0) {
					throw new InvalidValueException(
							"No negative values are allowed");
				}
			}
		} catch (NumberFormatException e) {
			throw new InvalidValueException("Not a Double");
		}
	}
}
