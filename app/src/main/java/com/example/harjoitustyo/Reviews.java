package com.example.harjoitustyo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Reviews implements Serializable {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    String time;
    Movie movie;
    String comment;
    int rating;

    public Reviews(Movie movie, String comment, int rating){
        LocalDateTime now = LocalDateTime.now();
        this.time = dtf.format(now);
        this.movie = movie;
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }
}
