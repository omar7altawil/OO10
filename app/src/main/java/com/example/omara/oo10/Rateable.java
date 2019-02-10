package com.example.omara.oo10;

import android.net.Uri;
import android.widget.ImageView;
import java.util.ArrayList;

public class Rateable {

    public String tagName;
    public String name;
    public static String ID;
    public double oo10;
    public ArrayList<Rating> ratings;
    public ArrayList<String> tags;
    public String picUrl;
    public ImageView BGPicture;
    public String description;
    public double reputation;
    public boolean verified;
    public String creatorID;

    public Rateable(String tagName,String name,Rating rate,String description,String category,String userID, String[] tages, String picUrl){
        this.tagName = tagName;
        this.name = name;

        ratings = new ArrayList<Rating>();
        ratings.add(rate);
        oo10 = rate.getRate();

        this.description = description;

        tags = new ArrayList<String>();
        tags.add(category);
        for(int i = 0; i < tages.length;i++){
            tags.add(tages[i]);
        }

        this.picUrl = picUrl;

        creatorID = userID;

        reputation = 0;
        verified = false;
    }

    public Rateable(){

    }
    public Rateable(String tagName){
        this.tagName =tagName ;
        oo10 = 0;
        reputation = 0;
        verified = false;
        //creator = user;
    }

    public void addRate(Rating rate){
        ratings.add(rate);
        calculateRate();
    }

    public void addTag(Tag t){
        tags.add(t.getTagName());
        // PROBREM-- READ FROM THE DATABASE
        t.addRateable(this);
    }


    public void calculateRate(){
        for (Rating obj: ratings) {
            oo10 = oo10 + obj.getRate();
        }
        oo10 = oo10/ratings.size();
    }

    public void verify(){
        verified = true;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    public boolean getverify(){
        return verified;
    }
    public String getName(){
        return tagName;
    }
    public String getDescription(){
        return description;
    }
    public double getRating(){
        return oo10;
    }
    public String getPicUrl(){
        return picUrl;
    }
    public ArrayList<String> getTags(){
        return tags;
    }

    @Override
    public String toString() {
        return "Rateable{" +
                "tagName='" + tagName + '\'' +
                ", name='" + name + '\'' +
                ", oo10=" + oo10 +
                ", ratings=" + ratings +
                ", tags=" + tags +
                ", picUrl='" + picUrl + '\'' +
                ", BGPicture=" + BGPicture +
                ", description='" + description + '\'' +
                ", reputation=" + reputation +
                ", verified=" + verified +
                ", creatorID='" + creatorID + '\'' +
                '}';
    }
}
