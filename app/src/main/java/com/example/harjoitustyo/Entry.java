package com.example.harjoitustyo;

public class Entry {
    Movie movie;
    String date;

    public Entry(Movie movie, String date){
        this.movie = movie;
        this.date = date;
    }

    public Movie getMovie() {
        return movie;
    }

    public String getDate() {
        return date;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setDate(String date) {
        this.date = date;
    }
}