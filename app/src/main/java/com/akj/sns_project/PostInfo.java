package com.akj.sns_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {
    private String title;
    private ArrayList<String> contents;
    private String publisher;
    private Date createdAt;
    private String id;
    private int like;
    private int unlike;
    private String saveLocation;
    private ArrayList<String> favorites;
    private ArrayList<String> unfavorites;

    public PostInfo(String title, ArrayList<String> contents, String publisher, Date createdAt,
                    String id, int like, int unlike, String saveLocation, ArrayList<String> favorites, ArrayList<String> unfavorites ){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.like = like;
        this.unlike = unlike;
        this.saveLocation = saveLocation;
        this.favorites = favorites;
        this.unfavorites = unfavorites;
    }

    public PostInfo(String title, ArrayList<String> contents, String publisher, Date createdAt,
                    int like, int unlike, String saveLocation, ArrayList<String> favorites, ArrayList<String> unfavorites){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = publisher;
        this.like = like;
        this.unlike = unlike;
        this.saveLocation = saveLocation;
        this.favorites = favorites;
        this.unfavorites = unfavorites;
    }

/*
    public Map<String, Object> getPostInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("title");
    }*/

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public ArrayList<String> getContents(){
        return this.contents;
    }
    public void setContents(ArrayList<String> contents){
        this.contents = contents;
    }

    public String getPublisher(){
        return this.publisher;
    }
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

    public Date getCreatedAt(){
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }

    public String getId(){
        return this.id;
    }
    public void setId(String id) {this.id = id;}

    public int getlike(){
        return this.like;
    }
    public void setlike(int like){ this.like = like; }

    public int getUnlike(){
        return this.unlike;
    }
    public void setunlike(int unlike){ this.unlike = unlike; }

    public String getsaveLocation(){
        return this.saveLocation;
    }
    public void setsaveLocation(String saveLocation){
        this.saveLocation = saveLocation;
    }

    public ArrayList<String> getFavorites(){
        return this.favorites;
    }
    public void setFavorites(ArrayList<String> favorites){
        this.favorites = favorites;
    }

    public ArrayList<String> getUnfavorites(){
        return this.favorites;
    }
    public void setUnfavorites(ArrayList<String> unfavorites){
        this.unfavorites = unfavorites;
    }

}