package de.kit.esmserver.uicomponents.basiccomponents;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class MacAddressComponent extends CustomComponent implements
		Property<String> {
	private static final long serialVersionUID = -8877543185739226636L;
	private HorizontalLayout horizontalLayout;
	private NotNullValidator notNullValidator;
	private Label labelCaption;
	private List<TextField> macAddressParts;

	public MacAddressComponent(String caption) {
		super();
		notNullValidator = new NotNullValidator();
		horizontalLayout = new HorizontalLayout();
		labelCaption = new Label(caption);
		horizontalLayout.addComponent(labelCaption);
		initializeAndAddMacAddressParts();
		setAlignmentCenter();
		setCompositionRoot(horizontalLayout);
	}

	private void initializeAndAddMacAddressParts() {
		macAddressParts = new ArrayList<TextField>();
		for (int i = 0; i < 6; i++) {
			TextField macAddressPartTextField = new TextField();
			macAddressPartTextField.setMaxLength(2);
			macAddressPartTextField.setWidth("40");
			macAddressPartTextField.addValidator(notNullValidator);
			macAddressPartTextField.setValidationVisible(true);
			macAddressPartTextField.setImmediate(true);
			macAddressParts.add(macAddressPartTextField);
			horizontalLayout.addComponent(macAddressPartTextField);
			if (i < 5) {
				horizontalLayout.addComponent(new Label(":"));
			}
		}
	}

	@Override
	public String getCaption() {
		return labelCaption.getValue();
	}

	@Override
	public void setCaption(String caption) {
		labelCaption.setValue(caption);
	}

	private void setAlignmentCenter() {
		for (Component component : horizontalLayout) {
			horizontalLayout.setComponentAlignment(component,
					Alignment.MIDDLE_CENTER);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public String getValue() {
		String macAddress = "";
		for (int i = 1; i < horizontalLayout.getComponentCount(); i++) {
			macAddress += ((Property<String>) horizontalLayout.getComponent(i))
					.getValue().toUpperCase();
		}
		return macAddress;
	}

	public boolean isValid() {
		boolean isValid = true;
		for (TextField textField : macAddressParts) {
			isValid = isValid && textField.isValid();
		}
		return isValid;
	}

	@Override
	public void setValue(String newValue)
			throws com.vaadin.data.Property.ReadOnlyException {

	}

	@Override
	public Class<? extends String> getType() {
		return String.class;
	}
}
