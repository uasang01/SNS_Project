package com.example.sns_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {
    private String title;
    private ArrayList<String> content;
    private ArrayList<String> format;
    private String publisher;
    private Date createdAt;
    private String id;


    public PostInfo(String id, String title, ArrayList<String> content, ArrayList<String> format, String publisher, Date createdAt){
        this.id = id;
        this.title = title;
        this.content = content;
        this.format = format;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }
    public PostInfo(String title, ArrayList<String> content, ArrayList<String> format, String publisher, Date createdAt){
        this.title = title;
        this.content = content;
        this.format = format;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }
    public Map<String, Object> getPostInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("title",title);
        docData.put("content",content);
        docData.put("format",format);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);
        return docData;
    }


    public void setFormat(ArrayList<String> format) { this.format = format; }

    public ArrayList<String> getFormat() { return format; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public void setId(String id) { this.id = id; }

    public String getId() { return id; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getContent() {
        return content;
    }
}
