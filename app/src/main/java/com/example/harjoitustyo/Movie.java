package com.example.harjoitustyo;

import java.io.Serializable;

public class Movie implements Serializable {
    String movieName;
    String duration;
    String genre;
    String releaseYear;

    public Movie(String movieName, String duration, String genre, String releaseDate) {
        this.movieName = movieName;
        this.duration = duration;
        this.genre = genre;
        this.releaseYear = releaseDate;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getDuration() {
        return duration;
    }

    public String getGenre() {
        return genre;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setReleaseYear(String releaseDate) {
        this.releaseYear = releaseDate;
    }

}
