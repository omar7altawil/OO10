package com.example.omara.oo10;

public class Rating {

    public double oo10;
    public String userID;
    public String rateable;

    public Rating(){
        oo10 = 0;
    }

    public Rating(String user, String rateable, double rate){
        this.userID = user;
        this.rateable = rateable;
        this.oo10 = rate;
    }

    public double getRate() {
        return oo10;
    }
}
