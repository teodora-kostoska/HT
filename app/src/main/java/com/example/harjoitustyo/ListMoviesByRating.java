package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Array;
import java.util.ArrayList;

public class ListMoviesByRating extends AppCompatActivity {
    private DataTransverClass data = null;
    private MovieManager manager = null;
    private User user = null;
    ListView list_movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movies_by_rating);
        list_movies = findViewById(R.id.list_movies);
        //Get data that was sent from Main menu
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        user = (User) getIntent().getSerializableExtra("user");
        System.out.println(data.getText());
        createList();
    }
    public void createList(){
        ArrayList<String> movies = manager.sortMoviesByRating();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, movies);
        list_movies.setAdapter(adapter);
    }
    @Override
    public void onBackPressed(){
        //On back press, if something was added to settings it doesn't get sent to main activity
        //Create intent
        Intent intent = new Intent();
        //Set something in data object to check it works
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