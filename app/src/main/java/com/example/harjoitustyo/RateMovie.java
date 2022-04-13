package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//Connected to rate_movie layout

public class RateMovie extends AppCompatActivity {
    //Initialized views and data transfer object
    Button rate;
    EditText movie_name;
    EditText movie_rating;
    EditText movie_comment;
    private DataTransverClass data = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movie);
        //Find all the views in activity by id
        rate = findViewById(R.id.rate_button);
        movie_name = findViewById(R.id.movie_name);
        movie_rating = findViewById(R.id.rating);
        movie_comment = findViewById(R.id.comment);
        //Get any data that was sent via intent from Main Menu
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        System.out.println(data.getText());
        //Set on click listener so that when the button is pressed:
        //TODO: rating is saved to db/json/xml
        //TODO: new object for keeping track of comments
        //TODO: set spinner so that current movies can be selected from spinner and rated
        //TODO: need new class which will keep track of all movies that were in Finnkino, even if not rated
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRating();
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