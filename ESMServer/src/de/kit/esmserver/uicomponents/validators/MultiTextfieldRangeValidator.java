package de.kit.esmserver.uicomponents.validators;

import com.vaadin.ui.TextField;

public class MultiTextfieldRangeValidator extends MinMaxValidator {
	private static final long serialVersionUID = 8114909409386587648L;
	private TextField betweenField;

	public MultiTextfieldRangeValidator(TextField minBoundField,
			TextField maxBoundField, TextField betweenField) {
		super(minBoundField, maxBoundField);
		this.betweenField = betweenField;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		try {
			super.validate(value);
			int min = Integer.parseInt(minBoundField.getValue());
			int max = Integer.parseInt(maxBoundField.getValue());
			int between = Integer.parseInt(betweenField.getValue());
			if (!(min <= between && between <= max)) {
				throw new InvalidValueException(
						"Field-Value isn't between Min. Bound and Max. Bound");
			}
		} catch (NumberFormatException e) {
			throw new InvalidValueException("fields not valid");
		}
	}
}
