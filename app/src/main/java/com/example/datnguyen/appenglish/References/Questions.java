package com.example.datnguyen.appenglish.References;

public class Questions {
    private int id;
    private String question;
    private String answer;

    public Questions(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
