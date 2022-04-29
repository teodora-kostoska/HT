package com.example.harjoitustyo;

import java.io.Serializable;

public class Reviews implements Serializable {

    String comment;
    int rating;

    public Reviews(String comment, int rating){
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
