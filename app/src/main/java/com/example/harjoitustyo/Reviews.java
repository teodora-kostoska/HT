package com.example.harjoitustyo;

import java.io.Serializable;

public class Reviews implements Serializable {
    String time;
    Movie movie;
    String comment;
    String rating;
    User user;

    public Reviews(Movie movie, String timestamp,String rating, String comment, User user){
        this.time = timestamp;
        this.movie = movie;
        this.comment = comment;
        this.rating = rating;
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public String getRating() {
        return rating;
    }

    public Movie getMovie(){ return movie;}

    public String getTimeStamp() {return time;}

    public User getUser() {return user;}
}
