package de.kit.esmserver.uicomponents.validators;

import com.vaadin.data.Validator;
import com.vaadin.ui.TextField;

public class MinMaxValidator implements Validator {
	private static final long serialVersionUID = -5428248567815649514L;
	protected TextField minBoundField, maxBoundField;

	public MinMaxValidator(TextField minBoundField, TextField maxBoundField) {
		this.minBoundField = minBoundField;
		this.maxBoundField = maxBoundField;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		try {
			int min = Integer.parseInt(minBoundField.getValue());
			int max = Integer.parseInt(maxBoundField.getValue());
			if (!(min < max)) {
				throw new InvalidValueException(
						"Minimum must be lower than Maximum.");
			}
		} catch (NumberFormatException e) {
			throw new InvalidValueException("Fields are not valid.");
		}

	}
}
