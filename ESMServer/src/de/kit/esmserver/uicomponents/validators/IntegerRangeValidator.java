package de.kit.esmserver.uicomponents.validators;

@SuppressWarnings("serial")
public class IntegerRangeValidator extends IntegerValidator {
	private int max;
	private int min;
	String errorMessage;

	public IntegerRangeValidator(int max, int min, String errorMessage,
			boolean allowNull, boolean allowNegative) {
		super(allowNegative, allowNull);
		this.max = max;
		this.min = min;
		this.errorMessage = errorMessage;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		super.validate(value);
		Integer valueInt = Integer.parseInt(String.valueOf(value));
		if (!(valueInt >= min && valueInt <= max)) {
			throw new InvalidValueException(errorMessage);
		}
	}
}
