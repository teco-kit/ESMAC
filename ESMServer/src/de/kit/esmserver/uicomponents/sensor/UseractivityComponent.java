package de.kit.esmserver.uicomponents.sensor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

import de.kit.esmserver.uicomponents.basiccomponents.CheckBoxComponent;

public class UseractivityComponent extends AbstractSensorComponent {
	private static final long serialVersionUID = 4495103947703955498L;
	private List<CheckBoxComponent> checkBoxList;

	public UseractivityComponent() {
		checkBoxList = new ArrayList<CheckBoxComponent>();
		buildMainLayout();

	}

	@Override
	protected Panel buildMainLayout() {
		GridLayout gridLayout = new GridLayout(13, 1);
		gridLayout.addComponent(getLabelOperator());
		gridLayout.addComponent(getComboboxOperator());
		checkBoxList.add(new CheckBoxComponent("Still"));
		checkBoxList.add(new CheckBoxComponent("On_Foot"));
		checkBoxList.add(new CheckBoxComponent("On_Bicycle"));
		checkBoxList.add(new CheckBoxComponent("In_Vehicle"));
		checkBoxList.add(new CheckBoxComponent("Running"));
		checkBoxList.add(new CheckBoxComponent("Tilting"));
		checkBoxList.add(new CheckBoxComponent("Unknown"));
		for (CheckBoxComponent checkBox : checkBoxList) {
			gridLayout.addComponent(checkBox);
		}
		setRestrictedOperatorComboBox();
		panel.setContent(gridLayout);
		panel.setCaption("UserActivity");
		return super.buildMainLayout();
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("useractivity");
		writer.writeAttribute("operator", getOperator());
		for (CheckBoxComponent checkBox : checkBoxList) {
			if (checkBox.getValue()) {
				writer.writeStartElement("activity");
				writer.writeCharacters(checkBox.getCaption().toUpperCase());
				writer.writeEndElement();
			}
		}
		writer.writeEndElement();
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
}
