package de.kit.esmserver.uicomponents.validators;

@SuppressWarnings("serial")
public class DoubleRangeValidator extends DoubleValidator {
	private double max;
	private double min;
	private String errorMessage;

	public DoubleRangeValidator(double max, double min, String errorMessage,
			boolean allowNull, boolean allowNegative) {
		super(allowNull, allowNegative);
		this.max = max;
		this.min = min;
		this.errorMessage = errorMessage;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		super.validate(value);
		Double valDouble = Double.parseDouble(String.valueOf(value));
		if (!(valDouble >= min && valDouble <= max)) {
			throw new InvalidValueException(errorMessage);
		}
	}
}
