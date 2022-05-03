package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class ListMoviesByRating extends AppCompatActivity {
    //Initialize objects and views
    private DataTransverClass data = null;
    private MovieManager manager = null;
    private User user = null;
    GridView list_movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movies_by_rating);
        //fetch view and collect data from intent into correct objects
        list_movies = findViewById(R.id.list_movies);
        //Get data that was sent from Main menu
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        user = (User) getIntent().getSerializableExtra("user");
        System.out.println(data.getText());
        //populate the list
        populateGrid();
    }
    public void populateGrid(){
        //Get the sorted movies array
        ArrayList<Movie> movies = manager.sortMoviesByRating();
        //Use movie adapter to create the adapter
        MovieAdapter adapter = new MovieAdapter(this, movies);
        //populate the grid
        list_movies.setAdapter(adapter);
    }
    @Override
    public void onBackPressed(){
        //On back press, if something was added to settings it doesn't get sent to main activity
        //Create intent
        Intent intent = new Intent();
        data.setText("Sending new text back from Movie Listing!");
        intent.putExtra("object", data);
        intent.putExtra("user", user);
        intent.putExtra("manager",manager);
        //Set result code and intent
        setResult(RESULT_OK, intent);
        //Finish send the changes to previous activity
        finish();
    }
}