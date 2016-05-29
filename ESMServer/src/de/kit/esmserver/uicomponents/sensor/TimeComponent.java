package de.kit.esmserver.uicomponents.sensor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.basiccomponents.CheckBoxComponent;
import de.kit.esmserver.uicomponents.basiccomponents.TimePickerComponent;
import de.kit.esmserver.uicomponents.validators.IntegerValidator;
import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class TimeComponent extends AbstractSensorComponent {
	private static final long serialVersionUID = 3302997148118389346L;
	private ComboBox keyComboBox;
	private GridLayout gridLayout;
	private Label keyLabel;
	private ComboBox daytimeBox;
	private CheckBoxComponent onWeekendCheckBox;
	private TimePickerComponent beginTime;
	private TimePickerComponent endTime;
	private TextField valueTextfield;
	private List<CheckBoxComponent> checkList;
	private IntegerValidator valueValidator;
	private NotNullValidator notNullValidator;

	@SuppressWarnings("serial")
	public TimeComponent() {
		checkList = new ArrayList<CheckBoxComponent>();
		valueValidator = new IntegerValidator(false, false);
		notNullValidator = new NotNullValidator();
		buildMainLayout();
		keyComboBox.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				onWeekendCheckBox = new CheckBoxComponent("onWeekend");
				switch (String.valueOf(event.getProperty().getValue())) {
				case "specificTime":
					setBackToBaseLayout();
					gridLayout.addComponent(beginTime);
					setFullOperatorComboBox();
					TimeComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					break;
				case "weekDay":
					setBackToBaseLayout();
					for (CheckBoxComponent checkBox : checkList) {
						gridLayout.addComponent(checkBox);
					}
					setRestrictedOperatorComboBox();
					TimeComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					Notification
							.show("Please note weekDay doesn't trigger automatically, you should use an additional sensor.",
									Notification.Type.WARNING_MESSAGE);
					break;
				case "daytime":
					setBackToBaseLayout();
					gridLayout.addComponent(new Label("daytime"));
					gridLayout.addComponent(daytimeBox);
					setRestrictedOperatorComboBox();
					TimeComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					Notification
							.show("Please note daytime doesn't trigger automatically, you should use an additional sensor.",
									Notification.Type.WARNING_MESSAGE);
					break;
				case "timeRange":
					setBackToBaseLayout();
					addBeginAndEndFieldsToLayout();
					setRestrictedOperatorComboBox();
					TimeComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					Notification
							.show("Please note timeRange doesn't trigger automatically, you should use an additional sensor.",
									Notification.Type.WARNING_MESSAGE);
					break;
				case "onWeekend":
					setBackToBaseLayout();
					setRestrictedOperatorComboBox();
					gridLayout.addComponent(onWeekendCheckBox);
					TimeComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					Notification
							.show("Please note onWeekend doesn't trigger automatically, you should use an additional sensor.",
									Notification.Type.WARNING_MESSAGE);
					break;
				case "random":
					setBackToBaseLayout();
					gridLayout.removeComponent(getLabelOperator());
					gridLayout.removeComponent(getComboboxOperator());
					gridLayout.addComponent(new Label("count"));
					gridLayout.addComponent(valueTextfield);
					addBeginAndEndFieldsToLayout();
					setRestrictedOperatorComboBox();
					TimeComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					Notification
							.show("Please be sure if you use random, you should use it only once and not with some other Events in one rule!",
									Notification.Type.WARNING_MESSAGE);
					break;
				case "timeInterval":
					setBackToBaseLayout();
					gridLayout.removeComponent(getLabelOperator());
					gridLayout.removeComponent(getComboboxOperator());
					gridLayout.addComponent(new Label("interval in min."));
					gridLayout.addComponent(valueTextfield);
					addBeginAndEndFieldsToLayout();
					setRestrictedOperatorComboBox();
					TimeComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					Notification
							.show("Please be sure if you use timeInterval, you should use it only once and not with some other Events in one rule!",
									Notification.Type.WARNING_MESSAGE);
					break;

				default:
					break;
				}

			}
		});
	}

	@Override
	protected Panel buildMainLayout() {
		gridLayout = new GridLayout(14, 1);

		valueTextfield = new TextField();
		valueTextfield.setImmediate(true);
		valueTextfield.setValidationVisible(true);
		valueTextfield.addValidator(valueValidator);
		valueTextfield.setWidth("80");

		beginTime = new TimePickerComponent("begin");
		endTime = new TimePickerComponent("end");

		daytimeBox = new ComboBox();
		daytimeBox.addItem("Morning");
		daytimeBox.addItem("Noon");
		daytimeBox.addItem("Afternoon");
		daytimeBox.addItem("Evening");
		daytimeBox.addItem("Night");
		daytimeBox.setImmediate(true);
		daytimeBox.setValidationVisible(true);
		daytimeBox.addValidator(notNullValidator);

		checkList.add(new CheckBoxComponent("Monday"));
		checkList.add(new CheckBoxComponent("Tuesday"));
		checkList.add(new CheckBoxComponent("Wednesday"));
		checkList.add(new CheckBoxComponent("Thursday"));
		checkList.add(new CheckBoxComponent("Friday"));
		checkList.add(new CheckBoxComponent("Saturday"));
		checkList.add(new CheckBoxComponent("Sunday"));

		keyLabel = new Label("Key");
		keyComboBox = new ComboBox();
		keyComboBox.addItem("specificTime");
		keyComboBox.addItem("daytime");
		keyComboBox.addItem("timeRange");
		keyComboBox.addItem("weekDay");
		keyComboBox.addItem("onWeekend");
		keyComboBox.addItem("random");
		keyComboBox.addItem("timeInterval");
		keyComboBox.setImmediate(true);
		keyComboBox.setValidationVisible(true);
		keyComboBox.addValidator(notNullValidator);
		setBackToBaseLayout();
		setFullOperatorComboBox();
		panel.setContent(gridLayout);
		panel.setCaption("Time");
		return super.buildMainLayout();
	}

	private void setBackToBaseLayout() {
		gridLayout.removeAllComponents();
		gridLayout.addComponent(keyLabel);
		gridLayout.addComponent(keyComboBox);
		gridLayout.addComponent(getLabelOperator());
		gridLayout.addComponent(getComboboxOperator());
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("time");
		switch (String.valueOf(keyComboBox.getValue())) {
		case "specificTime":
			writer.writeEmptyElement("specificTime");
			writer.writeAttribute("operator", getOperator());
			writer.writeAttribute("value", beginTime.getValue());
			break;
		case "weekDay":
			writer.writeStartElement("weekDay");
			writer.writeAttribute("operator", getOperator());
			for (CheckBoxComponent checkBox : checkList) {
				if (checkBox.getValue()) {
					writer.writeStartElement("day");
					writer.writeCharacters(checkBox.getCaption());
					writer.writeEndElement();
				}
			}
			writer.writeEndElement();
			break;
		case "daytime":
			writer.writeEmptyElement("daytime");
			writer.writeAttribute("operator", getOperator());
			writer.writeAttribute("value", daytimeBox.getValue().toString());
			break;
		case "timeRange":
			writer.writeEmptyElement("timeRange");
			writer.writeAttribute("operator", getOperator());
			writer.writeAttribute("beginValue", beginTime.getValue());
			writer.writeAttribute("endValue", endTime.getValue());
			break;
		case "onWeekend":
			writer.writeEmptyElement("onWeekend");
			writer.writeAttribute("operator", getOperator());
			writer.writeAttribute("value", onWeekendCheckBox.getValue()
					.toString());
			break;
		case "random":
			writer.writeEmptyElement("random");
			writer.writeAttribute("count", valueTextfield.getValue());
			writer.writeAttribute("beginValue", beginTime.getValue());
			writer.writeAttribute("endValue", endTime.getValue());
			break;
		case "timeInterval":
			writer.writeEmptyElement("timeInterval");
			writer.writeAttribute("interval",
					Integer.parseInt(valueTextfield.getValue()) * 60000 + "");
			writer.writeAttribute("beginValue", beginTime.getValue());
			writer.writeAttribute("endValue", endTime.getValue());
			break;
		}
		writer.writeEndElement();
	}

	@Override
	public boolean isValid() {
		switch (String.valueOf(keyComboBox.getValue())) {
		case "specificTime":
			return super.isValid() && keyComboBox.isValid();
		case "weekDay":
			for (CheckBoxComponent checkBox : checkList) {
				if (checkBox.getValue()) {
					return keyComboBox.isValid() && super.isValid();
				}
			}
		case "daytime":
			return super.isValid() && keyComboBox.isValid()
					&& daytimeBox.isValid();
		case "timeRange":
			return super.isValid() && keyComboBox.isValid();
		case "onWeekend":
			return super.isValid() && keyComboBox.isValid();
		case "random":
			return validateTimesWthoutOperator();
		case "timeInterval":
			return validateTimesWthoutOperator();
		default:
			return false;
		}
	}

	private boolean validateTimesWthoutOperator() {
		if (getConjunctionOperator().isEnabled()) {
			return validateTimes(keyComboBox.isValid()
					&& valueTextfield.isValid());
		} else {
			return validateTimes(getConjunctionOperator().isValid()
					&& keyComboBox.isValid() && valueTextfield.isValid());
		}
	}

	private boolean validateTimes(boolean validTimeValues) {
		if (validTimeValues) {
			if (!beginTime.getCalendarValue()
					.before(endTime.getCalendarValue())) {
				Notification.show(
						"Begin value is not before end value (TimeSensor: "
								+ keyComboBox.getValue() + ")",
						Notification.Type.ERROR_MESSAGE);
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private void addBeginAndEndFieldsToLayout() {
		gridLayout.addComponent(beginTime);
		gridLayout.addComponent(endTime);
	}
}
