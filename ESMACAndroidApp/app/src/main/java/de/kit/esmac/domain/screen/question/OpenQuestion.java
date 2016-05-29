package de.kit.esmac.domain.screen.question;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;

import de.kit.esmac.R;

public class OpenQuestion extends AbstractQuestion {

    private EditText editText;
    private int inputType;

    @Override
    public void drawQuestionUI(LayoutInflater inflater, final RelativeLayout relativeLayout) {
        super.drawQuestionUI(inflater, relativeLayout);
        editText = (EditText) inflater.inflate(
                R.layout.edittext, null);
        editText.setTextSize(15);
        editText.setId(getId());
        editText.setInputType(inputType);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSubmitButton(relativeLayout);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not Used

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not Used

            }
        });
        relativeLayout.addView(editText, getLayoutParameter(getLastId()));
        increaseId();
    }

    protected EditText getEditText() {
        return editText;
    }

    @Override
    protected void enableUi(boolean enabled) {
        super.enableUi(enabled);
        editText.setEnabled(enabled);
    }

    @Override
    public boolean isAnswered() {
        return !editText.getText().toString().equals("");
    }

    @Override
    protected boolean isEnabled() {
        return (super.isEnabled() && editText.isEnabled());
    }

    @Override
    public String getAnswer() {
        if (editText != null) {
            return editText.getText().toString();
        } else {
            return "";
        }
    }

    public void setInputType(String inputType) {
        switch (inputType) {
            case "number":
                this.inputType = InputType.TYPE_CLASS_NUMBER;
                break;
            case "decimal":
                this.inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                break;
            case "string":
                this.inputType = InputType.TYPE_CLASS_TEXT;
                break;
            default:
                this.inputType = InputType.TYPE_CLASS_TEXT;
                break;
        }
    }

}
