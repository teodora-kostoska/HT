package com.example.harjoitustyo;

import java.io.Serializable;

public class Movie implements Serializable {
    //Initialize values
    String movieName;
    String duration;
    String genre;
    String releaseYear;

    //constructor
    public Movie(String movieName, String duration, String genre, String releaseDate) {
        this.movieName = movieName;
        this.duration = duration;
        this.genre = genre;
        this.releaseYear = releaseDate;
    }

    //Methods to fetch info
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

}
