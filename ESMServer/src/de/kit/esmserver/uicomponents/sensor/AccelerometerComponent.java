package de.kit.esmserver.uicomponents.sensor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.DoubleValidator;

public class AccelerometerComponent extends AbstractSensorComponent {
	private static final long serialVersionUID = 2261795314807031888L;
	private TextField xTextField;
	private TextField yTextField;
	private TextField zTextField;
	private TextField accelerationTextfield;
	private DoubleValidator digitValidator;
	private DoubleValidator notnegativeDigValidator;

	public AccelerometerComponent() {
		digitValidator = new DoubleValidator(true, true);
		notnegativeDigValidator = new DoubleValidator(true, false);
		buildMainLayout();
	}

	@Override
	protected Panel buildMainLayout() {
		GridLayout gridLayout = new GridLayout(13, 1);
		gridLayout.addComponent(getLabelOperator());
		gridLayout.addComponent(getComboboxOperator());
		gridLayout.addComponent(new Label("x"));
		xTextField = new TextField();
		xTextField.setImmediate(true);
		xTextField.setValidationVisible(true);
		xTextField.setWidth("100");
		xTextField.addValidator(digitValidator);
		gridLayout.addComponent(xTextField);
		yTextField = new TextField();
		yTextField = new TextField();
		yTextField.setImmediate(true);
		yTextField.setValidationVisible(true);
		yTextField.setWidth("100");
		yTextField.addValidator(digitValidator);
		gridLayout.addComponent(new Label("y"));
		gridLayout.addComponent(yTextField);
		zTextField = new TextField();
		zTextField = new TextField();
		zTextField.setImmediate(true);
		zTextField.setValidationVisible(true);
		zTextField.setWidth("100");
		zTextField.addValidator(digitValidator);
		gridLayout.addComponent(new Label("z"));
		gridLayout.addComponent(zTextField);
		accelerationTextfield = new TextField();
		accelerationTextfield.setImmediate(true);
		accelerationTextfield.setValidationVisible(true);
		accelerationTextfield.setWidth("100");
		accelerationTextfield.addValidator(notnegativeDigValidator);
		gridLayout.addComponent(new Label("acceleration"));
		gridLayout.addComponent(accelerationTextfield);
		setFullOperatorComboBox();
		panel.setContent(gridLayout);
		panel.setCaption("Accelerometer");
		return super.buildMainLayout();
	}

	@Override
	public boolean isValid() {
		if (!isNotEmpty(xTextField) && !isNotEmpty(yTextField)
				&& !isNotEmpty(zTextField)
				&& !isNotEmpty(accelerationTextfield)) {
			Notification.show("One value in accelerometer should be set",
					Notification.Type.ERROR_MESSAGE);
			return false;
		} else {
			boolean isValid = true;
			if (isNotEmpty(xTextField)) {
				isValid = isValid && xTextField.isValid();
			}
			if (isNotEmpty(yTextField)) {
				isValid = isValid && yTextField.isValid();
			}
			if (isNotEmpty(zTextField)) {
				isValid = isValid && zTextField.isValid();
			}
			if (isNotEmpty(accelerationTextfield)) {
				isValid = isValid && accelerationTextfield.isValid();
			}
			return isValid;
		}
	}

	private boolean isNotEmpty(TextField textField) {
		return !(textField.getValue().equals("") || textField.getValue() == null);
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEmptyElement("accelerometer");
		writer.writeAttribute("operator", getOperator());
		if (isNotEmpty(xTextField)) {
			writer.writeAttribute("x", xTextField.getValue());
		}
		if (isNotEmpty(yTextField)) {
			writer.writeAttribute("y", yTextField.getValue());
		}
		if (isNotEmpty(zTextField)) {
			writer.writeAttribute("z", zTextField.getValue());
		}
		if (isNotEmpty(accelerationTextfield)) {
			writer.writeAttribute("acceleration",
					accelerationTextfield.getValue());
		}
	}
}
