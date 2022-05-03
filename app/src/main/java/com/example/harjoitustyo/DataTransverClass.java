package com.example.harjoitustyo;

import java.io.Serializable;

//Class used to send messages between activities, mainly used for testing
public class DataTransverClass implements Serializable {
    private String text = "";

    public void setText(String set){
        text = set;
    }
    public String getText(){
        return text;
    }
}
