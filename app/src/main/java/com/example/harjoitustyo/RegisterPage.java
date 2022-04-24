package com.example.harjoitustyo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
//This is connected to activity_register_page layout

public class RegisterPage extends AppCompatActivity {
    //Initialize views
    Button register;
    EditText name;
    EditText email;
    EditText password;
    EditText username;
    private DataTransverClass data = null;
    User user = null;
    private Context context;
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
        //Collect data that was sent from Login page
        //TODO: Is there need for data to be sent from Login page
        data =(DataTransverClass) getIntent().getSerializableExtra("object");
        System.out.println(data.getText());
        //Context of whole app
        context = getApplicationContext();
        //Create user object and write to XML
        user = new User(name.getText().toString(), email.getText().toString(), username.getText().toString(), password.getText().toString());
        //Set on click listener so that when register is pressed it goes back to login page
        register.setOnClickListener(view -> {
            writeToXML();
            //Send object to second activity and wait for result from activity
            Intent intent = new Intent(RegisterPage.this, MainActivity.class);
            //Check that data transfer object works
            data.setText("Sending some random text from Register page via register button");
            //TODO: Send user object to Login
            intent.putExtra("object", data);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    public void writeToXML() {
        //Get user info
        String username = user.getUsername();
        String name = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();
        //Creates new document
        //TODO: seperate for when document already exists and only need to append
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("userList");
            doc.appendChild(rootElement);

            Element userInfo = doc.createElement("userInfo");
            rootElement.appendChild(userInfo);

            Attr id = doc.createAttribute("user_id");
            id.setValue(Integer.toString(1));
            userInfo.setAttributeNode(id);

            Element elm = doc.createElement("name");
            elm.setTextContent(name);
            userInfo.appendChild(elm);

            Element elm3 = doc.createElement("username");
            elm3.setTextContent(username);
            userInfo.appendChild(elm3);

            Element elm4 = doc.createElement("email");
            elm4.setTextContent(email);
            userInfo.appendChild(elm4);

            Element elm5 = doc.createElement("password");
            elm5.setTextContent(password);
            userInfo.appendChild(elm5);

            DOMSource source = new DOMSource(doc);
            OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("userXML.xml", Context.MODE_PRIVATE));
            result.write(String.valueOf(source));

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } finally{
            System.out.println("##################File Written successfully##########################");
        }
    }
    @Override
    //When back arrow is pressed goes back to login page, without saving any changes done to register page
    public void onBackPressed(){
        //On back press, if something was added to settings it doesn't get sent to main activity
        Intent intent = new Intent();
        data.setText("Sending new text back from Register page!");
        intent.putExtra("object", data);
        setResult(RESULT_OK, intent);
        finish();
    }
    //TODO: When register button is pressed also need for the user data to be saved to db and the user object to be created

}