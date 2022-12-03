package com.akj.sns_project;

public class Poster {
    private String MovieName;
    private String imageUrl;
    public Poster(String MovieName,String imageUrl){
        this.MovieName = MovieName;
        this.imageUrl =  "https://image.tmdb.org/t/p/w500"+ imageUrl;
    }
    public String getMovieName(){
        return MovieName;
    }
    public String getImageUrl()
    {
        return imageUrl;
    }
}