package com.example.toieclearning.modal;

import java.util.HashMap;

/**
 * Created by MyPC on 3/13/2017.
 */

public class Question {
    private int id;
    private int question_type_id;
    private int package_id;
    private String content;
    private HashMap<Integer, Answer> answers;

    public Question() {
    }

    public Question(int id, int question_type_id, int package_id, String content, HashMap<Integer, Answer> answers) {
        this.id = id;
        this.question_type_id = question_type_id;
        this.package_id = package_id;
        this.content = content;
        this.answers = answers;
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

    public HashMap<Integer, Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<Integer, Answer> answers) {
        this.answers = answers;
    }
}
