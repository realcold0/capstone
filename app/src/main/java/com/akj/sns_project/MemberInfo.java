package com.akj.sns_project;

import android.widget.EditText;

public class MemberInfo {
    private String name;
    private String phoneNubmer;
    private String birthDay;
    private String address;
    private String photoUrl;

    public MemberInfo(String name, String phoneNubmer, String birthDay, String address, String photoUrl){
        this.name = name;
        this.phoneNubmer = phoneNubmer;
        this.birthDay = birthDay;
        this.address = address;
        this.photoUrl = photoUrl;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getPhoneNubmer(){
        return this.phoneNubmer;
    }
    public void setPhoneNubmer(String phoneNubmer){
        this.phoneNubmer = phoneNubmer;
    }

    public String getBirthDay(){
        return this.birthDay;
    }
    public void setBirthDay(String birthDay){
        this.birthDay = birthDay;
    }

    public String getAddress(){return this.address; }
    public void setAddress(String address){this.address = address;}

    public String getPhotoUrl(){return this.photoUrl; }
    public void setPhotoUrl(String photoUrl){this.photoUrl = photoUrl;}
}
