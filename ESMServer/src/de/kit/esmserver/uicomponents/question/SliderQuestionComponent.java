package de.kit.esmserver.uicomponents.question;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.DoubleValidator;
import de.kit.esmserver.uicomponents.validators.MultiTextfieldRangeValidator;

public class SliderQuestionComponent extends LikertQuestionComponent {

	private static final long serialVersionUID = -3395358756631730173L;
	protected TextField pointerTextField;
	private MultiTextfieldRangeValidator multiTextfieldRangeValidator;

	@Override
	protected Panel buildMainLayout() {
		super.buildMainLayout();
		Label label = new Label("Pointer Start");
		mainLayout.addComponent(label);
		pointerTextField = new TextField();
		pointerTextField.setImmediate(true);
		pointerTextField.setValidationVisible(true);
		pointerTextField.setWidth("150");

		multiTextfieldRangeValidator = new MultiTextfieldRangeValidator(
				minBoundField, maxBoundField, pointerTextField);
		pointerTextField.addValidator(new DoubleValidator(false, false));
		pointerTextField.addValidator(multiTextfieldRangeValidator);
		minBoundField.addValidator(multiTextfieldRangeValidator);
		minBoundField.removeValidator(minMaxValidator);
		maxBoundField.addValidator(multiTextfieldRangeValidator);
		maxBoundField.removeValidator(minMaxValidator);

		minBoundField.setValue("0");
		maxBoundField.setValue("100");
		pointerTextField.setValue("50");

		mainLayout.addComponent(pointerTextField);
		panel.setCaption("Slider Question");
		return panel;

	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEmptyElement("visualAnalogQuestion");
		writer.writeAttribute("name", textfieldValue.getValue());
		writer.writeAttribute("minBound", minBoundField.getValue());
		writer.writeAttribute("maxBound", maxBoundField.getValue());
		writer.writeAttribute("minDescription", minDescriptionField.getValue());
		writer.writeAttribute("maxDescription", maxDescriptionField.getValue());
		writer.writeAttribute("pointer", pointerTextField.getValue());
	}

	@Override
	public boolean isValid() {
		// if (super.isValid() && pointerTextField.isValid()) {
		// int minBoundInt = Integer.parseInt(minBoundField.getValue());
		// int maxBoundInt = Integer.parseInt(maxBoundField.getValue());
		// int pointerInt = Integer.parseInt(pointerTextField.getValue());
		// if (!(minBoundInt <= pointerInt && pointerInt <= maxBoundInt)) {
		// Notification.show("pointer isn't between min and max bound");
		// return false;
		// } else {
		// return true;
		// }
		// }
		return super.isValid() && pointerTextField.isValid();
	}

}
