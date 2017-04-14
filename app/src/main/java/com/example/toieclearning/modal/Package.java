package com.example.toieclearning.modal;

import java.util.ArrayList;

/**
 * Created by Administrator on 4/14/2017.
 */

public class Package {
    private String content;
    ArrayList<Question> questions;

    public Package() {
    }

    public Package(String content, ArrayList<Question> questions) {
        this.content = content;
        this.questions = questions;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
