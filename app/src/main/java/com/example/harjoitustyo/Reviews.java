package com.example.harjoitustyo;

import java.io.Serializable;

//All classes are serializable if they are to be transported from one activity to another
public class Reviews implements Serializable {
    //Initialize information
    private String time;
    private Movie movie;
    private String comment;
    private String rating;
    private User user;

    //Constructor for reviews, contains movie information, timestamp of review, rating, comment and user information
    public Reviews(Movie movie, String timestamp,String rating, String comment, User user){
        this.time = timestamp;
        this.movie = movie;
        this.comment = comment;
        this.rating = rating;
        this.user = user;
    }

    //Methods to get information from object
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
