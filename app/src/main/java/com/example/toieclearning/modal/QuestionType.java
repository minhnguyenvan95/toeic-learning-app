package com.example.toieclearning.modal;

/**
 * Created by MyPC on 3/12/2017.
 */

public class QuestionType {
    private int id;
    private String name;
    private String meta;

    public QuestionType(int id, String name, String meta) {
        this.id = id;
        this.name = name;
        this.meta = meta;
    }

    public QuestionType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

}
