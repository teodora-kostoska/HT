package com.example.harjoitustyo;

import java.io.Serializable;

//All the objects are going to be serializable, as they need to be transported from one activity to other
public class User implements Serializable {
    //Initialize values
    String name;
    String email;
    String username;
    String password;
    
    //User constructor
    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    //Methods to fetch or set the information in the object
    public String getEmail() {
        return email;
    }

    public String getName(){
        return name;
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setFirstName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
