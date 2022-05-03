package com.example.harjoitustyo;
//This is the backend for the activity_main, which is the login page

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import android.content.Context;
import java.io.File;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
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
import android.os.StrictMode;
import android.widget.TextView;
import java.util.Locale;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
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
    Context context;
    TextView loginInfo;
    //Create object in order to be able to send data from one view to another
    private DataTransverClass transfer = new DataTransverClass();
    private MovieManager manager = null;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        loadLocale();
        manager = MovieManager.getInstance();
        /*
        File directory = getFilesDir();
        File file2 = new File(directory, "MovieXML.txt");
        file2.delete();

        File file3 = new File(directory, "ReviewsXML.txt");
        file3.delete();
        File file = new File(directory, "UserXML.txt");
        file.delete();
         */

        //Initialize all buttons and edit text
        sign_in_button = findViewById(R.id.signIn_Button);
        register_button = findViewById(R.id.register_Button);
        username = findViewById(R.id.usernameID);
        password = findViewById(R.id.PasswordID);
        loginInfo = findViewById(R.id.loginInfo);
        context = getApplicationContext();
        //Set listener, so that when button is pressed it goes to Main Menu
        sign_in_button.setOnClickListener(view -> {
            int userExistance = manager.getUserFromXML(username.getText().toString(), password.getText().toString(),context);
            if(userExistance == 1){
                manager.GetMovieInfo(context);
                user = manager.getCurrentUser(username.getText().toString());
                loadMainMenu();
            }else{
                loginInfo.setText("Login credentials wrong, try again!");
            }
        });
        //Set listener, so that when button pressed goes to Register Page
        register_button.setOnClickListener(view -> loadRegister());

        //change actionbar title, if this isn't done it will be on systems default language
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
                authStatusTv.setText(getApplicationContext().getResources().getString(R.string.authenticationError) + " " + errString);
                Toast.makeText(MainActivity.this, getApplicationContext().getResources().getString(R.string.authenticationError) + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //Continue tasks that require authentication
                authStatusTv.setText(getApplicationContext().getResources().getString(R.string.authenticationSucceed));
                Toast.makeText(MainActivity.this, getApplicationContext().getResources().getString(R.string.authenticationSucceed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //Stop tasks that requires authentication
                authStatusTv.setText(getApplicationContext().getResources().getString(R.string.authenticationFailed));
                Toast.makeText(MainActivity.this, getApplicationContext().getResources().getString(R.string.authenticationFailed), Toast.LENGTH_SHORT).show();
            }
        });


        //Setuping the description on authorising dialog and titles
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getApplicationContext().getResources().getString(R.string.biometricAuthentication))
                .setSubtitle(getApplicationContext().getResources().getString(R.string.loginUsingFingerprintAuthentication))
                .setNegativeButtonText(getApplicationContext().getResources().getString(R.string.userAppPassword))
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
                //Show list of languages, one can be selected
                showChangeLanguageDialog();
            }
        });
    }


    //Change language
    private void showChangeLanguageDialog() {
        //Array of different languages
        final String[] listItems = {"Suomi", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(getApplicationContext().getResources().getString(R.string.chooseLanguage));
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


    public void loadMainMenu(){
        //Send object to second activity and wait for result from activity
        //TODO: Should be probably edited so that it doesn't wait for response from activity, as this is only the log in page
        //Set intent, which is current activity and to which activity we switch
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        //Checking functionality of data transfer object
        transfer.setText("Sending some random text from Main Activity!");
        //Put object in Extra
        //TODO: send user object
        intent.putExtra("object", transfer);
        intent.putExtra("user", user);
        intent.putExtra("manager", manager);
        //Start Main menu
        startActivityForResult(intent, 1);
    }
    //Same as above but for the Register Page
    public void loadRegister(){
        Intent intent = new Intent(MainActivity.this, RegisterPage.class);
        transfer.setText("Sending some random text from Main Activity!");
        intent = intent.putExtra("object", transfer);
        intent = intent.putExtra("manager", manager);
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
                user = (User) data.getSerializableExtra("user");
                manager = (MovieManager) data.getSerializableExtra("manager");
                System.out.println(transfer.getText());
            }
            //For register page, as request code was 4 for register page
        }else if(requestCode == 4){
            //Check result code
            if (resultCode == RESULT_OK) {
                //Collect data from object
                transfer = (DataTransverClass) data.getSerializableExtra("object");
                manager = (MovieManager) data.getSerializableExtra("manager");
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