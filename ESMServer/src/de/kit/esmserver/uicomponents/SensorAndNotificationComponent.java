package de.kit.esmserver.uicomponents;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import de.kit.esmserver.uicomponents.validators.IntegerValidator;

public class SensorAndNotificationComponent extends AbstractWritableComponent {
	private static final long serialVersionUID = -7094772384042699620L;
	private List<CheckBox> sensorList;
	private CheckBox ring;
	private CheckBox vibrate;
	private CheckBox notificationLed;
	private TextField textFieldMaxNotifications;
	private TextField textFieldCoolDown;
	private ComboBox voluntaryComboBox;
	private GridLayout mainGridLayout;
	private IntegerValidator integerValidator;

	public SensorAndNotificationComponent() {
		integerValidator = new IntegerValidator(false, false);
		sensorList = new ArrayList<CheckBox>();
		setCompositionRoot(buildMainLayout());
	}

	private Panel buildMainLayout() {
		mainGridLayout = new GridLayout(2, 1);

		GridLayout gridLayout = new GridLayout(5, 2);
		gridLayout.setCaption("Sensors: ");
		sensorList.add(new CheckBox("Weather"));
		sensorList.add(new CheckBox("Time"));
		sensorList.add(new CheckBox("Notifications"));
		sensorList.add(new CheckBox("Accelerometer"));
		sensorList.add(new CheckBox("AmbientLight"));
		sensorList.add(new CheckBox("Bluetooth"));
		sensorList.add(new CheckBox("Location"));
		sensorList.add(new CheckBox("ScreenActivity"));
		sensorList.add(new CheckBox("Telephone"));
		sensorList.add(new CheckBox("UserActivity"));
		for (CheckBox checkBox : sensorList) {
			gridLayout.addComponent(checkBox);
		}

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setCaption("Notification: ");
		ring = new CheckBox("Ring");
		vibrate = new CheckBox("Vibration");
		notificationLed = new CheckBox("NotificationLED");
		horizontalLayout.addComponent(ring);
		horizontalLayout.addComponent(vibrate);
		horizontalLayout.addComponent(notificationLed);

		textFieldMaxNotifications = new TextField(
				"Maximum Notifications per Day");
		textFieldMaxNotifications.setValidationVisible(true);
		textFieldMaxNotifications.setImmediate(true);
		textFieldMaxNotifications.addValidator(integerValidator);
		textFieldMaxNotifications.setValue("12");

		textFieldCoolDown = new TextField("Cooldown-Time (Min.)");
		textFieldCoolDown.setValidationVisible(true);
		textFieldCoolDown.setImmediate(true);
		textFieldCoolDown.addValidator(integerValidator);
		textFieldCoolDown.setValue("10");

		HorizontalLayout horizontalLayout2 = new HorizontalLayout();
		horizontalLayout2.setCaption("Allow voluntary samples?");
		voluntaryComboBox = new ComboBox();
		voluntaryComboBox.addItem("True");
		voluntaryComboBox.addItem("False");
		voluntaryComboBox.setValue("False");
		horizontalLayout2.addComponent(voluntaryComboBox);

		mainGridLayout.addComponent(gridLayout);
		mainGridLayout.addComponent(textFieldMaxNotifications);
		mainGridLayout.addComponent(horizontalLayout);
		mainGridLayout.addComponent(textFieldCoolDown);
		mainGridLayout.addComponent(new Label());
		mainGridLayout.addComponent(horizontalLayout2);

		int browserWidth = UI.getCurrent().getPage().getBrowserWindowWidth();
		mainGridLayout.setWidth(String.valueOf(browserWidth - 650));

		panel.setContent(mainGridLayout);

		return panel;
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		buildSensorXML(writer);
		buildNotificationXML(writer);
	}

	private void buildNotificationXML(XMLStreamWriter writer)
			throws XMLStreamException {
		writer.writeEmptyElement("notification");
		writer.writeAttribute("ring", ring.getValue().toString());
		writer.writeAttribute("vibrate", vibrate.getValue().toString());
		writer.writeAttribute("notificationLed", notificationLed.getValue()
				.toString());
		writer.writeAttribute("maxNotifications",
				textFieldMaxNotifications.getValue());
		writer.writeAttribute("cooldownTime", String.valueOf((Integer
				.parseInt(textFieldCoolDown.getValue()) * 60000)));
	}

	private void buildSensorXML(XMLStreamWriter writer)
			throws XMLStreamException {
		writer.writeStartElement("sensors");
		for (CheckBox checkBox : sensorList) {
			if (checkBox.getValue()) {
				writer.writeStartElement("sensor");
				writer.writeCharacters(checkBox.getCaption().toLowerCase());
				writer.writeEndElement();
			}
		}
		writer.writeEndElement();
	}

	public String getVoluntaryValue() {
		return (String) voluntaryComboBox.getValue();
	}

	@Override
	public boolean isValid() {
		return textFieldCoolDown.isValid()
				&& textFieldMaxNotifications.isValid();
	}
}
