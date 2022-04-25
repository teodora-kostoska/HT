package com.example.harjoitustyo;


public class MovieManager {
    User user;
    Entry entry;

    public MovieManager(Entry entry, User user){
        this.user = user;
        this.entry = entry;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Entry getEntry() {
        return entry;
    }

    public Entry getEntries() {
        return entry; //???
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }
/*
    public String getCurrentShows() {
        return ; //???
    }

    public String listMoviesByRating() {
        return ; //???
    }

    public String listUserMoviesByRating() {
        return ; //???
    }*/
}
