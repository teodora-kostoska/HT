package com.example.harjoitustyo;


import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.text.SimpleDateFormat;
import java.util.Date;

//The movie manager class is in charge of every object, in here all of the savings to file are created,
//fetching from file, loading from Finnkino and so on
public class MovieManager implements Serializable {
    //Initialize values, manager is going to be a singleton class
    private static MovieManager manager = null;
    ArrayList <User> users; //List of users
    ArrayList <Entry> entries; //List of all movies in finnkino
    ArrayList <Entry> all_entries; //Unique movie list (only one instance of each movie)
    ArrayList <Reviews> reviews; //List of all reviews
    int add_movies_to_xml; //Makes sure that file information is loaded to objects only once

    //When movie manager constructor is called all of the initialized arrays are created and information from Finnkino is fetched
    private MovieManager() {
        users = new ArrayList<>();
        entries = new ArrayList<>();
        all_entries = new ArrayList<>();
        reviews = new ArrayList<>();
        getMoviesFromFinnkino();
        add_movies_to_xml = 0;
    }

    //Singleton method, if manager is null create new manager, otherwise nothing
    public static MovieManager getInstance()
    {
        if (manager == null)
            manager = new MovieManager();
        return manager;
    }

    //Load infromation from files to objects, this enables to keep track of user activities
    public void GetMovieInfo(Context context_app){
        //If the files have not yet been loaded to objects then do that
        if(add_movies_to_xml == 0) {
            //User information to users Array list
            addUsersFromXMLToObject(context_app);
            //Movie information to all_movies Array List
            getMoviesFromXML(context_app);
            //Review information to reviews Array List
            getReviewsFromXML(context_app);
            try {
                //Add movies that are not yet in MoviesXML file to the file, by comparing to current shows in Finnkino
                addMoviesToXML(context_app);
                //Once this is done, there is no need to do these again, until the application is opened again
                add_movies_to_xml = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //When user is created add it to the users Array list and the UsersXML file
    public int addUserToXML(String username, String name, String password, String email, Context context) throws IOException {
        String toFile;
        //Check whether username is taken
        int userExist = getUserFromXML(username,password, context);
        //If username is taken exit method
        if(userExist == 1 || userExist ==2){
            return userExist;
        }
        //add user object to users Array
        users.add(new User(name, email, username, password));
        //Creates new document
        File file = new File(context.getFilesDir(), "UserXML.txt");
        //If the file exists already go through the information in the file and add the new user in the end
        if(file.exists()){
            //Keep track of content of the UserXML file
            ArrayList<String> list_of_content = new ArrayList<>();
            InputStream in = context.openFileInput("UserXML.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String output;
            //Read all information from UserXML file to the list
            while((output = r.readLine()) != null){
                list_of_content.add(output + "\n");
            }
            //This value shows the index in the array where the /userList value is, before this is where the new user needs to be added
            int i = 0;
            //This value contains the number of users in the file and is used to give id:s to users
            int y = 0;
            while(i< list_of_content.size()){
                if(list_of_content.get(i).equals("</userList>")) {
                    break;
                }
                if(list_of_content.get(i).contains("</userInfo>")){
                    y = y+1;
                }
                i++;
            }
            //The user information that needs to be added to the file
            toFile = "<userInfo user_id='"+ y +"'>\n" +
                    "<name>" + name + "</name>\n" +
                    "<username>" + username + "</username>\n" +
                    "<email>" + email + "</email>\n" +
                    "<password>" + password +"</password>\n" +
                    "</userInfo>\n";
            //Add the new user before the /userList tag
            list_of_content.add((i-1), toFile);
            //Open the file
            OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("UserXML.txt", Context.MODE_PRIVATE));
            i = 0;
            //Overwrite everything in the file with the content of the list_of_content
            while(i < list_of_content.size()){
                result.write(list_of_content.get(i));
                i++;
            }
            result.close();
        }
        //If file doesn't already exist it needs to be created
        else {
            //Information that needs to be added to file
            toFile = "<?xml version='1.0' encoding='UTF-8'?>\n"+"<userList>\n" +"<userInfo user_id='0'>\n" +
                    "<name>" + name + "</name>\n" +
                    "<username>" + username + "</username>\n" +
                    "<email>" + email + "</email>\n" +
                    "<password>" + password +"</password>\n" +
                    "</userInfo>\n"+
                    "</userList>\n";
            //Open file
            OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("UserXML.txt", Context.MODE_PRIVATE));
            //Write to file
            result.write(toFile);
            result.close();
            System.out.println("File created!");
        }
        return userExist;
    }

    //Load user information from File to users object upon start of app
    public void addUsersFromXMLToObject(Context context){
        //Clear the users array
        users.clear();
        File file = new File(context.getFilesDir(), "UserXML.txt");
        //If the file exists already, if it doesn't then there is nothing to load
        if(file.exists()) {
            try {
                //Open file
                InputStream ins = context.openFileInput("UserXML.txt");
                DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                //Parse file
                Document xmlDoc = docBuild.parse(ins);
                Element elem = xmlDoc.getDocumentElement();
                elem.normalize();
                //Get list of users
                NodeList list_of_users = elem.getElementsByTagName("userInfo");
                //Go through list of user nodes
                for (int i = 0; i < list_of_users.getLength(); i++) {
                    //Go through nodes one by one
                    Node node = list_of_users.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //Get the user information
                        Element element2 = (Element) node;
                        NodeList nodeName = element2.getElementsByTagName("name").item(0).getChildNodes();
                        NodeList nodeUsername = element2.getElementsByTagName("username").item(0).getChildNodes();
                        NodeList nodeEmail = element2.getElementsByTagName("email").item(0).getChildNodes();
                        NodeList nodePassword = element2.getElementsByTagName("password").item(0).getChildNodes();
                        Node nameNode = nodeName.item(0);
                        Node usernameNode = nodeUsername.item(0);
                        Node emailNode = nodeEmail.item(0);
                        Node passwordNode = nodePassword.item(0);
                        //add new user to the users list
                        users.add(new User(nameNode.getNodeValue(), emailNode.getNodeValue(), usernameNode.getNodeValue(), passwordNode.getNodeValue()));
                    }
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                System.out.println("UserXML doesn't exist");
            }
        }
    }

    //This is to check if userExists and to do the logging in functionality
    public int getUserFromXML(String username, String password, Context context){
        //This is a value to keep track of the user existance
        //userExistance = 0 means user doesn't exist
        //userExistance = 1 means the user exists and password is correct
        //userExistance = 2 means the user exists but password is wrong
        int userExistance = 0;
        try {
            //open file and get file information by using the xml format to help
            InputStream ins = context.openFileInput("UserXML.txt");
            DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDoc = docBuild.parse(ins);
            Element elem = xmlDoc.getDocumentElement();
            elem.normalize();

            //get list of users
            NodeList list_of_users = elem.getElementsByTagName("userInfo");

            //Go through list of users
            for (int i=0; i<list_of_users.getLength(); i++) {
                Node node = list_of_users.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    //Get user username and password
                    NodeList nodeList = element2.getElementsByTagName("username").item(0).getChildNodes();
                    NodeList nodePassword = element2.getElementsByTagName("password").item(0).getChildNodes();
                    Node passwordNode = nodePassword.item(0);
                    Node node2 = nodeList.item(0);
                    //If the username exists and password is correct set userExistance to 1
                    if(node2.getNodeValue().compareTo(username) == 0){
                        if(passwordNode.getNodeValue().compareTo(password) == 0){
                            userExistance = 1;
                            break;
                        }
                        //Password is wrong
                        userExistance = 2;
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return userExistance;
    }

    //Function to edit the user information in settings
    public int editUserInformation(User current_user, String username, String password,String name, String email, Context context) throws IOException {
        //Check whether user exists, if user exists already return from the method, as only individual usernames are accepted
        int userExist = getUserFromXML(username,password, context);
        if(userExist == 1 || userExist ==2){
            if(current_user.getUsername().compareTo(username) != 0) {
                return userExist;
            }
            userExist = 0;
        }
        //Update the user information in the users array
        for(int i = 0; i<users.size();i++){
            if(users.get(i).getUsername().compareTo(current_user.getUsername())==0){
                users.get(i).setUsername(username);
                users.get(i).setPassword(password);
                users.get(i).setFirstName(name);
                users.get(i).setEmail(email);
            }
        }
        //update the user information in the reviews array
        for(int i = 0; i<reviews.size();i++){
            if(reviews.get(i).getUser().getUsername().compareTo(current_user.getUsername())==0){
                reviews.get(i).getUser().setUsername(username);
                reviews.get(i).getUser().setPassword(password);
                reviews.get(i).getUser().setFirstName(name);
                reviews.get(i).getUser().setEmail(email);
            }
        }
        //Next same thing for the values in the files
        String toFile;
        File file = new File(context.getFilesDir(), "UserXML.txt");
        //Check if user file exists and if it does fetch the user information
        if(file.exists()){
            //Keep track of content of UserXML
            ArrayList<String> list_of_content = new ArrayList<>();
            InputStream in = context.openFileInput("UserXML.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String output;
            //Add everything in file to the list_of_content array
            while((output = r.readLine()) != null){
                list_of_content.add(output + "\n");
            }
            //Keeps track of where the username is so that the correct user information can be edited
            int i = 0;
            //Go through the array list and stop when the username matches the current user's username
            while(i< list_of_content.size()){
                if(list_of_content.get(i).compareTo("<username>"+current_user.getUsername()+"</username>\n")==0) {
                    break;
                }
                i++;
            }
            //Strings of user information that needs to be updated in file
            toFile = "<username>" + username + "</username>\n";
            String new_pw = "<password>" + password + "</password>\n";
            String new_name = "<name>" + name + "</name>\n";
            String new_email = "<email>" + email + "</email>\n";
            //Replace the values in the list_of_content with the updated info by using the i value to find the correct tag
            list_of_content.set(i,toFile);
            list_of_content.set(i+2, new_pw);
            list_of_content.set(i-1, new_name);
            list_of_content.set(i+1, new_email);
            OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("UserXML.txt", Context.MODE_PRIVATE));
            i = 0;
            //Overwrite stuff in the UserXML with the updated information
            while(i < list_of_content.size()){
                result.write(list_of_content.get(i));
                i++;
            }
            result.close();
            //Repeat same process as with the updation of the user information in the UserXMl file
            //with the ReviewsXML file
            File file2 = new File(context.getFilesDir(), "ReviewsXML.txt");
            if(file2.exists()) {
                list_of_content.clear();
                InputStream inp = context.openFileInput("ReviewsXML.txt");
                BufferedReader read = new BufferedReader(new InputStreamReader(inp));
                String output2;
                while ((output2 = read.readLine()) != null) {
                    list_of_content.add(output2 + "\n");
                }
                i = 0;
                while (i < list_of_content.size()) {
                    if (list_of_content.get(i).equals("<username>" + current_user.getUsername() + "</username>\n")) {
                        break;
                    }
                    i++;
                }
                toFile = "<username>" + username + "</username>\n";
                list_of_content.set(i, toFile);
                OutputStreamWriter result2 = new OutputStreamWriter(context.openFileOutput("ReviewsXML.txt", Context.MODE_PRIVATE));
                i = 0;
                while (i < list_of_content.size()) {
                    result2.write(list_of_content.get(i));
                    i++;
                }
                result2.close();
            }
        }
        return userExist;
    }

    //Fetch movies from Finnkino
    //only run once when the app is started
    public void getMoviesFromFinnkino(){
        try{
            String name;
            String releaseDate;
            String duration;
            String genre;

            //URL where to go to
            String url = "https://www.finnkino.fi/xml/Schedule/";
            //Parse the stuff in the webpage
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDocument = builder.parse(url);
            xmlDocument.getDocumentElement().normalize();

            //Get the movie info
            NodeList nlist = xmlDocument.getDocumentElement().getElementsByTagName("Show");
            for(int i = 0; i < nlist.getLength(); i++){
                Node node = nlist.item(i);
                Element element = (Element) node;
                name = element.getElementsByTagName("OriginalTitle").item(0).getTextContent();
                releaseDate = element.getElementsByTagName("ProductionYear").item(0).getTextContent();
                duration = element.getElementsByTagName("LengthInMinutes").item(0).getTextContent();
                genre = element.getElementsByTagName("Genres").item(0).getTextContent();
                //Add all the scheduled movies into the entries file
                entries.add(new Entry(new Movie(name,duration,genre,releaseDate)));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println("Couldn't get movies from finnkino");
        }
    }

    //Fetch the movies from the MovieXML and add them to the all_entries array, this is unique array
    //This is done when the app is started
    public void getMoviesFromXML(Context context){
        all_entries.clear();
        File file = new File(context.getFilesDir(), "MovieXML.txt");
        //If the file exists then do the loading, if it doesn't exist is means that there is nothing to load to the array
        if(file.exists()) {
            try {
                //Open file and fetch file content
                InputStream ins = context.openFileInput("MovieXML.txt");
                DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document xmlDoc = docBuild.parse(ins);
                Element elem = xmlDoc.getDocumentElement();
                elem.normalize();
                //Get the movies into list
                NodeList list_of_users = elem.getElementsByTagName("movieInfo");
                for (int i = 0; i < list_of_users.getLength(); i++) {
                    Node node = list_of_users.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
                        //Get each movies information and add it to the all_entries array
                        NodeList nodeMovie = element2.getElementsByTagName("movieTitle").item(0).getChildNodes();
                        NodeList nodeDuration = element2.getElementsByTagName("movieDuration").item(0).getChildNodes();
                        NodeList nodeGenre = element2.getElementsByTagName("movieGenre").item(0).getChildNodes();
                        NodeList nodeYear = element2.getElementsByTagName("movieYear").item(0).getChildNodes();
                        Node movieNode = nodeMovie.item(0);
                        Node durationNode = nodeDuration.item(0);
                        Node genreNode = nodeGenre.item(0);
                        Node yearNode = nodeYear.item(0);
                        all_entries.add(new Entry(new Movie(movieNode.getNodeValue(), durationNode.getNodeValue(), genreNode.getNodeValue(), yearNode.getNodeValue())));
                    }
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
            //This section adds any new movies in finnkino that aren't yet in the xml to the array
            //k keeps track of whether the movie is already in the all_entries array
            int k = 0;
            //Go through the finnkino entries
            for(int i = 0; i<entries.size(); i++){
                //Go through the entries from the file
                for(int y = 0; y < all_entries.size(); y++){
                    //If movie is already in all_entries, set k to 1 and break, movie will not be added to all_entries
                    if(all_entries.get(y).getMovie().getMovieName().compareTo(entries.get(i).getMovie().getMovieName()) == 0){
                        k = 1;
                        break;
                    }
                }
                //If movie was not in all_entries yet, add it to all_entries
                if(k == 0){
                    all_entries.add(entries.get(i));
                }
                k = 0;
            }
            //If the file doesn't exist add the movies fetched from finnkino to the all_movies array
        }else{
            //Makes sure that each movie is only once in the all_movies array
            int k = 0;
            for(int i = 0; i<entries.size();i++) {
                for(int y = 0; y<all_entries.size();y++){
                    if(all_entries.get(y).getMovie().getMovieName().compareTo(entries.get(i).getMovie().getMovieName())==0){
                        k = 1;
                        break;
                    }
                }
                if(k == 0){
                    all_entries.add(entries.get(i));
                }
                k = 0;
            }
        }
    }

    //Add the all_entries to MovieXML
    public void addMoviesToXML(Context context) throws IOException {
        //This is done only once in the beginning of the app
        if(add_movies_to_xml == 0) {
            String toFile;
            List<String> list_of_content = new ArrayList<>();
            //Creates new document
            File file = new File(context.getFilesDir(), "MovieXML.txt");
            //If file exists already, the new entries need to be added to file
            if (file.exists()) {
                //Functions the same way as addUserToXML
                InputStream in = context.openFileInput("MovieXML.txt");
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String output;
                while ((output = r.readLine()) != null) {
                    list_of_content.add(output + "\n");
                }
                //Keep track of position of end of file tag
                int i = 0;
                //Keep track of all movies
                int y = 0;
                while (i < list_of_content.size()) {
                    if (list_of_content.get(i).equals("</movieList>")) {
                        break;
                    }
                    if (list_of_content.get(i).contains("</movieInfo>")) {
                        y = y + 1;
                    }
                    i++;
                }
                int k = y;
                int n = i - 2;
                //Add new entries to all_entries
                while (k < all_entries.size()) {
                    toFile = "<movieInfo movie_id='" + k + "'>\n" +
                            "<movieTitle>" + all_entries.get(k).getMovie().getMovieName() + "</movieTitle>\n" +
                            "<movieDuration>" + all_entries.get(k).getMovie().getDuration() + "</movieDuration>\n" +
                            "<movieGenre>" + all_entries.get(k).getMovie().getGenre() + "</movieGenre>\n" +
                            "<movieYear>" + all_entries.get(k).getMovie().getReleaseYear() + "</movieYear>\n" +
                            "</movieInfo>\n";
                    list_of_content.add((n + 1), toFile);
                    n++;
                    k++;
                }
                OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("MovieXML.txt", Context.MODE_PRIVATE));
                i = 0;
                //Overwrite file
                while (i < list_of_content.size()) {
                    result.write(list_of_content.get(i));
                    i++;
                }
                result.close();
            } else {
                //If file doesn't exist yet then create the new file
                int k = 0;
                list_of_content.add("<?xml version='1.0' encoding='UTF-8'?>\n");
                list_of_content.add("<movieList>\n");
                while (k < all_entries.size()) {
                    toFile = "<movieInfo movie_id='" + k + "'>\n" +
                            "<movieTitle>" + all_entries.get(k).getMovie().getMovieName() + "</movieTitle>\n" +
                            "<movieDuration>" + all_entries.get(k).getMovie().getDuration() + "</movieDuration>\n" +
                            "<movieGenre>" + all_entries.get(k).getMovie().getGenre() + "</movieGenre>\n" +
                            "<movieYear>" + all_entries.get(k).getMovie().getReleaseYear() + "</movieYear>\n" +
                            "</movieInfo>\n";
                    list_of_content.add(toFile);
                    k++;
                }
                list_of_content.add("</movieList>\n");
                OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("MovieXML.txt", Context.MODE_PRIVATE));
                int i = 0;
                while (i < list_of_content.size()) {
                    result.write(list_of_content.get(i));
                    i++;
                }
                result.close();
                System.out.println("File created!");
            }
        }
    }

    //Get array of all the movie names to set in spinners
    public ArrayList<String> getMovieNames(){
        ArrayList<String> movieNames = new ArrayList<>();
        for(int i = 0; i<all_entries.size();i++){
            movieNames.add(all_entries.get(i).getMovie().getMovieName());
        }
        return movieNames;
    }

    //Get the current user -> stay logged in
    public User getCurrentUser(String username) {
        int i = 0;
        User returnUser = null;
        while(i < users.size()){
            if(users.get(i).getUsername().compareTo(username) == 0){
                returnUser = users.get(i);
                break;
            }
            i++;
        }
        return returnUser;
    }

    //Upon app start load from file to object array all the reviews
    //Functions the same way as the user method that loads from user file to object array
    public void getReviewsFromXML(Context context){
        reviews.clear();
        File file = new File(context.getFilesDir(), "ReviewsXML.txt");
        if(file.exists()) {
            try {
                InputStream ins = context.openFileInput("ReviewsXML.txt");
                DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document xmlDoc = docBuild.parse(ins);
                Element elem = xmlDoc.getDocumentElement();
                elem.normalize();
                NodeList list_of_users = elem.getElementsByTagName("reviewInfo");
                for (int i = 0; i < list_of_users.getLength(); i++) {
                    Node node = list_of_users.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
                        NodeList nodeName = element2.getElementsByTagName("movieTitle").item(0).getChildNodes();
                        NodeList nodeDuration = element2.getElementsByTagName("movieDuration").item(0).getChildNodes();
                        NodeList nodeGenre = element2.getElementsByTagName("movieGenre").item(0).getChildNodes();
                        NodeList nodeYear = element2.getElementsByTagName("movieYear").item(0).getChildNodes();
                        NodeList nodeTimestamp = element2.getElementsByTagName("timestamp").item(0).getChildNodes();
                        NodeList nodeRating = element2.getElementsByTagName("rating").item(0).getChildNodes();
                        NodeList nodeComment = element2.getElementsByTagName("comment").item(0).getChildNodes();
                        NodeList nodeUsername = element2.getElementsByTagName("username").item(0).getChildNodes();
                        Node nameNode = nodeName.item(0);
                        Node durationNode = nodeDuration.item(0);
                        Node genreNode = nodeGenre.item(0);
                        Node yearNode = nodeYear.item(0);
                        Node timestampNode = nodeTimestamp.item(0);
                        Node ratingNode = nodeRating.item(0);
                        Node commentNode = nodeComment.item(0);
                        Node usernameNode = nodeUsername.item(0);
                        User user = getCurrentUser(usernameNode.getNodeValue());
                        reviews.add(new Reviews(new Movie(nameNode.getNodeValue(), durationNode.getNodeValue(), genreNode.getNodeValue(), yearNode.getNodeValue()), timestampNode.getNodeValue(),ratingNode.getNodeValue(),commentNode.getNodeValue(), user));
                    }
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                System.out.println("ReviewsXML doesn't exist");
            }
        }

    }

    //Add a review to the reviews array list and to the ReviewsXML file, works the same as addUserToXML
    public void setReviewToXML(Context context, Movie movie, String rating, String comment, User user) throws IOException{
        String toFile;
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String timeStamp = date.format(new Date());
        //Create user object
        reviews.add(new Reviews(movie, timeStamp, rating, comment, user));
        //Creates new document
        File file = new File(context.getFilesDir(), "ReviewsXML.txt");
        if(file.exists()){
            List<String> list_of_content = new ArrayList<>();
            InputStream in = context.openFileInput("ReviewsXML.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String output;
            while((output = r.readLine()) != null){
                list_of_content.add(output + "\n");
            }
            int i = 0;
            int y = 0;
            while(i< list_of_content.size()){
                if(list_of_content.get(i).equals("</reviewList>")) {
                    break;
                }
                if(list_of_content.get(i).contains("</reviewInfo>")){
                    y = y+1;
                }
                i++;
            }
            toFile = "<reviewInfo review_id='"+ y +"'>\n" +
                    "<movieTitle>" + movie.getMovieName() + "</movieTitle>\n" +
                    "<movieDuration>" + movie.getDuration() + "</movieDuration>\n" +
                    "<movieGenre>" + movie.getGenre() + "</movieGenre>\n" +
                    "<movieYear>" + movie.getReleaseYear() +"</movieYear>\n" +
                    "<timestamp>" + timeStamp + "</timestamp>\n"+
                    "<rating>" + rating + "</rating>\n"+
                    "<comment>" + comment + "</comment>\n"+
                    "<username>" + user.getUsername() + "</username>\n"+
                    "</reviewInfo>\n";
            list_of_content.add((i-1), toFile);
            OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("ReviewsXML.txt", Context.MODE_PRIVATE));
            i = 0;
            while(i < list_of_content.size()){
                result.write(list_of_content.get(i));
                i++;
            }
            result.close();
        }
        else {
            toFile = "<?xml version='1.0' encoding='UTF-8'?>\n"+"<reviewList>\n" +"<reviewInfo review_id='0'>\n" +
                    "<movieTitle>" + movie.getMovieName() + "</movieTitle>\n" +
                    "<movieDuration>" + movie.getDuration() + "</movieDuration>\n" +
                    "<movieGenre>" + movie.getGenre() + "</movieGenre>\n" +
                    "<movieYear>" + movie.getReleaseYear() +"</movieYear>\n" +
                    "<timestamp>" + timeStamp + "</timestamp>\n"+
                    "<rating>" + rating + "</rating>\n"+
                    "<comment>" + comment + "</comment>\n"+
                    "<username>" + user.getUsername() + "</username>\n"+
                    "</reviewInfo>\n"+
                    "</reviewList>\n";
            OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("ReviewsXML.txt", Context.MODE_PRIVATE));
            result.write(toFile);
            result.close();
            System.out.println("File created!");
        }
    }

    //Get the reviews when the movie name in the review is the one given from the activity
    public ArrayList<Reviews> getReviewsByMovieName(String movie_name){
        ArrayList<Reviews> return_reviews = new ArrayList<>();
        for(int i = 0; i<reviews.size();i++){
            if(reviews.get(i).getMovie().getMovieName().compareTo(movie_name)==0){
                return_reviews.add(reviews.get(i));
            }
        }
        return return_reviews;
    }

    //Method to sort the reviewed movies by the rating they got
    public ArrayList<Movie> sortMoviesByRating() {
        ArrayList<String[]> sorted_movies = new ArrayList<>();
        //Keep track of the rating in order to calculate an average score for each movie
        float average = 0.0f;
        float amount = 0.0f;
        float calculated_average;
        for (int i = 0; i < all_entries.size(); i++) {
            for (int j = 0; j < reviews.size(); j++) {
                if (reviews.get(j).getMovie().getMovieName().compareTo(all_entries.get(i).getMovie().getMovieName()) == 0) {
                    average = average + Integer.parseInt(reviews.get(j).getRating());
                    amount = amount + 1;
                }
            }
            //When the movie had some rating, calculate the average and add it ot the sorted_movies list
            if (amount > 0) {
                calculated_average = average / amount;
                String[] movie_info = {all_entries.get(i).getMovie().getMovieName(), String.valueOf(calculated_average)};
                sorted_movies.add(movie_info);
                average = 0;
                amount = 0;
            }
        }
        //In here we sort the sorted_movies list in a declining review order
        ArrayList<String[]> sort = new ArrayList<>();
        if (!sorted_movies.isEmpty()) {
            for (int i = 0; i < sorted_movies.size(); i++) {
                //If sort is empty add the first value from sorted_movies
                if (sort.isEmpty()) {
                    sort.add(sorted_movies.get(i));
                } else {
                    //k keeps track of whether the movie needs to be added in the end
                    int k = 0;
                    for (int j = 0; j < sort.size(); j++) {
                        //If the current movie has a higher or equal score than the movie in the sort array, add it before that movie
                        if (Float.valueOf(sorted_movies.get(i)[1]) >= Float.valueOf(sort.get(j)[1])) {
                            sort.add(j, sorted_movies.get(i));
                            k = 1;
                            break;
                        }
                    }
                    //Add movie to ending if it didn't fulfill the upper condition
                    if(k == 0){
                        sort.add(sorted_movies.get(i));
                    }
                }
            }
        }
        //Create the array list that is used to populate the grid view
        ArrayList<Movie> sorted_movies_back = new ArrayList<>();
        sorted_movies_back.add(new Movie("Rating and Movie name", "Movie duration", "Movie genre", "Movie released (year)"));
        for(int i = 0; i<sort.size(); i++){
            for(int j = 0; j<all_entries.size();j++){
                if(all_entries.get(j).getMovie().getMovieName().compareTo(sort.get(i)[0])==0) {
                    sorted_movies_back.add(new Movie(sort.get(i)[1] + " " + sort.get(i)[0],all_entries.get(j).getMovie().getDuration(),all_entries.get(j).getMovie().getGenre(),all_entries.get(j).getMovie().getReleaseYear()));
                }
            }
        }
        return sorted_movies_back;
    }
    public ArrayList<Entry> getEntries() {
        return all_entries;
    }
}
