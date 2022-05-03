package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
//Connected to activity_main_menu layout

public class MainMenu extends AppCompatActivity {
    //Initialize views and data object
    Button settings;
    Button list_movie;
    Button rate_movie;
    Button comments_button;
    private DataTransverClass data = null;
    private User user = null;
    private MovieManager manager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        //Find correct views by id from layout
        settings = findViewById(R.id.settings_button);
        list_movie = findViewById(R.id.list_movies_button);
        rate_movie = findViewById(R.id.rate_movie_button);
        comments_button = findViewById(R.id.movie_comments_button);
        //Get any data that was sent from login i.e user information
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        user = (User) getIntent().getSerializableExtra("user");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        System.out.println(data.getText());
        //Set on click listener for settings button, so that when settings button is pressed the settings activity is launched
        settings.setOnClickListener(view -> {
            //Send object to second activity and wait for result from activity
            //Set intent which contains information on the current activity and the target activity
            Intent intent = new Intent(MainMenu.this, Settings.class);
            //Check data transfer object workings
            data.setText("Sending some random text from Main menu!");
            intent.putExtra("object", data);
            intent.putExtra("manager", manager);
            intent.putExtra("user", user);
            startActivityForResult(intent, 2);
        });
        list_movie.setOnClickListener(view -> {
            //Send object to second activity and wait for result from activity
            //Set intent which contains information on the current activity and the target activity
            Intent intent = new Intent(MainMenu.this, ListMoviesByRating.class);
            //Check data transfer object workings
            data.setText("Sending some random text from Main menu!");
            intent.putExtra("object", data);
            intent.putExtra("manager", manager);
            intent.putExtra("user", user);
            startActivityForResult(intent, 2);
        });
        //set on click listener for when rate movie button is pressed
        rate_movie.setOnClickListener(view -> {
            //Send object to second activity and wait for result from activity
            Intent intent = new Intent(MainMenu.this, RateMovie.class);
            data.setText("Sending some random text from Main Menu!");
            intent.putExtra("object", data);
            intent.putExtra("manager", manager);
            intent.putExtra("user",user);
            startActivityForResult(intent, 2);
        });
        comments_button.setOnClickListener(view -> {
            //Send object to second activity and wait for result from activity
            //Set intent which contains information on the current activity and the target activity
            Intent intent = new Intent(MainMenu.this, ListCommentsByMovie.class);
            //Check data transfer object workings
            data.setText("Sending some random text from Main menu!");
            intent.putExtra("object", data);
            intent.putExtra("manager", manager);
            intent.putExtra("user", user);
            startActivityForResult(intent, 2);
        });

    }

    @Override
    //When back arrow pressed go back to login page, TODO: but don't sign user out!
    //Any changes done to data object don't need to be sent to login page
    public void onBackPressed(){
        //On back press, if something was added to settings it doesn't get sent to main activity
        Intent intent = new Intent();
        data.setText("Sending new text back from Main Menu!");
        intent.putExtra("object", data);
        intent.putExtra("user", user);
        intent.putExtra("manager", manager);
        setResult(RESULT_OK, intent);
        finish();
    }
    //As we have a few activities that wait for results from opened activity, we need a method to handle the results from
    //second activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check that request code is what was sent
        if (requestCode == 2) {
            //Check that result code is what was expected
            if (resultCode == RESULT_OK) {
                //Get the result
                this.data = (DataTransverClass) data.getSerializableExtra("object");
                this.manager = (MovieManager) data.getSerializableExtra("manager");
                this.user = (User) data.getSerializableExtra("user");
                System.out.println(this.data.getText());
            }
        }
    }
}