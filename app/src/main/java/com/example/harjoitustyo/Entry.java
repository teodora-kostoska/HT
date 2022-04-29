package com.example.harjoitustyo;

import java.io.Serializable;

public class Entry implements Serializable {
    Movie movie;

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