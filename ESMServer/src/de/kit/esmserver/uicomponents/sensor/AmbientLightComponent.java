package de.kit.esmserver.uicomponents.sensor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.DoubleValidator;

public class AmbientLightComponent extends AbstractSensorComponent {

	private static final long serialVersionUID = 7446256196915300938L;
	private TextField ambientLightValue;

	public AmbientLightComponent() {
		buildMainLayout();
	}

	@Override
	protected Panel buildMainLayout() {
		GridLayout gridLayout = new GridLayout(7, 1);
		Label ambientLightLabel = new Label("Lumen ");
		ambientLightValue = new TextField();
		ambientLightValue.setImmediate(true);
		ambientLightValue.setValidationVisible(true);
		ambientLightValue.addValidator(new DoubleValidator(false, false));
		gridLayout.addComponent(getLabelOperator());
		gridLayout.addComponent(getComboboxOperator());
		gridLayout.addComponent(ambientLightLabel);
		gridLayout.addComponent(ambientLightValue);
		setFullOperatorComboBox();
		panel.setContent(gridLayout);
		panel.setCaption("AmbientLight");
		return super.buildMainLayout();
	}

	@Override
	public boolean isValid() {
		return super.isValid() && ambientLightValue.isValid();
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEmptyElement("ambientlight");
		writer.writeAttribute("operator", getOperator());
		writer.writeAttribute("value", ambientLightValue.getValue());
	}

}
