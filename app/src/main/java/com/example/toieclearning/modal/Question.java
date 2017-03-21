package com.example.toieclearning.modal;

/**
 * Created by MyPC on 3/13/2017.
 */

public class Question {
    private int id;
    private int question_type_id;
    private int package_id;
    private String content;

    public Question() {
    }

    public Question(int id, int question_type_id, int package_id, String content) {
        this.id = id;
        this.question_type_id = question_type_id;
        this.package_id = package_id;
        this.content = content;
    }

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

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
