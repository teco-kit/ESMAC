package de.kit.esmserver.uicomponents.question.conditional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.DoubleValidator;
import de.kit.esmserver.uicomponents.validators.MultiTextfieldRangeValidator;
import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class SliderQuestionConditionalComponent extends
		LikertQuestionConditionalComponent {
	private static final long serialVersionUID = 8730515026005657886L;
	private TextField pointerTextField;

	public SliderQuestionConditionalComponent() {
		digitValidator = new DoubleValidator(false, false);
		notNullValidator = new NotNullValidator();
	}

	@Override
	protected Panel buildMainLayout() {
		Panel p = super.buildMainLayout();
		pointerTextField = new TextField();
		pointerTextField.setImmediate(true);
		pointerTextField.setValidationVisible(true);
		pointerTextField.setWidth("120");
		pointerTextField.addValidator(digitValidator);

		MultiTextfieldRangeValidator muValidator = new MultiTextfieldRangeValidator(
				minBoundField, maxBoundField, pointerTextField);
		minBoundField.addValidator(muValidator);
		minBoundField.removeValidator(minMaxValidator);
		maxBoundField.addValidator(muValidator);
		maxBoundField.removeValidator(minMaxValidator);
		minBoundField.setValue("0");
		maxBoundField.setValue("100");
		pointerTextField.setValue("50");
		requiredBoundField.setValue("80");

		mainLayout.addComponent(new Label("pointer"));
		mainLayout.addComponent(pointerTextField);
		condPanel.setCaption("Slider Question");
		return p;
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("conditionalQuestion");
		writer.writeEmptyElement("visualAnalogQuestionConditional");
		writer.writeAttribute("name", textfieldValue.getValue());
		writer.writeAttribute("minBound", minBoundField.getValue());
		writer.writeAttribute("maxBound", maxBoundField.getValue());
		writer.writeAttribute("minDescription", minDescriptionField.getValue());
		writer.writeAttribute("maxDescription", maxDescriptionField.getValue());
		writer.writeAttribute("pointer", pointerTextField.getValue());
		writer.writeAttribute("requiredValue", requiredBoundField.getValue());
		conditionedQuestion.writeXML(writer);
		writer.writeEndElement();
	}

	@Override
	public boolean isValid() {
		return super.isValid() && pointerTextField.isValid()
				&& conditionedQuestion.isValid();
	}
}
