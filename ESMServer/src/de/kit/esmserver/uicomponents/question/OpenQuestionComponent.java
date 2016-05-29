package de.kit.esmserver.uicomponents.question;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class OpenQuestionComponent extends AbstractQuestionComponent {

	private static final long serialVersionUID = 7256985948787406937L;
	private HorizontalLayout mainLayout;
	private TextField textfieldValue;
	private Label labelName;
	private Label labelInputType;
	private ComboBox comboBoxInputType;

	public OpenQuestionComponent() {
		buildMainLayout();
	}

	private Panel buildMainLayout() {
		mainLayout = new HorizontalLayout();
		NotNullValidator validator = new NotNullValidator();
		labelName = new Label();
		labelName.setValue("Question");
		mainLayout.addComponent(labelName);
		textfieldValue = new TextField();
		textfieldValue.setImmediate(true);
		textfieldValue.setValidationVisible(true);
		textfieldValue.addValidator(validator);
		mainLayout.addComponent(textfieldValue);
		labelInputType = new Label("Input Type");
		comboBoxInputType = new ComboBox();
		comboBoxInputType.addValidator(validator);
		comboBoxInputType.addItem("number");
		comboBoxInputType.addItem("decimal");
		comboBoxInputType.addItem("string");
		mainLayout.addComponent(labelInputType);
		mainLayout.addComponent(comboBoxInputType);
		panel.setCaption("Open Question");
		panel.setContent(mainLayout);
		return panel;
	}

	@Override
	public boolean isValid() {
		return textfieldValue.isValid();
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEmptyElement("openQuestion");
		writer.writeAttribute("name", textfieldValue.getValue());
		writer.writeAttribute("inputType", comboBoxInputType.getValue()
				.toString());
	}
}
