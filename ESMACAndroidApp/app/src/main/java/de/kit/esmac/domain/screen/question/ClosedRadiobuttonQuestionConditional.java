package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class ClosedRadiobuttonQuestionConditional extends ClosedRadiobuttonQuestion {

    private AbstractQuestion conditionedQuestion;
    private String requiredAnswer;

    public void setConditionedQuestion(AbstractQuestion conditionedQuestion) {
        this.conditionedQuestion = conditionedQuestion;
    }

    public void setRequiredAnswer(String requiredAnswer) {
        this.requiredAnswer = requiredAnswer;
    }

    @Override
    public void drawQuestionUI(LayoutInflater inflater, RelativeLayout relativeLayout) {
        super.drawQuestionUI(inflater, relativeLayout);
        conditionedQuestion.drawQuestionUI(inflater, relativeLayout);
        conditionedQuestion.enableUi(false);
        getRadioGroup().setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = ((RadioButton) group
                        .findViewById(checkedId));
                conditionedQuestion.enableUi(selectedRadioButton.getText().toString()
                        .equals(requiredAnswer));

            }
        });
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
