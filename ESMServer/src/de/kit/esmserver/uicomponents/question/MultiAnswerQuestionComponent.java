package de.kit.esmserver.uicomponents.question;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class MultiAnswerQuestionComponent extends AbstractQuestionComponent {

	private static final long serialVersionUID = -151405304780639524L;
	public static final String SELECT_ONE_QUESTION = "Select One Question";
	public static final String SELECT_MANY_QUESTION = "Select Many Question";
	private HorizontalLayout mainLayout;
	private TextField textFieldName;
	private Label labelName;
	private Label labelAnswers;
	private List<TextField> answers;
	private String name;
	private NotNullValidator notNullValidator;

	public MultiAnswerQuestionComponent(String name) {
		this.name = name;
		notNullValidator = new NotNullValidator();
		answers = new ArrayList<TextField>();
		buildMainLayout();
	}

	public String getName() {
		return textFieldName.getValue();
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

		TextField secondAnswer = new TextField();
		secondAnswer.setImmediate(true);
		secondAnswer.setValidationVisible(true);
		secondAnswer.setWidth("100");
		secondAnswer.addValidator(notNullValidator);

		answers.add(firstAnswer);
		answers.add(secondAnswer);
		mainLayout.addComponent(firstAnswer);
		mainLayout.addComponent(secondAnswer);

		Button moreAnswer = new Button("+");
		moreAnswer.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				TextField answer = new TextField();
				answer.setImmediate(true);
				answer.setValidationVisible(true);
				answer.setWidth("100");
				answer.addValidator(notNullValidator);
				answers.add(answer);
				mainLayout.addComponent(answer,
						mainLayout.getComponentCount() - 2);

			}
		});
		Button lessAnswer = new Button("-");
		lessAnswer.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				if (mainLayout.getComponentCount() > 7) {
					TextField answer = (TextField) mainLayout
							.getComponent(mainLayout.getComponentCount() - 3);
					answers.remove(answer);
					mainLayout.removeComponent(answer);
				}
			}
		});
		mainLayout.addComponent(lessAnswer);
		mainLayout.addComponent(moreAnswer);
		panel.setCaption(name);
		panel.setContent(mainLayout);

		return panel;
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		if (panel.getCaption().equals(SELECT_ONE_QUESTION)) {
			writer.writeStartElement("closedRadiobuttonQuestion");
			writer.writeAttribute("name", textFieldName.getValue());
			getXmlForAnswers(writer);
			writer.writeEndElement();
		} else {
			writer.writeStartElement("closedCheckboxQuestion");
			writer.writeAttribute("name", textFieldName.getValue());
			getXmlForAnswers(writer);
			writer.writeEndElement();
		}
	}

	private void getXmlForAnswers(XMLStreamWriter writer)
			throws XMLStreamException {
		for (TextField answer : answers) {
			writer.writeStartElement("answer");
			writer.writeCharacters(answer.getValue());
			writer.writeEndElement();
		}
	}

	@Override
	public boolean isValid() {
		boolean isValid = textFieldName.isValid();
		for (TextField answer : answers) {
			isValid = isValid && answer.isValid();
		}
		return isValid && !checkAnswersForDuplicates();
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
}
