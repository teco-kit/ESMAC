package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class LikertQuestionConditional extends LikertQuestion {

    private AbstractQuestion conditionedQuestion;
    private Integer requiredBound;

    public void setConditionedQuestion(AbstractQuestion conditionedQuestion) {
        this.conditionedQuestion = conditionedQuestion;
    }

    public void setRequiredBound(Integer requiredBound) {
        this.requiredBound = requiredBound;
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
                Integer radioButtonValue = group.indexOfChild(selectedRadioButton) + getMinBound();
                conditionedQuestion.enableUi((requiredBound <= radioButtonValue));

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
