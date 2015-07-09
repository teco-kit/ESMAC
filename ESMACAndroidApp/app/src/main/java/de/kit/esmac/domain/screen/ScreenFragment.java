package de.kit.esmac.domain.screen;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import de.kit.esmac.R;
import de.kit.esmac.domain.screen.question.AbstractQuestion;

public class ScreenFragment extends Fragment {

    private List<AbstractQuestion> questions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout item = (RelativeLayout) inflater
                .inflate(R.layout.relative, container, false);
        for (AbstractQuestion question : questions) {
            question.drawQuestionUI(inflater, item);
        }
        return item;
    }

    public List<AbstractQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AbstractQuestion> questions) {
        this.questions = questions;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TODO: Vorerst so implementiert, da bei einem wiederherstellen die Anwendung sonst crashed --> entweder Parceable in Fragen implementieren oder XML festablegen
//        super.onSaveInstanceState(outState);
    }

    public boolean allQuestionsAnswered() {
        boolean allAnswered = true;
        for (AbstractQuestion question : questions) {
            allAnswered = allAnswered && question.isAnswered();
        }
        return allAnswered;
    }

}
