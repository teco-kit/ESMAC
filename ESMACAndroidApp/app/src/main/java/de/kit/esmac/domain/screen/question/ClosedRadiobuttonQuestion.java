package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import de.kit.esmac.R;

public class ClosedRadiobuttonQuestion extends ClosedQuestion {

    private RadioGroup radioGroup;

    @Override
    public void drawQuestionUI(LayoutInflater inflater, final RelativeLayout relativeLayout) {
        super.drawQuestionUI(inflater, relativeLayout);
        radioGroup = (RadioGroup) inflater.inflate(
                R.layout.radiogroup, null);
        Integer lastId = getLastId();
        for (String answer : getAnswerList()) {
            RadioButton radioButton = (RadioButton) inflater.inflate(
                    R.layout.radiobutton, null);
            radioButton.setText(answer);
            radioButton.setId(getId());
            increaseId();
            radioGroup.addView(radioButton);
            radioButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    updateSubmitButton(relativeLayout);
                }
            });
        }
        radioGroup.setId(getId());
        relativeLayout.addView(radioGroup, getLayoutParameter(lastId));
        increaseId();
    }

    protected RadioGroup getRadioGroup() {
        return radioGroup;
    }

    @Override
    protected void enableUi(boolean enabled) {
        super.enableUi(enabled);
        radioGroup.setEnabled(enabled);
    }

    @Override
    public String getAnswer() {
        if (radioGroup != null) {
            RadioButton selectedRadioButton = ((RadioButton) radioGroup
                    .findViewById(radioGroup.getCheckedRadioButtonId()));
            return selectedRadioButton.getText().toString();
        } else {
            return "";
        }

    }

    @Override
    public boolean isAnswered() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            return false;
        } else
            return true;
    }

    @Override
    protected boolean isEnabled() {
        return super.isEnabled() && radioGroup.isEnabled();
    }
}
