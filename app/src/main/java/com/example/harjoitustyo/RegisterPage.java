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
        //TODO: Is there need for data to be sent from Login page
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        System.out.println(data.getText());
        //Context of whole app
        context = getApplicationContext();
        //Set on click listener so that when register is pressed it goes back to login page
        register.setOnClickListener(view -> {
            try {
                //Create user object and add user data to XML
                int userExist = manager.addUserToXML(username.getText().toString(), name.getText().toString(), password.getText().toString(), email.getText().toString(), context);
                if(userExist == 0){
                    //Send object to second activity and wait for result from activity
                    Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                    //Check that data transfer object works
                    data.setText("Sending some random text from Register page via register button");
                    //TODO: Send user object to Login
                    intent.putExtra("object", data);
                    intent.putExtra("manager", manager);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
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
        //On back press, if something was added to settings it doesn't get sent to main activity
        Intent intent = new Intent();
        data.setText("Sending new text back from Register page!");
        intent.putExtra("object", data);
        intent.putExtra("manager", manager);
        setResult(RESULT_OK, intent);
        finish();
    }
    //TODO: When register button is pressed also need for the user data to be saved to db and the user object to be created

}