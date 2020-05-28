package com.example.sns_project;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String name;
    private String phone;
    private String birthday;
    private String address;
    private String photoUrl;

    public UserInfo(String name, String phone, String birthday, String address, String photoUrl) {
        this.name = name;
        this.phone = phone;
        this.birthday = birthday;
        this.address = address;
        this.photoUrl = photoUrl;
    }

    public UserInfo(String name, String phone, String birthday, String address) {
        this.name = name;
        this.phone = phone;
        this.birthday = birthday;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public String getAddress() {
        return address;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
