package com.example.harjoitustyo;

public class Movie {
    String movieName;
    String producer;
    String lead;
    String releaseDate;

    public Movie(String movieName, String producer, String lead, String releaseDate) {
        this.movieName = movieName;
        this.producer = producer;
        this.lead = lead;
        this.releaseDate = releaseDate;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getProducer() {
        return producer;
    }

    public String getLead() {
        return lead;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}
