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

public class MovieManager implements Serializable {
    private static MovieManager manager = null;
    ArrayList <User> users;
    ArrayList <Entry> entries;
    ArrayList <Entry> all_entries; //Entries that are in file also, TODO: Make sure they are unique!!
    int add_movies_to_xml;

    private MovieManager() {
        users = new ArrayList<>();
        entries = new ArrayList<>();
        all_entries = new ArrayList<>();
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
            getMoviesFromXML(context_app);
            try {
                addMoviesToXML(context_app);
                add_movies_to_xml = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int addUserToXML(String username, String name, String password, String email, Context context) throws IOException {
        //Create user object
        users.add(new User(name, email, username, password));
        String toFile;
        //Check whether username is taken
        int userExist = getUserFromXML(username,password, context);
        if(userExist == 1 || userExist ==2){
            return userExist;
        }
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
            toFile = "<userList>\n" +"<userInfo user_id='0'>\n" +
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
                e.printStackTrace();
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
            e.printStackTrace();
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
            all_entries = entries;
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
                OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("UserXML.txt", Context.MODE_PRIVATE));
                i = 0;
                while (i < list_of_content.size()) {
                    result.write(list_of_content.get(i));
                    i++;
                }
                result.close();
            } else {
                int k = 0;
                while (k < entries.size()) {
                    toFile = "<movieList>\n" + "<movieInfo movie_id='" + k + "'>\n" +
                            "<movieTitle>" + entries.get(k).getMovie().getMovieName() + "</movieTitle>\n" +
                            "<movieDuration>" + entries.get(k).getMovie().getDuration() + "</movieDuration>\n" +
                            "<movieGenre>" + entries.get(k).getMovie().getGenre() + "</movieGenre>\n" +
                            "<movieYear>" + entries.get(k).getMovie().getReleaseYear() + "</movieYear>\n" +
                            "</movieInfo>\n" +
                            "</movieList>\n";
                    list_of_content.add(toFile);
                    k++;
                }
                OutputStreamWriter result = new OutputStreamWriter(context.openFileOutput("UserXML.txt", Context.MODE_PRIVATE));
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

    public ArrayList<Entry> getEntries() {
        return all_entries;
    }

    public void setEntry(Entry entry) {
    }
/*
    public String getCurrentShows() {
        return ; //???
    }

    public String listMoviesByRating() {
        return ; //???
    }
*/
}
