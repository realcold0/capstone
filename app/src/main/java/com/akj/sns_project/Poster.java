package com.akj.sns_project;

public class Poster {
    private String MovieName;
    private String Poster;
    public Poster(String MovieName,String Poster){
        this.MovieName = MovieName;
        this.Poster = Poster;
    }
    public String getMovieName(){
        return MovieName;
    }
    public String getPoster()
    {
        return Poster;
    }
}