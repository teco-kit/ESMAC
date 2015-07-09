package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VisualAnalogQuestionConditional extends VisualAnalogQuestion {
    private Integer requiredValue;
    private AbstractQuestion conditionedQuestion;

    @Override
    public void drawQuestionUI(final LayoutInflater inflater, final RelativeLayout relativeLayout) {
        super.drawQuestionUI(inflater, relativeLayout);
        conditionedQuestion.drawQuestionUI(inflater, relativeLayout);
        conditionedQuestion.enableUi(false);
        SeekBar seekbar = getSeekBar();
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not Used

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not Used

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println(getRequiredValue() + " " + getValue());
                conditionedQuestion.enableUi((getRequiredValue() < getValue()));
                updateSubmitButton(relativeLayout);
            }
        });
    }

    public void setRequiredValue(Integer requiredValue) {
        this.requiredValue = requiredValue;
    }

    public void setConditionedQuestion(AbstractQuestion conditionedQuestion) {
        this.conditionedQuestion = conditionedQuestion;
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

    private int getRequiredValue() {
        int difference = maxBound - minBound;
        return Math.round(Math.round((difference * (new Double(requiredValue) / 100)) + minBound));

    }

}
