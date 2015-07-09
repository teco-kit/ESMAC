package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class ClosedCheckboxQuestionConditional extends ClosedCheckboxQuestion {
    private AbstractQuestion conditionedQuestion;
    private List<Boolean> requiredAnswers;

    public ClosedCheckboxQuestionConditional() {
        super();
        requiredAnswers = new ArrayList<Boolean>();
    }

    public void setConditionedQuestion(AbstractQuestion conditionedQuestion) {
        this.conditionedQuestion = conditionedQuestion;
    }

    public void addRequiredAnswer(boolean requiredAnswer) {
        requiredAnswers.add(requiredAnswer);
    }

    @Override
    public void drawQuestionUI(LayoutInflater inflater, RelativeLayout relativeLayout) {
        super.drawQuestionUI(inflater, relativeLayout);
        conditionedQuestion.drawQuestionUI(inflater, relativeLayout);
        conditionedQuestion.enableUi(false);
        for (CheckBox checkBox : getCheckboxes()) {
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean condition = true;
                    for (int i = 0; i < requiredAnswers.size(); i++) {
                        condition = condition && (getCheckboxes().get(i).isChecked()
                                == requiredAnswers.get(i));
                    }
                    conditionedQuestion.enableUi(condition);

                }
            });
        }
    }

    @Override
    public boolean isAnswered() {
        if (!super.isAnswered()) {
            return false;
        } else if (conditionedQuestion.isEnabled()
                && !conditionedQuestion.isAnswered()) {
            return false;
        }
        return true;
    }

    @Override
    public String getAnswer() {
        return super.getAnswer() + ";" + conditionedQuestion.getAnswer();
    }

    @Override
    public String getName() {
        return super.getName() + ";" + conditionedQuestion.getName();
    }
}
