package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
//Connected to rate_movie layout

public class RateMovie extends AppCompatActivity {
    //Initialized views and data transfer object
    Button rate;
    Spinner movie_name;
    EditText movie_rating;
    EditText movie_comment;
    private DataTransverClass data = null;
    private MovieManager manager = null;
    int counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movie);
        //Find all the views in activity by id
        rate = findViewById(R.id.rate_button);
        movie_name = findViewById(R.id.movieList);
        movie_rating = findViewById(R.id.rating);
        movie_comment = findViewById(R.id.comment);
        //Get any data that was sent via intent from Main Menu
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        System.out.println(data.getText());
        setSpinner();
        //Set on click listener so that when the button is pressed:
        //TODO: rating is saved to db/json/xml
        //TODO: new object for keeping track of comments
        //TODO: set spinner so that current movies can be selected from spinner and rated
        //TODO: need new class which will keep track of all movies that were in Finnkino, even if not rated
        rate.setOnClickListener(view -> sendRating());
    }
    //Method to fill spinner with data on movies
    public void setSpinner(){
        ArrayAdapter<String> movie_list;
        System.out.println(manager.getMovieNames().get(1));
        movie_list = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, manager.getMovieNames());
        movie_list.setDropDownViewResource(android.R.layout.simple_spinner_item);
        movie_name.setAdapter(movie_list);
        movie_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                counter = position;
                System.out.println(manager.getEntries().get(counter).getMovie().getMovieName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });
    }
    //Method in order to send comments to correct places
    public void sendRating(){
        System.out.println("Rating sent!");
    }
    //When arrow back is pressed goes back to previous page without saving anything that was written
    public void onBackPressed(){
        //On back press, if something was added to settings it doesn't get sent to main activity
        Intent intent = new Intent();
        //Check that data transfer object works
        data.setText("Sending new text back from Rate Movie!");
        intent.putExtra("object", data);
        setResult(RESULT_OK, intent);
        finish();
    }
}