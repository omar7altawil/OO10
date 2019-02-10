package com.example.omara.oo10;

import java.util.ArrayList;

public class Tag {

    public String tagName;
    public ArrayList<Rateable> identifies;

    public Tag(String tagName, Rateable obj){
        this.tagName = tagName;
        identifies.add(obj);

    }

    public Tag(String tagName){
        this.tagName = tagName;
        this.identifies = new ArrayList<Rateable>();
    }

    public void addRateable(Rateable e){
        identifies.add(e);
       // e.addTag(this);
    }

    public String getTagName() {
        return tagName;
    }

    public void tags(Rateable obj){
        identifies.add(obj);
    }

}
