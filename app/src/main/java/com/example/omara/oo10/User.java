package com.example.omara.oo10;

import java.util.ArrayList;
import android.widget.ImageView;

public class User {


    // Account rates    // User info
    public String userName;
    public String usersurname;
    public String userNickname;
    public ImageView userAvatar;
    public ImageView userBG;

    // Account info
    public static String userID;
    public ArrayList<Rating> objectsRated;
    public ArrayList<User> followers;
    public ArrayList<User> following;
    public ArrayList<User> blocked;
    public double reputation;
    public boolean accVerified;
    public double accuracy;
    public int level;
    public double points;

    public ArrayList<Rating> accRatings;
    public Rating accRate;

    public User(String userName, String usersurname, String userNickname) {
        this.userName = userName;
        this.usersurname = usersurname;
        this.userNickname = userNickname;
        accVerified =  false;
        reputation = 0;
        accuracy = 0;
        level = 0;
        points = 0;
    }

    public void verify(){
        accVerified = true;
    }


}
