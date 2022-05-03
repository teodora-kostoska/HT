package com.example.harjoitustyo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;
//This is connected to activity_settings layout

public class Settings extends AppCompatActivity {
    //Initialize views and data transfer object
    Button modify_info;
    Button sign_out;
    EditText new_username;
    EditText new_password;
    EditText new_name;
    EditText new_email;
    TextView to_user;
    private DataTransverClass data = null;
    private MovieManager manager = null;
    private User user = null;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Load language in shared preferences
        loadLocale();

        //Find the corresponding views by id
        modify_info =findViewById(R.id.edit_user_info);
        sign_out = findViewById(R.id.sign_out);
        new_username = findViewById(R.id.change_username);
        new_password = findViewById(R.id.change_password);
        new_name = findViewById(R.id.change_name);
        new_email = findViewById(R.id.change_email);
        //Get application context to be able to access files
        context = getApplicationContext();
        to_user = findViewById(R.id.textView6);

        //Get data that was sent from Main menu
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        manager = (MovieManager) getIntent().getSerializableExtra("manager");
        user = (User) getIntent().getSerializableExtra("user");
        System.out.println(data.getText());

        //Set the information views with the current users info, so that they can change only the values they want
        new_username.setText(user.getUsername());
        new_password.setText(user.getPassword());
        new_name.setText(user.getName());
        new_email.setText(user.getEmail());

        //Listener for sign out button
        sign_out.setOnClickListener(view -> {
            //Set current user to null -> user logged out
            user = null;
            //Set intent to go to Main Activity (Log in page)
            Intent intent = new Intent(Settings.this, MainActivity.class);

            //Set extra to intent so that they are transported to login page
            data.setText("Sending new text back from Settings!");
            intent.putExtra("object", data);
            intent.putExtra("user", user);
            intent.putExtra("manager",manager);
            //Start the activity -> go to log in page
            startActivity(intent);
        });

        //to edit the user information
        modify_info.setOnClickListener(view -> enableEditing());

        //Change the language in the activity on press
        Button changeLang = findViewById(R.id.changeMyLanguage);
        changeLang.setOnClickListener(view -> {
            //Show list of languages, one can be selected
            showChangeLanguageDialog();
        });
    }

    //Method to change the user information in movie manager
    public void enableEditing(){
        try {
            int existance = manager.editUserInformation(user,new_username.getText().toString(), new_password.getText().toString(), new_name.getText().toString(), new_email.getText().toString(), context);
            //If existance >0 then there is some other user that already has that username and the change isn't done
            if(existance > 0){
                to_user.setText("Username is already taken, try again!");
            }else{
                //Otherwise the change was successful
                to_user.setText("Modifications done successfully!");
                //Update the current user to have the edited information
                user = manager.getCurrentUser(new_username.getText().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showChangeLanguageDialog() {
        //Array of different languages
        final String[] listItems = {"Suomi", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //Finnish
                    setLocale("fi");
                    recreate();
                }
                if (i == 1) {
                    //English
                    setLocale("en");
                    recreate();
                }
                //Dismiss dialog once language selected
                dialogInterface.dismiss();

            }
        });

        AlertDialog mDialog = mBuilder.create();
        //Show alert dialog
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //Save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    //Load a language in shared preferences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    @Override
    //When arrow back of phone is pressed it goes back to the previous page which is Main menu,
    //Any edits in the Settings need to not be done, as the settings get set only when pressing the
    //edit user info button
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