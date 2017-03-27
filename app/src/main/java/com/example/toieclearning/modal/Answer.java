package com.example.toieclearning.modal;

/**
 * Created by Admin on 3/27/2017.
 */

public class Answer {
    private int id;
    private boolean checked;
    private String content;
    private int question_id;

    public Answer(int id, boolean checked, String content, int question_id) {
        this.id = id;
        this.checked = checked;
        this.content = content;
        this.question_id = question_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }
}
