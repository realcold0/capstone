package com.akj.sns_project;

import java.util.ArrayList;
import java.util.Date;

public class WriteInfo {
    private String title;
    private ArrayList<String> contents;
    private String publisher;
    private Date cretedAt;

    public WriteInfo(String title, ArrayList<String> contents, String publisher, Date cretedAt){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.cretedAt = cretedAt;
    }

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

    public String getPublisher(){return this.publisher; }
    public void setPublisher(String publisher){this.publisher = publisher;}

    public Date getCretedAt(){return this.cretedAt; }
    public void setCretedAt(Date cretedAt){this.cretedAt = cretedAt;}
}