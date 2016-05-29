package de.kit.esmserver.uicomponents.question.conditional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class MultiAnswerQuestionConditional extends AbstractConditionalQuestion {

	private static final long serialVersionUID = -7854797127814665183L;
	private HorizontalLayout mainLayout;
	private TextField textFieldName;
	private Label labelName;
	private Label labelAnswers;
	private List<TextField> answers;
	private List<CheckBox> checkBoxes;
	private String name;
	private NotNullValidator notNullValidator;

	public MultiAnswerQuestionConditional(String name) {
		this.name = name;
		answers = new ArrayList<TextField>();
		checkBoxes = new ArrayList<CheckBox>();
		notNullValidator = new NotNullValidator();
		buildMainLayout();
	}

	@Override
	public String getCaption() {
		return name;
	}

	@SuppressWarnings("serial")
	private Panel buildMainLayout() {
		mainLayout = new HorizontalLayout();

		labelName = new Label();
		labelName.setValue("Question");
		mainLayout.addComponent(labelName);

		textFieldName = new TextField();
		textFieldName.setImmediate(true);
		textFieldName.setValidationVisible(true);
		textFieldName.addValidator(notNullValidator);
		mainLayout.addComponent(textFieldName);

		labelAnswers = new Label("Answers");
		mainLayout.addComponent(labelAnswers);

		TextField firstAnswer = new TextField();
		firstAnswer.setImmediate(true);
		firstAnswer.setValidationVisible(true);
		firstAnswer.setWidth("100");
		firstAnswer.addValidator(notNullValidator);
		CheckBox firstAnswerCheckbox = new CheckBox();
		checkBoxes.add(firstAnswerCheckbox);
		addValueChangeListenerIfSelectOne(firstAnswerCheckbox);

		TextField secondAnswer = new TextField();
		secondAnswer.setImmediate(true);
		secondAnswer.setValidationVisible(true);
		secondAnswer.setWidth("100");
		secondAnswer.addValidator(notNullValidator);
		CheckBox secondAnswerCheckbox = new CheckBox();
		checkBoxes.add(secondAnswerCheckbox);
		addValueChangeListenerIfSelectOne(secondAnswerCheckbox);

		answers.add(firstAnswer);
		answers.add(secondAnswer);
		mainLayout.addComponent(firstAnswer);
		mainLayout.addComponent(firstAnswerCheckbox);
		mainLayout.addComponent(secondAnswer);
		mainLayout.addComponent(secondAnswerCheckbox);

		Button moreAnswer = new Button("+");
		moreAnswer.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				TextField answer = new TextField();
				answer.setImmediate(true);
				answer.setValidationVisible(true);
				answer.setWidth("100");
				answer.addValidator(notNullValidator);
				CheckBox checkBoxAnswer = new CheckBox();
				addValueChangeListenerIfSelectOne(checkBoxAnswer);
				answers.add(answer);
				checkBoxes.add(checkBoxAnswer);
				mainLayout.addComponent(answer,
						mainLayout.getComponentCount() - 2);
				mainLayout.addComponent(checkBoxAnswer,
						mainLayout.getComponentCount() - 2);
				setAlignmentMiddleCenter();

			}

		});
		Button lessAnswer = new Button("-");
		lessAnswer.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				if (mainLayout.getComponentCount() > 9) {
					TextField answer = (TextField) mainLayout
							.getComponent(mainLayout.getComponentCount() - 4);
					CheckBox checkBox = (CheckBox) mainLayout
							.getComponent(mainLayout.getComponentCount() - 3);
					answers.remove(answer);
					checkBoxes.remove(checkBox);
					mainLayout.removeComponent(answer);
					mainLayout.removeComponent(checkBox);
				}
			}
		});
		mainLayout.addComponent(lessAnswer);
		mainLayout.addComponent(moreAnswer);
		panel.setCaption(name);
		panel.setContent(mainLayout);

		condPanel = new Panel(mainLayout);
		condPanel.setCaption(name);
		verticalLayout.addComponent(condPanel);
		panel.setContent(verticalLayout);
		panel.setCaption("Conditional Question");
		return panel;
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("conditionalQuestion");
		if (name.equals("Select One Question")) {
			writer.writeStartElement("closedRadiobuttonQuestionConditional");
			writer.writeAttribute("requiredAnswer", getCheckedCheckbox());
			writer.writeAttribute("name", textFieldName.getValue());
			getXmlForAnswers(writer);
			writer.writeEndElement();
		} else {
			writer.writeStartElement("closedCheckboxQuestionConditional");
			writer.writeAttribute("name", textFieldName.getValue());
			getXmlForAnswers(writer);
			writer.writeEndElement();
		}
		conditionedQuestion.writeXML(writer);
		writer.writeEndElement();
	}

	private void getXmlForAnswers(XMLStreamWriter writer)
			throws XMLStreamException {
		if (name.equals("Select One Question"))
			for (TextField answer : answers) {
				writer.writeStartElement("answer");
				writer.writeCharacters(answer.getValue());
				writer.writeEndElement();
			}
		else {
			for (TextField answer : answers) {
				writer.writeStartElement("answer");
				writer.writeAttribute("required",
						checkBoxes.get(answers.indexOf(answer)).getValue()
								.toString());
				writer.writeCharacters(answer.getValue());
				writer.writeEndElement();
			}
		}
	}

	@Override
	public boolean isValid() {
		boolean isValid = textFieldName.isValid();
		for (TextField answer : answers) {
			isValid = isValid && answer.isValid();
		}
		if (getCheckedCheckbox().equals("")) {
			Notification.show("One answer must be required in " + name
					+ " Conditional : " + getName() + "!",
					Notification.Type.ERROR_MESSAGE);
			return false;
		}
		return isValid && conditionedQuestion.isValid()
				&& !checkAnswersForDuplicates();
	}

	@SuppressWarnings("serial")
	private ValueChangeListener getValueChangeListener() {
		return new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				CheckBox triggeredCheckBox = (CheckBox) event.getProperty();
				if (triggeredCheckBox.getValue()) {
					for (CheckBox checkBox : checkBoxes) {
						if (!triggeredCheckBox.equals(checkBox)) {
							checkBox.setValue(false);
						}
					}
				}

			}
		};
	}

	private void addValueChangeListenerIfSelectOne(CheckBox checkBoxAnswer) {
		if (name.equals("Select One Question")) {
			checkBoxAnswer.addValueChangeListener(getValueChangeListener());
		}
	}

	private String getCheckedCheckbox() {
		for (CheckBox component : checkBoxes) {
			if (component.getValue()) {
				return answers.get(checkBoxes.indexOf(component)).getValue();
			}
		}
		return "";
	}

	public boolean checkAnswersForDuplicates() {
		Set<String> answerStrings = new HashSet<String>();
		for (TextField answer : answers) {
			if (!answerStrings.add(answer.getValue())) {
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return textFieldName.getValue();
	}

}
