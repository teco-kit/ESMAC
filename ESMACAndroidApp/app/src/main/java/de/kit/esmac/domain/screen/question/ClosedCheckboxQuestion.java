package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import de.kit.esmac.R;

public class ClosedCheckboxQuestion extends ClosedQuestion {

    private List<CheckBox> checkboxes;

    public ClosedCheckboxQuestion() {
        checkboxes = new ArrayList<CheckBox>();
    }

    @Override
    public void drawQuestionUI(LayoutInflater inflater, final RelativeLayout relativeLayout) {
        super.drawQuestionUI(inflater, relativeLayout);
        for (String answer : getAnswerList()) {
            CheckBox checkBox = (CheckBox) inflater.inflate(
                    R.layout.checkbox, null);
            checkBox.setText(answer);
            checkBox.setId(getId());
            checkBox.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    updateSubmitButton(relativeLayout);
                }
            });
            checkboxes.add(checkBox);
            relativeLayout.addView(checkBox, getLayoutParameter(getLastId()));
            increaseId();
        }
    }

    protected List<CheckBox> getCheckboxes() {
        return checkboxes;
    }

    @Override
    protected void enableUi(boolean enabled) {
        super.enableUi(enabled);
        for (CheckBox checkBox : checkboxes) {
            checkBox.setEnabled(enabled);
        }
    }

    @Override
    public boolean isAnswered() {
        for (CheckBox checkBox : checkboxes) {
            if (checkBox.isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isEnabled() {
        boolean enabled = super.isEnabled();
        for (CheckBox checkBox : checkboxes) {
            enabled = enabled && checkBox.isEnabled();
        }

        return enabled;
    }

    @Override
    public String getAnswer() {
        String answerString = "[";
        for (CheckBox checkBox : checkboxes) {
            if (checkBox.isChecked()) {
                answerString += checkBox.getText() + ",";
            }
        }
        if (answerString.length() > 1) {
            return answerString.substring(0, answerString.length() - 1) + "]";
        } else {
            return answerString + "]";
        }
    }
}
