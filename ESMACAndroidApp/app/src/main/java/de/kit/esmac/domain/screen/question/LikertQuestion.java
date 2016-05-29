package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import de.kit.esmac.R;

public class LikertQuestion extends AbstractQuestion {

    private Integer minBound, maxBound;

    private String minDescription, maxDescription;

    private RadioGroup radioGroup;

    public String getMinDescription() {
        return minDescription;
    }

    public void setMinDescription(String minDiscription) {
        this.minDescription = minDiscription;
    }

    public String getMaxDescription() {
        return maxDescription;
    }

    public void setMaxDescription(String maxDiscription) {
        this.maxDescription = maxDiscription;
    }

    public Integer getMinBound() {
        return minBound;
    }

    public void setMinBound(Integer minBound) {
        this.minBound = minBound;
    }

    public Integer getMaxBound() {
        return maxBound;
    }

    public void setMaxBound(Integer maxBound) {
        this.maxBound = maxBound;
    }

    @Override
    public void drawQuestionUI(LayoutInflater inflater, final RelativeLayout relativeLayout) {
        super.drawQuestionUI(inflater, relativeLayout);
        radioGroup = (RadioGroup) inflater.inflate(
                R.layout.radiogroup, null);
        int id = getLastId();
        for (int i = getMinBound(); i <= getMaxBound(); i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(
                    R.layout.radiobutton, null);
            radioButton.setId(getId());
            increaseId();
            if (i == getMinBound()) {
                radioButton.setText(getMinDescription());
            } else if (i == getMaxBound()) {
                radioButton.setText(getMaxDescription());
            }
            radioGroup.addView(radioButton);
            radioButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    updateSubmitButton(relativeLayout);
                }
            });
        }
        radioGroup.setId(getId());

        relativeLayout.addView(radioGroup,
                getLayoutParameter(id));
        increaseId();

    }

    protected RadioGroup getRadioGroup() {
        return radioGroup;
    }

    @Override
    protected void enableUi(boolean enabled) {
        super.enableUi(enabled);
        radioGroup.setEnabled(enabled);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(enabled);
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

    @Override
    public String getAnswer() {
        if (radioGroup != null) {
            RadioButton selectedRadioButton = ((RadioButton) radioGroup
                    .findViewById(radioGroup.getCheckedRadioButtonId()));
            Integer radioButtonValue = radioGroup.indexOfChild(selectedRadioButton) + getMinBound();
            return radioButtonValue.toString();
        } else {
            return "";
        }

    }

}
