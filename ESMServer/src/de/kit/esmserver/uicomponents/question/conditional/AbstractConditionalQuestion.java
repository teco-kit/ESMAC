package de.kit.esmserver.uicomponents.question.conditional;

import java.util.Iterator;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.kit.esmserver.uicomponents.question.AbstractQuestionComponent;
import de.kit.esmserver.uicomponents.question.LikertQuestionComponent;
import de.kit.esmserver.uicomponents.question.MultiAnswerQuestionComponent;
import de.kit.esmserver.uicomponents.question.OpenQuestionComponent;
import de.kit.esmserver.uicomponents.question.SliderQuestionComponent;

public abstract class AbstractConditionalQuestion extends
		AbstractQuestionComponent {
	private static final long serialVersionUID = 191638950960324203L;
	protected AbstractQuestionComponent conditionedQuestion;
	protected VerticalLayout verticalLayout;
	protected Panel condPanel;

	public AbstractConditionalQuestion() {
		verticalLayout = new VerticalLayout();
	}

	public void setConditionedQuestion(String questionString) {
		switch (questionString) {
		case "Open Question":
			conditionedQuestion = new OpenQuestionComponent();
			break;
		case "Select One Question":
			conditionedQuestion = new MultiAnswerQuestionComponent(
					MultiAnswerQuestionComponent.SELECT_ONE_QUESTION);
			break;
		case "Select Many Question":
			conditionedQuestion = new MultiAnswerQuestionComponent(
					MultiAnswerQuestionComponent.SELECT_MANY_QUESTION);
			break;
		case "Likert Question":
			conditionedQuestion = new LikertQuestionComponent();
			break;
		case "Slider Question":
			conditionedQuestion = new SliderQuestionComponent();
			break;

		default:
			break;
		}
		drawConditionedQuestion();
	}

	private void drawConditionedQuestion() {
		verticalLayout.addComponent(conditionedQuestion);
		setAlignmentMiddleCenter();
	}

	@Override
	public void setAlignmentMiddleCenter() {
		if (condPanel.getContent() instanceof Layout.AlignmentHandler) {
			Layout layout = (Layout) condPanel.getContent();
			for (Iterator<Component> iterator = layout.iterator(); iterator
					.hasNext();) {
				Component component = (Component) iterator.next();
				Layout.AlignmentHandler alignmentHandler = (Layout.AlignmentHandler) layout;
				alignmentHandler.setComponentAlignment(component,
						Alignment.MIDDLE_CENTER);
			}
		}
		conditionedQuestion.setAlignmentMiddleCenter();
	}
}
