package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import de.kit.esmac.ui.QuestionnaireActivity;
import de.kit.esmac.R;

public abstract class AbstractQuestion {

    private static Integer ID = 1;
    private String name;
    private TextView nameTextView;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void drawQuestionUI(LayoutInflater inflater, final RelativeLayout relativeLayout) {
        nameTextView = (TextView) inflater.inflate(
                R.layout.textview, null);
        nameTextView.setText(name);
        nameTextView.setId(getId());
        LayoutParams layoutParams = getLayoutParameter(getLastId());
        relativeLayout.addView(nameTextView, layoutParams);
        increaseId();

    }

    protected LayoutParams getLayoutParameter(Integer id) {
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_END);
        layoutParams.addRule(RelativeLayout.ALIGN_START);
        layoutParams.addRule(RelativeLayout.BELOW, id);
        return layoutParams;
    }

    protected LayoutParams getLayoutParameterForLeftDescription(Integer id,
                                                                boolean left) {
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (left == true) {
            layoutParams.addRule(RelativeLayout.ALIGN_LEFT, id);
            layoutParams.addRule(RelativeLayout.BELOW, id);
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, id);
            layoutParams.addRule(RelativeLayout.BELOW, id);
        }
        return layoutParams;
    }

    protected void enableUi(boolean enabled) {
        nameTextView.setEnabled(enabled);
    }

    protected boolean isEnabled() {
        return nameTextView.isEnabled();
    }

    protected Integer getId() {
        return ID;
    }

    protected Integer getLastId() {
        return (ID - 1);
    }

    protected Integer increaseId() {
        return ID++;
    }

    public boolean isAnswered() {
        return true;
    }

    protected void updateSubmitButton(RelativeLayout relativeLayout) {
        RelativeLayout mainRelativeLayout = (RelativeLayout) relativeLayout.getParent().getParent()
                .getParent();
        QuestionnaireActivity main = (QuestionnaireActivity) mainRelativeLayout.getContext();
        Button nextButton = (Button) mainRelativeLayout.findViewById(R.id.next);
        main.enableButtons(nextButton);

    }

    public String getAnswer() {
        return "";
    }
}
