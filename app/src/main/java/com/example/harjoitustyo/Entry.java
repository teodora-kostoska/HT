package com.example.harjoitustyo;

import java.io.Serializable;

//Class is used for keeping track of all movies
public class Entry implements Serializable {
    Movie movie;

    //Methods to get and set information as well as constructor
    public Entry(Movie movie){
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}