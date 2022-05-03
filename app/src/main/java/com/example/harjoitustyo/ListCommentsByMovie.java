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
        ArrayAdapter<String> movie_list;
        movie_list = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, manager.getMovieNames());
        movie_list.setDropDownViewResource(android.R.layout.simple_spinner_item);
        comment_spinner.setAdapter(movie_list);
        comment_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                counter = position;
                System.out.println(manager.getEntries().get(counter).getMovie().getMovieName());
                setGrid(manager.getEntries().get(counter).getMovie().getMovieName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });
    }

    public void setGrid(String movie_name){
        ArrayList<Reviews> reviews = manager.getReviewsByMovieName(movie_name);
        CommentAdapter adapter = new CommentAdapter(this, reviews);
        comment_view.setAdapter(adapter);
    }

    public void onBackPressed(){
        //On back press, if something was added to settings it doesn't get sent to main activity
        //Create intent
        Intent intent = new Intent();
        //Set something in data object to check it works
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