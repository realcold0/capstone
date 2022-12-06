package com.akj.sns_project;

import java.util.ArrayList;
import java.util.Date;

public class ReplyInfo {
    private String contents;
    private String id;
    private Date createdAt;
    private String saveLocation;


    public ReplyInfo(String contents, Date createdAt, String saveLocation){

        this.contents = contents;
        this.createdAt = createdAt;
        this.saveLocation = saveLocation;
    }

    public String getsaveLocation(){
        return this.saveLocation;
    }
    public void setsaveLocation(String saveLocation){
        this.contents = saveLocation;
    }

    public String getContents(){
        return this.contents;
    }
    public void setContents(String contents){
        this.contents = contents;
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
}
