package com.ttp.learning_web.learningPlatform.dto;

import com.ttp.learning_web.learningPlatform.enums.ChoiceLetter;

public class QuizChoiceDTO {
    private ChoiceLetter choiceLetter;
    private String content;

    public QuizChoiceDTO() {}

    public QuizChoiceDTO(ChoiceLetter choiceLetter, String content) {
        this.choiceLetter = choiceLetter;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChoiceLetter getChoiceLetter() {
        return choiceLetter;
    }

    public void setChoiceLetter(ChoiceLetter choiceLetter) {
        this.choiceLetter = choiceLetter;
    }
}
