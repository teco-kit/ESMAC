package de.kit.esmserver.uicomponents.sensor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

import de.kit.esmserver.uicomponents.basiccomponents.CheckBoxComponent;

public class ScreenActivityComponent extends AbstractSensorComponent {
	private static final long serialVersionUID = 4264027693719068676L;
	private CheckBoxComponent checkboxActivated;

	public ScreenActivityComponent() {
		buildMainLayout();
	}

	@Override
	protected Panel buildMainLayout() {
		GridLayout gridLayout = new GridLayout(7, 1);
		checkboxActivated = new CheckBoxComponent("Activated");
		gridLayout.addComponent(getLabelOperator());
		gridLayout.addComponent(getComboboxOperator());
		gridLayout.addComponent(checkboxActivated);
		setRestrictedOperatorComboBox();
		panel.setContent(gridLayout);
		panel.setCaption("ScreenActivity");
		return super.buildMainLayout();
	}

	@Override
	public boolean isValid() {
		return super.isValid();
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEmptyElement("screenactivity");
		writer.writeAttribute("operator", getOperator());
		writer.writeAttribute("value", checkboxActivated.getValue().toString());
	}

}
