package com.example.harjoitustyo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Locale;
//This is connected to activity_settings layout

public class Settings extends AppCompatActivity {
    //Initialize views and data transfer object
    Button modify_info;
    Button sign_out;
    Button delete_account;

    EditText new_username;
    EditText new_password;
    private DataTransverClass data = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Find the corresponding views by id
        modify_info =findViewById(R.id.edit_user_info);
        sign_out = findViewById(R.id.sign_out);
        delete_account = findViewById(R.id.delete_account);
        new_username = findViewById(R.id.change_username);
        new_password = findViewById(R.id.change_password);




        Button changeLang = findViewById(R.id.changeMyLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });



        //Get data that was sent from Main menu
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        System.out.println(data.getText());
        //Set listener on account deletion button
        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Method to delete account
                deleteAccount();
            }
        });
        //Listener for sign out button
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To sign out method
                signOut();
            }
        });
        //to edit the username and password of user
        modify_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditing();
            }
        });
    }




    private void showChangeLanguageDialog() {
        final String[] listItems = {"Suomi", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocale("fi");
                    recreate();
                }
                if (i == 1) {
                    setLocale("en");
                    recreate();
                }

                dialogInterface.dismiss();

            }
        });

        AlertDialog mDialog = mBuilder.create();

        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }





    @Override
    //When arrow back of phone is pressed it goes back to the previous page which is Main menu,
    //Any edits in the Settings need to not be done, as the settings get set only when pressing the
    //TODO: save changes button
    public void onBackPressed(){
        //On back press, if something was added to settings it doesn't get sent to main activity
        //Create intent
        Intent intent = new Intent();
        //Set something in data object to check it works
        data.setText("Sending new text back from Settings!");
        intent.putExtra("object", data);
        //Set result code and intent
        setResult(RESULT_OK, intent);
        //Finish send the changes to previous activity
        finish();
    }
    //TODO: Method to delete account
    public void deleteAccount(){
        System.out.println("Deleted Account");
    }
    //TODO: Method to sign out
    public void signOut(){
        System.out.println("Signed out");
    }
    //TODO: Method to enable editing, this will reload the same settings page and make the edit fields editable and makes delete account button pressable
    public void enableEditing(){
        System.out.println("Editing on!");
    }
}