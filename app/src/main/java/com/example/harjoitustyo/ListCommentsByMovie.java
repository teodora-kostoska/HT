package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;


public class ListCommentsByMovie extends AppCompatActivity {
    //Get views and initialize objects
    Spinner comment_spinner;
    GridView comment_view;
    int counter;
    private DataTransverClass data = null;
    private MovieManager manager = null;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_comments_by_movie);

        //Get data that was sent from Main menu
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        user = (User) getIntent().getSerializableExtra("user");
        comment_spinner = findViewById(R.id.comment_movieName);
        comment_view = findViewById(R.id.comment_Grid);
        System.out.println(data.getText());
        setSpinner();
    }

    public void setSpinner(){
        //create array of movies
        ArrayAdapter<String> movie_list;
        //create adapter
        movie_list = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, manager.getMovieNames());
        movie_list.setDropDownViewResource(android.R.layout.simple_spinner_item);
        //Populate adapter
        comment_spinner.setAdapter(movie_list);
        comment_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                counter = position;
                //set the grid with the movie name that was selected from spinner
                setGrid(manager.getEntries().get(counter).getMovie().getMovieName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });
    }

    public void setGrid(String movie_name){
        //Get the reviews where the movie name is the one selected in spinner
        ArrayList<Reviews> reviews = manager.getReviewsByMovieName(movie_name);
        //Create adapter
        CommentAdapter adapter = new CommentAdapter(this, reviews);
        //Populate grid
        comment_view.setAdapter(adapter);
    }

    public void onBackPressed(){
        //On back press go to main menu
        //Create intent
        Intent intent = new Intent();
        //Set extras
        data.setText("Sending new text back from Settings!");
        intent.putExtra("object", data);
        intent.putExtra("user", user);
        intent.putExtra("manager",manager);
        //Set result code and intent
        setResult(RESULT_OK, intent);
        //Finish send the changes to previous activity
        finish();
    }
}