
package de.kit.esmac.domain.screen.question;

import java.util.ArrayList;
import java.util.List;

public abstract class ClosedQuestion extends AbstractQuestion {
    private List<String> answerList;

    public ClosedQuestion() {
        answerList = new ArrayList<String>();
    }

    public void addAnswerToAnswerList(String answer) {
        answerList.add(answer);
    }

    public List<String> getAnswerList() {
        return answerList;
    }

}
