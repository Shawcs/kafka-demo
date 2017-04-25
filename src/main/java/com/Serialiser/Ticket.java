package com.Serialiser;

/**
 * Created by aturbillon on 19/04/2017.
 */

import java.io.Serializable;


public class Ticket implements Serializable {

    private String content;
    private String type;
    private int number;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", number=" + number +
                '}';
    }
}