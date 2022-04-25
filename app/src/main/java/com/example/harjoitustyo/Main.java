package com.example.harjoitustyo;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Movie movie = new Movie("1", "2", "3", "4");
        movie.setMovieName("Aladfin");
        System.out.println(movie.getMovieName());

        User user = new User("1", "2", "3", "4", "5");
        user.setPassword("Aladfin");
        System.out.println(user.getUsername() + user.getPassword());

    }

}
