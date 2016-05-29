package de.kit.esmserver.uicomponents.sensor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

import de.kit.esmserver.uicomponents.basiccomponents.CheckBoxComponent;

public class TelephonComponent extends AbstractSensorComponent {
	private static final long serialVersionUID = -4015133974620412826L;
	private List<CheckBoxComponent> checkBoxList;

	public TelephonComponent() {
		checkBoxList = new ArrayList<CheckBoxComponent>();
		buildMainLayout();
	}

	@Override
	protected Panel buildMainLayout() {
		GridLayout gridLayout = new GridLayout(13, 1);
		gridLayout.addComponent(getLabelOperator());
		gridLayout.addComponent(getComboboxOperator());
		checkBoxList.add(new CheckBoxComponent("Idle"));
		checkBoxList.add(new CheckBoxComponent("Ringing"));
		checkBoxList.add(new CheckBoxComponent("OffHook"));
		for (CheckBoxComponent checkBox : checkBoxList) {
			gridLayout.addComponent(checkBox);
		}
		setRestrictedOperatorComboBox();
		panel.setContent(gridLayout);
		panel.setCaption("Telephon");
		return super.buildMainLayout();
	}

	@Override
	public boolean isValid() {
		for (CheckBoxComponent checkBox : checkBoxList) {
			if (checkBox.getValue()) {
				return super.isValid();
			}
		}
		return false;
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("telephone");
		writer.writeAttribute("operator", getOperator());
		for (CheckBoxComponent checkBox : checkBoxList) {
			if (checkBox.getValue()) {
				writer.writeStartElement("state");
				writer.writeCharacters("CALL_STATE_"
						+ checkBox.getCaption().toUpperCase());
				writer.writeEndElement();
			}
		}
		writer.writeEndElement();
	}
}
