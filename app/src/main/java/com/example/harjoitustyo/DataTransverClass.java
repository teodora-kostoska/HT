package com.example.harjoitustyo;

import java.io.Serializable;

public class DataTransverClass implements Serializable {
    private String text = "";

    public void setText(String set){
        text = set;
    }
    public String getText(){
        return text;
    }
}
