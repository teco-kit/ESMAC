package de.kit.esmserver.uicomponents.sensor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.DoubleRangeValidator;
import de.kit.esmserver.uicomponents.validators.DoubleValidator;
import de.kit.esmserver.uicomponents.validators.StringValidator;

public class GeofenceComponent extends AbstractSensorComponent {
	private static final long serialVersionUID = -2388375759760927842L;
	private TextField longitudeTextField;
	private TextField latitudeTextField;
	private TextField radiusTextField;
	private TextField nameTextField;
	private DoubleRangeValidator latitudeValidator;
	private DoubleRangeValidator longitudeValidator;
	private DoubleValidator doubleValidator;

	public GeofenceComponent() {
		latitudeValidator = new DoubleRangeValidator(90, -90,
				"Latitude must between 90 and -90", false, true);
		longitudeValidator = new DoubleRangeValidator(180, -180,
				"Longitude must between 180 and -180", false, true);
		doubleValidator = new DoubleValidator(false, false);
		buildMainLayout();
	}

	@Override
	protected Panel buildMainLayout() {
		GridLayout gridLayout = new GridLayout(13, 1);
		Label longitudeLabel = new Label("Long. ");
		Label latitudeLabel = new Label("Lat. ");
		Label radiusLabel = new Label("Rad. in m. ");
		Label nameLabel = new Label("Name ");

		longitudeTextField = new TextField();
		longitudeTextField.setWidth("80");
		longitudeTextField.setImmediate(true);
		longitudeTextField.setValidationVisible(true);
		longitudeTextField.addValidator(longitudeValidator);

		latitudeTextField = new TextField();
		latitudeTextField.setWidth("80");
		latitudeTextField.setImmediate(true);
		latitudeTextField.setValidationVisible(true);
		latitudeTextField.addValidator(latitudeValidator);

		radiusTextField = new TextField();
		radiusTextField.setWidth("80");
		radiusTextField.setImmediate(true);
		radiusTextField.setValidationVisible(true);
		radiusTextField.addValidator(doubleValidator);

		nameTextField = new TextField();
		nameTextField.setWidth("80");
		nameTextField.setImmediate(true);
		nameTextField.setValidationVisible(true);
		nameTextField.addValidator(new StringValidator());

		gridLayout.addComponent(getLabelOperator());
		gridLayout.addComponent(getComboboxOperator());
		gridLayout.addComponent(nameLabel);
		gridLayout.addComponent(nameTextField);
		gridLayout.addComponent(latitudeLabel);
		gridLayout.addComponent(latitudeTextField);
		gridLayout.addComponent(longitudeLabel);
		gridLayout.addComponent(longitudeTextField);
		gridLayout.addComponent(radiusLabel);
		gridLayout.addComponent(radiusTextField);
		setRestrictedOperatorComboBox();
		panel.setContent(gridLayout);
		panel.setCaption("Geofence");
		return super.buildMainLayout();
	}

	@Override
	public boolean isValid() {
		return super.isValid() && latitudeTextField.isValid()
				&& longitudeTextField.isValid() && radiusTextField.isValid()
				&& nameTextField.isValid();
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEmptyElement("geofence");
		writer.writeAttribute("name", nameTextField.getValue());
		writer.writeAttribute("operator", getOperator());
		writer.writeAttribute("longitude", longitudeTextField.getValue());
		writer.writeAttribute("latitude", latitudeTextField.getValue());
		writer.writeAttribute("radius", radiusTextField.getValue());
	}
}
