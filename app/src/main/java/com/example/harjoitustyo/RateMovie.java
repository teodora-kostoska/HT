package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
//Connected to rate_movie layout

public class RateMovie extends AppCompatActivity {
    //Initialized views and data transfer object
    Button rate;
    Spinner movie_name;
    Spinner movie_rating;
    EditText movie_comment;
    String rating_value;
    Movie movie;
    private DataTransverClass data = null;
    private MovieManager manager = null;
    private User user = null;
    int counter;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movie);
        //Find all the views in activity by id
        rate = findViewById(R.id.rate_button);
        movie_name = findViewById(R.id.movieList);
        movie_rating = findViewById(R.id.rating);
        movie_comment = findViewById(R.id.comment);
        context = getApplicationContext();
        //Get any data that was sent via intent from Main Menu
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        user = (User) getIntent().getSerializableExtra("user");
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
        ArrayList<String> rating_list = new ArrayList<>();
        for(int i = 0; i<6;i++){
            rating_list.add(String.valueOf(i));
        }
        ArrayAdapter<String> rating_values;
        rating_values = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rating_list);
        rating_values.setDropDownViewResource(android.R.layout.simple_spinner_item);
        movie_rating.setAdapter(rating_values);
        ArrayAdapter<String> movie_list;
        movie_list = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, manager.getMovieNames());
        movie_list.setDropDownViewResource(android.R.layout.simple_spinner_item);
        movie_name.setAdapter(movie_list);
        movie_rating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rating_value = rating_list.get(i);
                System.out.println(rating_list.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        movie_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                counter = position;
                movie = manager.getEntries().get(counter).getMovie();
                System.out.println(manager.getEntries().get(counter).getMovie().getMovieName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });
    }
    //Method in order to send comments to correct places
    public void sendRating(){
        try {
            manager.setReviewToXML(context, movie,rating_value, movie_comment.getText().toString());
            System.out.println("Rating sent!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //When arrow back is pressed goes back to previous page without saving anything that was written
    public void onBackPressed(){
        //On back press, if something was added to settings it doesn't get sent to main activity
        Intent intent = new Intent();
        //Check that data transfer object works
        data.setText("Sending new text back from Rate Movie!");
        intent.putExtra("manager", manager);
        intent.putExtra("user", user);
        intent.putExtra("object", data);
        setResult(RESULT_OK, intent);
        finish();
    }
}