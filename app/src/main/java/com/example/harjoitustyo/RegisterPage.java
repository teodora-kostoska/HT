package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
//This is connected to activity_register_page layout

public class RegisterPage extends AppCompatActivity {
    //Initialize views
    Button register;
    EditText name;
    EditText email;
    EditText password;
    EditText username;
    TextView showUser;
    //Initialize values to collect the objects that were sent from other activities
    private DataTransverClass data = null;
    private MovieManager manager;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        //Find correct views by id
        register = findViewById(R.id.button_register);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email_id);
        password = findViewById(R.id.password_id);
        username = findViewById(R.id.username);
        showUser = findViewById(R.id.showUser);

        //Collect data that was sent from Login page
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        System.out.println(data.getText());

        //Context of whole app to fetch the data that is in files
        context = getApplicationContext();

        //Set on click listener so that when register is pressed it goes back to login page if user creation was successful
        register.setOnClickListener(view -> {
            try {
                //Create user object and add user data to XML
                int userExist = manager.addUserToXML(username.getText().toString(), name.getText().toString(), password.getText().toString(), email.getText().toString(), context);
                // if userExist is 0 that means that there isn't another user that has the same username, so the user was created successfully
                if(userExist == 0){
                    //Set intent to go to login page
                    Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                    //Set extras so that the data gained in this activity is transported to the login page
                    data.setText("Sending some random text from Register page via register button");
                    intent.putExtra("object", data);
                    intent.putExtra("manager", manager);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    //If userExist gets other value that means that username is taken and the user should choose a different one
                    showUser.setText("Username is taken, try again!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    //When back arrow is pressed goes back to login page, without saving any changes done to register page
    public void onBackPressed(){
        //On back press, go to previous activity and send objects from this activity to the other one
        Intent intent = new Intent();
        data.setText("Sending new text back from Register page!");
        intent.putExtra("object", data);
        intent.putExtra("manager", manager);
        setResult(RESULT_OK, intent);
        finish();
    }
}