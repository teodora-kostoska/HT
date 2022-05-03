package com.example.harjoitustyo;


import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieManager implements Serializable {
    private static MovieManager manager = null;
    ArrayList <User> users;
    ArrayList <Entry> entries;
    ArrayList <Entry> all_entries; //Entries that are in file also, TODO: Make sure they are unique!!
    ArrayList <Reviews> reviews;
    int add_movies_to_xml;

    private MovieManager() {
        users = new ArrayList<>();
        entries = new ArrayList<>();
        all_entries = new ArrayList<>();
        reviews = new ArrayList<>();
        getMoviesFromFinnkino();
        add_movies_to_xml = 0;
    }

    public static MovieManager getInstance()
    {
        if (manager == null)
            manager = new MovieManager();
        return manager;
    }

    public void GetMovieInfo(Context context_app){
        if(add_movies_to_xml == 0) {
            addUsersFromXMLToObject(context_app);
            getMoviesFromXML(context_app);
            getReviewsFromXML(context_app);
            try {
                addMoviesToXML(context_app);
                add_movies_to_xml = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int addUserToXML(String username, String name, String password, String email, Context context) throws IOException {
        String toFile;
        //Check whether username is taken
        int userExist = getUserFromXML(username,password, context);
        if(userExist == 1 || userExist ==2){
            return userExist;
        }
        //Create user object
        users.add(new User(name, email, username, password));
        //Creates new document
        File file = new File(context.getFilesDir(), "UserXML.txt");
        if(file.exists()){
            List<String> list_of_content = new ArrayList<>();
            InputStream in = context.openFileInput("UserXML.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String output;
            while((output = r.readLine()) != null){
                list_of_content.add(output + "\n");
            }
            int i = 0;
            int y = 0;
            while(i< list_of_content.size()){
                if(list_of_content.get(i).equals("</userList>"))
                    break;
                if(list_of_content.get(i).contains("</userInfo>")){
                    y = y+1;
                }
                i++;
            }
            toFile = "<userInfo user_id='"+ y +"'>\n" +
                    "<name>" + name + "</name>\n" +
                    "<username>" + username + "</username>\n" +
                    "<email>" + email + "</email>\n" +
                    "<password>" + password +"</password>\n" +
                    "</userInfo>\n";
            list_of_content.add((i-1), toFile);
            OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("UserXML.txt", Context.MODE_PRIVATE));
            i = 0;
            while(i < list_of_content.size()){
                result.write(list_of_content.get(i));
                i++;
            }
            result.close();
        }
        else {
            toFile = "<?xml version='1.0' encoding='UTF-8'?>\n"+"<userList>\n" +"<userInfo user_id='0'>\n" +
                    "<name>" + name + "</name>\n" +
                    "<username>" + username + "</username>\n" +
                    "<email>" + email + "</email>\n" +
                    "<password>" + password +"</password>\n" +
                    "</userInfo>\n"+
                    "</userList>\n";
            OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("UserXML.txt", Context.MODE_PRIVATE));
            result.write(toFile);
            result.close();
            System.out.println("File created!");
        }
        return userExist;
    }

    public void addUsersFromXMLToObject(Context context){
        users.clear();
        File file = new File(context.getFilesDir(), "UserXML.txt");
        if(file.exists()) {
            try {
                InputStream ins = context.openFileInput("UserXML.txt");
                DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document xmlDoc = docBuild.parse(ins);
                Element elem = xmlDoc.getDocumentElement();
                elem.normalize();
                NodeList list_of_users = elem.getElementsByTagName("userInfo");
                for (int i = 0; i < list_of_users.getLength(); i++) {
                    Node node = list_of_users.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
                        NodeList nodeName = element2.getElementsByTagName("name").item(0).getChildNodes();
                        NodeList nodeUsername = element2.getElementsByTagName("username").item(0).getChildNodes();
                        NodeList nodeEmail = element2.getElementsByTagName("email").item(0).getChildNodes();
                        NodeList nodePassword = element2.getElementsByTagName("password").item(0).getChildNodes();
                        Node nameNode = nodeName.item(0);
                        Node usernameNode = nodeUsername.item(0);
                        Node emailNode = nodeEmail.item(0);
                        Node passwordNode = nodePassword.item(0);
                        users.add(new User(nameNode.getNodeValue(), emailNode.getNodeValue(), usernameNode.getNodeValue(), passwordNode.getNodeValue()));
                    }
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                System.out.println("UserXML doesn't exist");
            }
        }
    }

    public int getUserFromXML(String username, String password, Context context){
        int userExistance = 0;
        try {
            InputStream ins = context.openFileInput("UserXML.txt");
            DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDoc = docBuild.parse(ins);
            Element elem = xmlDoc.getDocumentElement();
            elem.normalize();

            NodeList list_of_users = elem.getElementsByTagName("userInfo");

            for (int i=0; i<list_of_users.getLength(); i++) {
                Node node = list_of_users.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    NodeList nodeList = element2.getElementsByTagName("username").item(0).getChildNodes();
                    NodeList nodePassword = element2.getElementsByTagName("password").item(0).getChildNodes();
                    Node passwordNode = nodePassword.item(0);
                    Node node2 = nodeList.item(0);
                    if(node2.getNodeValue().compareTo(username) == 0){
                        if(passwordNode.getNodeValue().compareTo(password) == 0){
                            userExistance = 1;
                            break;
                        }
                        userExistance = 2;
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return userExistance;
    }

    public int editUserInformation(User current_user, String username, String password, Context context){
        int userExist = getUserFromXML(username,password, context);
        if((userExist == 1 || userExist ==2)&&username.compareTo(current_user.getUsername())!=0){
            return userExist;
        }
        for(int i = 0; i<users.size();i++){
            if(users.get(i).getUsername().compareTo(username)==0){
                users.get(i).setUsername(username);
                users.get(i).setPassword(password);
            }
        }
        for(int i = 0; i<reviews.size();i++){
            if(reviews.get(i).getUser().getUsername().compareTo(username)==0){
                reviews.get(i).getUser().setUsername(username);
                reviews.get(i).getUser().setPassword(password);
            }
        }
        //Next same thing for the values in the files
        return userExist;
    }
    /*
    public String hashPassword(String password){
        String generatedPassword = "";
        try {
            //Salt
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            //instance for hashing using SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < bytes.length; i++) {
                generatedPassword = generatedPassword+Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
     */
    public void getMoviesFromFinnkino(){
        try{
            String name;
            String releaseDate;
            String duration;
            String genre;

            String url = "https://www.finnkino.fi/xml/Schedule/";
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDocument = builder.parse(url);
            xmlDocument.getDocumentElement().normalize();
            NodeList nlist = xmlDocument.getDocumentElement().getElementsByTagName("Show");
            for(int i = 0; i < nlist.getLength(); i++){
                Node node = nlist.item(i);
                Element element = (Element) node;
                name = element.getElementsByTagName("OriginalTitle").item(0).getTextContent();
                releaseDate = element.getElementsByTagName("ProductionYear").item(0).getTextContent();
                duration = element.getElementsByTagName("LengthInMinutes").item(0).getTextContent();
                genre = element.getElementsByTagName("Genres").item(0).getTextContent();
                entries.add(new Entry(new Movie(name,duration,genre,releaseDate)));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println("Couldn't get movies from finnkino");
        }
    }

    public void getMoviesFromXML(Context context){
        all_entries.clear();
        File file = new File(context.getFilesDir(), "MovieXML.txt");
        if(file.exists()) {
            try {
                InputStream ins = context.openFileInput("MovieXML.txt");
                DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document xmlDoc = docBuild.parse(ins);
                Element elem = xmlDoc.getDocumentElement();
                elem.normalize();
                NodeList list_of_users = elem.getElementsByTagName("movieInfo");
                for (int i = 0; i < list_of_users.getLength(); i++) {
                    Node node = list_of_users.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
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
            int k = 0;
            for(int i = 0; i<entries.size(); i++){
                for(int y = 0; y < all_entries.size(); y++){
                    if(all_entries.get(y).getMovie().getMovieName().compareTo(entries.get(i).getMovie().getMovieName()) == 0){
                        k = 1;
                        break;
                    }
                }
                if(k == 0){
                    all_entries.add(entries.get(i));
                }
                k = 0;
            }
        }else{
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

    public void addMoviesToXML(Context context) throws IOException {
        if(add_movies_to_xml == 0) {
            String toFile;
            List<String> list_of_content = new ArrayList<>();
            //Creates new document
            File file = new File(context.getFilesDir(), "MovieXML.txt");
            if (file.exists()) {
                InputStream in = context.openFileInput("MovieXML.txt");
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String output;
                while ((output = r.readLine()) != null) {
                    list_of_content.add(output + "\n");
                }
                int i = 0;
                int y = 0;
                while (i < list_of_content.size()) {
                    if (list_of_content.get(i).equals("</movieList>"))
                        break;
                    if (list_of_content.get(i).contains("</movieInfo>")) {
                        y = y + 1;
                    }
                    i++;
                }
                int k = y;
                int n = i - 2;
                while (k < all_entries.size()) {
                    toFile = "<movieInfo movie_id='" + k + "'>\n" +
                            "<movieTitle>" + all_entries.get(k).getMovie().getMovieName() + "</movieTitle>\n" +
                            "<movieDuration>" + all_entries.get(k).getMovie().getDuration() + "</movieDuration>\n" +
                            "<movieGenre>" + all_entries.get(k).getMovie().getGenre() + "</movieGenre>\n" +
                            "<movieYear>" + all_entries.get(k).getMovie().getReleaseYear() + "</movieYear>\n" +
                            "</movieInfo>\n";
                    list_of_content.add((n + 1), toFile);
                    k++;
                    n++;
                }
                OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("MovieXML.txt", Context.MODE_PRIVATE));
                i = 0;
                while (i < list_of_content.size()) {
                    result.write(list_of_content.get(i));
                    i++;
                }
                result.close();
            } else {
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

    public ArrayList<String> getMovieNames(){
        ArrayList<String> movieNames = new ArrayList<>();
        for(int i = 0; i<all_entries.size();i++){
            movieNames.add(all_entries.get(i).getMovie().getMovieName());
        }
        return movieNames;
    }

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
                if(list_of_content.get(i).equals("</reviewList>"))
                    break;
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

    public ArrayList<Reviews> getReviewsByMovieName(String movie_name){
        ArrayList<Reviews> return_reviews = new ArrayList<>();
        for(int i = 0; i<reviews.size();i++){
            if(reviews.get(i).getMovie().getMovieName().compareTo(movie_name)==0){
                return_reviews.add(reviews.get(i));
            }
        }
        return return_reviews;
    }

    public ArrayList<Movie> sortMoviesByRating() {
        ArrayList<String[]> sorted_movies = new ArrayList<>();
        float average = 0.0f;
        float amount = 0.0f;
        float calculated_average = 0.0f;
        for (int i = 0; i < all_entries.size(); i++) {
            for (int j = 0; j < reviews.size(); j++) {
                if (reviews.get(j).getMovie().getMovieName().compareTo(all_entries.get(i).getMovie().getMovieName()) == 0) {
                    average = average + Integer.parseInt(reviews.get(j).getRating());
                    amount = amount + 1;
                }
            }
            if (amount > 0) {
                calculated_average = average / amount;
                String[] movie_info = {all_entries.get(i).getMovie().getMovieName(), String.valueOf(calculated_average)};
                sorted_movies.add(movie_info);
                average = 0;
                amount = 0;
            }
        }
        ArrayList<String[]> sort = new ArrayList<>();
        if (!sorted_movies.isEmpty()) {
            for (int i = 0; i < sorted_movies.size(); i++) {
                if (sort.isEmpty()) {
                    sort.add(sorted_movies.get(i));
                } else {
                    int k = 0;
                    for (int j = 0; j < sort.size(); j++) {
                        if (Float.valueOf(sorted_movies.get(i)[1]) >= Float.valueOf(sort.get(j)[1])) {
                            sort.add(j, sorted_movies.get(i));
                            k = 1;
                            break;
                        }
                    }
                    if(k == 0){
                        sort.add(sorted_movies.get(i));
                    }
                }
            }
        }
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
