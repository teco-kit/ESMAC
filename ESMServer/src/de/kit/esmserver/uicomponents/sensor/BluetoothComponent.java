package de.kit.esmserver.uicomponents.sensor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.basiccomponents.MacAddressComponent;
import de.kit.esmserver.uicomponents.validators.IntegerValidator;
import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class BluetoothComponent extends AbstractSensorComponent {
	private static final String MAC_IDENTIFIER = "mac";
	private static final String COUNT_IDENTIFIER = "count";
	private static final String NAME_IDENTIFIER = "name";
	private static final long serialVersionUID = -2281150437265106084L;
	private Label keyLabel;
	private ComboBox keyComboBox;
	private HorizontalLayout horizontalLayout;
	private Button addPackageButton;
	private Button removePackageButton;
	private List<TextField> values;
	private List<MacAddressComponent> macs;
	private TextField count;
	private MacAddressComponent firstMAC;
	private TextField firstName;
	private NotNullValidator notNullValidator;

	@SuppressWarnings("serial")
	public BluetoothComponent() {
		values = new ArrayList<TextField>();
		macs = new ArrayList<MacAddressComponent>();
		notNullValidator = new NotNullValidator();
		buildMainLayout();
		keyComboBox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				switch (String.valueOf(event.getProperty().getValue())) {
				case COUNT_IDENTIFIER:
					setBackToBaseLayout();
					horizontalLayout.addComponent(new Label(COUNT_IDENTIFIER));
					horizontalLayout.addComponent(count);
					setFullOperatorComboBox();
					BluetoothComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					break;
				case NAME_IDENTIFIER:
					setBackToBaseLayout();
					values.clear();
					values.add(firstName);
					horizontalLayout.addComponent(new Label("Name"));
					horizontalLayout.addComponent(firstName);
					horizontalLayout.addComponent(removePackageButton);
					horizontalLayout.addComponent(addPackageButton);
					setRestrictedOperatorComboBox();
					BluetoothComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					break;
				case MAC_IDENTIFIER:
					setBackToBaseLayout();
					macs.clear();
					macs.add(firstMAC);
					horizontalLayout.addComponent(new Label("Mac-Address"));
					horizontalLayout.addComponent(firstMAC);
					horizontalLayout.addComponent(removePackageButton);
					horizontalLayout.addComponent(addPackageButton);
					setRestrictedOperatorComboBox();
					BluetoothComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					break;

				default:
					break;
				}
			}

		});
	}

	@Override
	protected Panel buildMainLayout() {
		horizontalLayout = new HorizontalLayout();
		keyLabel = new Label("Key");
		keyComboBox = new ComboBox();
		keyComboBox.addItem(NAME_IDENTIFIER);
		keyComboBox.addItem(COUNT_IDENTIFIER);
		keyComboBox.addItem(MAC_IDENTIFIER);
		keyComboBox.setImmediate(true);
		keyComboBox.setValidationVisible(true);
		keyComboBox.addValidator(notNullValidator);

		count = new TextField();
		count.setImmediate(true);
		count.setValidationVisible(true);
		count.addValidator(new IntegerValidator(false, false));

		firstName = new TextField();
		firstName.setImmediate(true);
		firstName.setValidationVisible(true);
		firstName.addValidator(notNullValidator);

		firstMAC = new MacAddressComponent("");

		setRestrictedOperatorComboBox();
		setBackToBaseLayout();
		initializeButtons();
		panel.setContent(horizontalLayout);
		panel.setCaption("Bluetooth");
		Notification
				.show("Please note bluetoothsensor doesn't trigger automatically, you should use an additional sensor.",
						Notification.Type.WARNING_MESSAGE);
		return super.buildMainLayout();
	}

	private void setBackToBaseLayout() {
		horizontalLayout.removeAllComponents();
		horizontalLayout.addComponent(keyLabel);
		horizontalLayout.addComponent(keyComboBox);
		horizontalLayout.addComponent(getLabelOperator());
		horizontalLayout.addComponent(getComboboxOperator());
	}

	@SuppressWarnings("serial")
	private void initializeButtons() {
		addPackageButton = new Button("+");
		addPackageButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				switch (keyComboBox.getValue().toString()) {
				case MAC_IDENTIFIER:
					addMacComponent();
					break;
				case NAME_IDENTIFIER:
					addTextfield();
					break;

				default:
					break;
				}
			}

			private void addMacComponent() {
				MacAddressComponent macAddressComponent = new MacAddressComponent(
						"");
				macs.add(macAddressComponent);
				if (getLabelOperator().isEnabled()) {
					int index = horizontalLayout.getComponentCount() - 4;
					horizontalLayout.addComponent(macAddressComponent, index);
					horizontalLayout.addComponent(new Label("or"), index);
					setAlignmentMiddleCenter();
				} else {
					int index = horizontalLayout.getComponentCount() - 2;
					horizontalLayout.addComponent(macAddressComponent, index);
					horizontalLayout.addComponent(new Label("or"), index);
					setAlignmentMiddleCenter();
				}
			}

			private void addTextfield() {
				TextField newTextField = new TextField();
				newTextField.setImmediate(true);
				newTextField.setValidationVisible(true);
				newTextField.addValidator(notNullValidator);
				values.add(newTextField);
				if (getLabelOperator().isEnabled()) {
					int index = horizontalLayout.getComponentCount() - 4;
					horizontalLayout.addComponent(newTextField, index);
					horizontalLayout.addComponent(new Label("or"), index);
					setAlignmentMiddleCenter();
				} else {
					int index = horizontalLayout.getComponentCount() - 2;
					horizontalLayout.addComponent(newTextField, index);
					horizontalLayout.addComponent(new Label("or"), index);
					setAlignmentMiddleCenter();
				}
			}
		});
		removePackageButton = new Button("-");
		removePackageButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				switch (keyComboBox.getValue().toString()) {
				case MAC_IDENTIFIER:
					removeComponent(macs);
					break;
				case NAME_IDENTIFIER:
					removeComponent(values);
					break;

				default:
					break;
				}

			}

			private void removeComponent(List<? extends Component> values) {
				if (values.size() > 1) {
					if (getLabelOperator().isEnabled()) {
						Component lastLabel = horizontalLayout
								.getComponent(horizontalLayout
										.getComponentCount() - 6);
						Component lastComponent = horizontalLayout
								.getComponent(horizontalLayout
										.getComponentCount() - 5);
						values.remove(lastComponent);
						horizontalLayout.removeComponent(lastComponent);
						horizontalLayout.removeComponent(lastLabel);

					} else {
						Component lastLabel = horizontalLayout
								.getComponent(horizontalLayout
										.getComponentCount() - 4);
						Component lastTextField = horizontalLayout
								.getComponent(horizontalLayout
										.getComponentCount() - 3);
						values.remove(lastTextField);
						horizontalLayout.removeComponent(lastTextField);
						horizontalLayout.removeComponent(lastLabel);
					}
				}
			}
		});
	}

	@Override
	public boolean isValid() {
		boolean isValid;
		switch (String.valueOf(keyComboBox.getValue())) {
		case COUNT_IDENTIFIER:
			return super.isValid() && keyComboBox.isValid() && count.isValid();
		case MAC_IDENTIFIER:
			isValid = super.isValid() && keyComboBox.isValid();
			for (MacAddressComponent macComponent : macs) {
				isValid = isValid && macComponent.isValid();
			}
			return isValid && !checkAnswersForDuplicates(macs);
		default:
			// defaultcase sind die anderen beieden Fälle
			isValid = super.isValid() && keyComboBox.isValid();
			for (TextField textField : values) {
				isValid = isValid && textField.isValid();
			}
			return isValid && !checkAnswersForDuplicates(values);
		}
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("bluetooth");
		switch (String.valueOf(keyComboBox.getValue())) {
		case COUNT_IDENTIFIER:
			writer.writeEmptyElement(COUNT_IDENTIFIER);
			writer.writeAttribute("operator", getOperator());
			writer.writeAttribute("value", count.getValue());
			break;
		case NAME_IDENTIFIER:
			writer.writeStartElement("name");
			writer.writeAttribute("operator", getOperator());
			for (TextField value : values) {
				writer.writeStartElement("deviceName");
				writer.writeCharacters(value.getValue());
				writer.writeEndElement();
			}
			writer.writeEndElement();
			break;
		case MAC_IDENTIFIER:
			writer.writeStartElement("mac");
			writer.writeAttribute("operator", getOperator());
			for (MacAddressComponent value : macs) {
				writer.writeStartElement("address");
				writer.writeCharacters(value.getValue());
				writer.writeEndElement();
			}
			writer.writeEndElement();
			break;
		}
		writer.writeEndElement();
	}

	private boolean checkAnswersForDuplicates(
			List<? extends Property<String>> components) {
		Set<String> listStrings = new HashSet<String>();
		for (Property<String> value : components) {
			if (!listStrings.add(value.getValue())) {
				return true;
			}
		}
		return false;
	}

	public boolean checkAnswersForDuplicates() {
		switch (keyComboBox.getValue().toString()) {
		case MAC_IDENTIFIER:
			return checkAnswersForDuplicates(macs);
		case NAME_IDENTIFIER:
			return checkAnswersForDuplicates(values);

		default:
			return false;
		}
	}

}
