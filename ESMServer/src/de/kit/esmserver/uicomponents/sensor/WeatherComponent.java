package de.kit.esmserver.uicomponents.sensor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.DoubleValidator;
import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class WeatherComponent extends AbstractSensorComponent {

	private static final long serialVersionUID = -6741269572576028315L;
	private GridLayout mainLayout;
	private TextField textfieldValue;
	private Label labelValue;
	private ComboBox comboboxKey;
	private Label labelKey;
	private ComboBox comboBoxCondition;
	private NotNullValidator notNullValidator;

	public WeatherComponent() {
		notNullValidator = new NotNullValidator();
		buildMainLayout();
		comboboxKey.addItem("condition");
		comboboxKey.addItem("degree");
		comboboxKey.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -2171473579929275700L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().equals("condition")) {
					setRestrictedOperatorComboBox();
					mainLayout.removeAllComponents();
					mainLayout.addComponent(labelKey);
					mainLayout.addComponent(comboboxKey);
					mainLayout.addComponent(getLabelOperator());
					mainLayout.addComponent(getComboboxOperator());
					mainLayout.addComponent(labelValue);
					mainLayout.addComponent(comboBoxCondition);
					WeatherComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
				} else {
					setFullOperatorComboBox();
					mainLayout.removeAllComponents();
					mainLayout.addComponent(labelKey);
					mainLayout.addComponent(comboboxKey);
					mainLayout.addComponent(getLabelOperator());
					mainLayout.addComponent(getComboboxOperator());
					mainLayout.addComponent(labelValue);
					mainLayout.addComponent(textfieldValue);
					WeatherComponent.super.buildMainLayout();

					setAlignmentMiddleCenter();
				}
			}
		});

	}

	protected Panel buildMainLayout() {
		mainLayout = new GridLayout(9, 1);

		// labelKey
		labelKey = new Label();
		labelKey.setImmediate(false);
		labelKey.setValue("Key");
		mainLayout.addComponent(labelKey);

		// comboboxKey
		comboboxKey = new ComboBox();
		comboboxKey.setImmediate(true);
		comboboxKey.setValidationVisible(true);
		comboboxKey.addValidator(notNullValidator);
		mainLayout.addComponent(comboboxKey);

		// labelValue
		labelValue = new Label();
		labelValue.setImmediate(false);
		labelValue.setValue("Value");

		textfieldValue = new TextField();
		textfieldValue.setImmediate(true);
		textfieldValue.setValidationVisible(true);
		textfieldValue.addValidator(new DoubleValidator(false, true));

		comboBoxCondition = new ComboBox();
		comboBoxCondition.setImmediate(true);
		comboBoxCondition.setValidationVisible(true);
		comboBoxCondition.addValidator(notNullValidator);

		comboBoxCondition.addItem("clear sky");
		comboBoxCondition.addItem("few clouds");
		comboBoxCondition.addItem("scattered clouds");
		comboBoxCondition.addItem("broken clouds");
		comboBoxCondition.addItem("shower rain");
		comboBoxCondition.addItem("rain");
		comboBoxCondition.addItem("thunderstorm");
		comboBoxCondition.addItem("snow");
		comboBoxCondition.addItem("mist");

		panel.setCaption(this.getClass().getSimpleName());
		panel.setContent(mainLayout);

		return super.buildMainLayout();
	}

	@Override
	public boolean isValid() {
		if (comboboxKey.getValue() != null) {
			if (comboboxKey.getValue().equals("degree")) {
				return super.isValid() && comboboxKey.isValid()
						&& textfieldValue.isValid();
			} else {
				return super.isValid() && comboboxKey.isValid()
						&& comboBoxCondition.isValid();
			}
		} else {
			return false;
		}

	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("weather");
		if (comboboxKey.getValue().equals("degree")) {
			writer.writeStartElement("degree");
			writer.writeAttribute("operator", getOperator());
			writer.writeAttribute("value", textfieldValue.getValue());
			writer.writeEndElement();
		} else {
			writer.writeStartElement("condition");
			writer.writeAttribute("operator", getOperator());
			writer.writeAttribute("value", comboBoxCondition.getValue()
					.toString());
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

}
