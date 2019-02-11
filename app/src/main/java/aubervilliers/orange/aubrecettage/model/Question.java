package aubervilliers.orange.aubrecettage.model;

import java.io.Serializable;

public class Question implements Serializable {

    private String questionLabel;
    private boolean buttonYesSelected;
    private boolean buttonNoSelected;
    private String commentary;
    private boolean isOpenQuestion;
    private boolean isObligatoryQuestion;

    public boolean isButtonYesSelected() {
        return buttonYesSelected;
    }

    public void setButtonYesSelected(boolean buttonYesSelected) {
        this.buttonYesSelected = buttonYesSelected;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getQuestionLabel() {
        return questionLabel;
    }

    public void setQuestionLabel(String questionLabel) {
        this.questionLabel = questionLabel;
    }

    public boolean isOpenQuestion() {
        return isOpenQuestion;
    }

    public void setOpenQuestion(boolean openQuestion) {
        isOpenQuestion = openQuestion;
    }

    public boolean isObligatoryQuestion() {
        return isObligatoryQuestion;
    }

    public void setObligatoryQuestion(boolean obligatoryQuestion) {
        isObligatoryQuestion = obligatoryQuestion;
    }

    public boolean isButtonNoSelected() {
        return buttonNoSelected;
    }

    public void setButtonNoSelected(boolean buttonNoSelected) {
        this.buttonNoSelected = buttonNoSelected;
    }
}
