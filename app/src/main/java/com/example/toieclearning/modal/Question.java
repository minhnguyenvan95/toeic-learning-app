package com.example.toieclearning.modal;

/**
 * Created by MyPC on 3/13/2017.
 */

public class Question {
    private int id;
    private int question_type_id;
    private int id_doanvan;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestion_type_id() {
        return question_type_id;
    }

    public void setQuestion_type_id(int question_type_id) {
        this.question_type_id = question_type_id;
    }

    public int getId_doanvan() {
        return id_doanvan;
    }

    public void setId_doanvan(int id_doanvan) {
        this.id_doanvan = id_doanvan;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Question(int id, int question_type_id, int id_doanvan, String content) {
        this.id = id;
        this.question_type_id = question_type_id;
        this.id_doanvan = id_doanvan;
        this.content = content;
    }

    public Question() {
    }
}
