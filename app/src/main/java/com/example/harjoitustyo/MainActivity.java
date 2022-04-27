package com.example.harjoitustyo;
//This is the backend for the activity_main, which is the login page

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity{
    //Import views
    Button sign_in_button;
    Button register_button;
    private TextView authStatusTv;
    private Button authBtn;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    EditText username;
    EditText password;
    //Create object in order to be able to send data from one view to another
    private DataTransverClass transfer = new DataTransverClass();

    // private Spinner spinnerCurrentMovies; elokuvaSpinner
    //private Spinner spinnerRatings; ratingSpinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));


        //Biometric authenticator
        //Initiate UI views
        authStatusTv = findViewById(R.id.authStatusTv);
        authBtn = findViewById(R.id.authBtn);

        //Initiate Biometric
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //Stop tasks that requires authentication
                authStatusTv.setText("Authentication error " + ":" + errString);
                Toast.makeText(MainActivity.this, "Authentication error" + ": " + errString, Toast.LENGTH_SHORT).show();
             }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //Continue tasks that require authentication
                authStatusTv.setText("Authentication succeed!");
                Toast.makeText(MainActivity.this, "Authentication succeed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //Stop tasks that requires authentication
                authStatusTv.setText("Authentication failed!");
                Toast.makeText(MainActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
            }
        });


        //Setuping the description on authorising dialog and titles
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Login using fingerprint authentication")
                .setNegativeButtonText("User App Password")
                .build();


        //handle authBtn click, start authentication
        authBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });



        //Change language
        Button changeLang = findViewById(R.id.changeMyLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });




        /*
        //elokuvaSpinner jutut alkaa
        spinnerCurrentMovies = findViewById(R.id.spinnerShowCurrentMovies);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.movies, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrentMovies.setAdapter(adapter1);

        spinnerCurrentMovies.setOnItemSelectedListener(this);


        //elokuvaSpinner jutut loppuu toistaiseksi


        //ratingSpinner jutut alkaa
        spinnerCurrentMovies = findViewById(R.id.spinnerRating);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.ratings, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrentMovies.setAdapter(adapter2);

        spinnerCurrentMovies.setOnItemSelectedListener(this);


        //ratingSpinner jutut loppuu toistaiseksi*/


        //Initialize all buttons and edit text
        sign_in_button = findViewById(R.id.signIn_Button);
        register_button = findViewById(R.id.register_Button);
        username = findViewById(R.id.usernameID);
        password = findViewById(R.id.PasswordID);
        //Set listener, so that when button is pressed it goes to Main Menu
        //TODO: Needs separate method to check password and username, before sending to main menu
        sign_in_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                loadMainMenu();
            }
        });
        //Set listener, so that when button pressed goes to Register Page
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRegister();
            }
        });
    }





    //Change language
    private void showChangeLanguageDialog() {
        final String[] listItems = {"Suomi", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
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






    public void loadMainMenu(){
        //Send object to second activity and wait for result from activity
        //TODO: Should be probably edited so that it doesn't wait for response from activity, as this is only the log in page
        //Set intent, which is current activity and to which activity we switch
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        //Checking functionality of data transfer object
        transfer.setText("Sending some random text from Main Activity!");
        //Put object in Extra
        intent.putExtra("object", transfer);
        //Start Main menu
        startActivityForResult(intent, 1);
    }
    //Same as above but for the Register Page
    public void loadRegister(){
        Intent intent = new Intent(MainActivity.this, RegisterPage.class);
        transfer.setText("Sending some random text from Main Activity!");
        intent.putExtra("object", transfer);
        startActivityForResult(intent, 4);
    }
    @Override
    //On results from Main Menu or Register collect the data that was sent
    //TODO: Maybe no need for the result from Main menu
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //From Main menu as request code is 1 for Main menu
        if (requestCode == 1) {
            //Check whether the returned code is what was expected
            if (resultCode == RESULT_OK) {
                //Collect the data transfer object
                transfer = (DataTransverClass) data.getSerializableExtra("object");
                System.out.println(transfer.getText());
            }
            //For register page, as request code was 4 for register page
        }else if(requestCode == 4){
            //Check result code
            if (resultCode == RESULT_OK) {
                //Collect data from object
                transfer = (DataTransverClass) data.getSerializableExtra("object");
                System.out.println(transfer.getText());
            }
        }
    }


    /*Spinneriä on noi loput

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getApplicationContext(), choice, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }*/
}