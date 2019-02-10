package com.example.omara.oo10;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by rkcs on 12/3/2017.
 */

public class RateablePage  extends AppCompatActivity {

    private static Rateable requestedObj ;
    private TextView nor;
    private TextView displayname;
    private TextView displaytags;
    private TextView displaydesc;
    private RatingBar displayrating;
    private ImageView displaypic;
    private ImageView verified;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference TagRoot = database.getReference("tags");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rateablepage);


        nor = (TextView) findViewById(R.id.nor) ;
        displayname = (TextView) findViewById(R.id.rtbName) ;
        displaytags = (TextView) findViewById(R.id.rtbTags) ;
        displaydesc = (TextView) findViewById(R.id.rtbDescription) ;
        displayrating = (RatingBar) findViewById(R.id.rtbRate) ;
        displaypic = (ImageView) findViewById(R.id.rtbImage) ;
        verified = (ImageView) findViewById(R.id.verified) ;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setChecked(false);
        }
        setUpPage();
    }

    public static void setRequestedObj(Rateable obj){
        requestedObj = obj;
    }

    public void setUpPage() { // Researches database


                Rateable ret = requestedObj;
                displayname.setText(ret.getName());
                 nor.setText(""+ret.ratings.size());
                for (String tag: ret.getTags()) {

                    displaytags.append(tag+" ");

                }
                displaydesc.setText(ret.getDescription());
                displayrating.setRating((float)ret.getRating());
                Glide.with(getApplicationContext()).load(ret.getPicUrl()).into(displaypic);
                if(ret.verified==true)
                    verified.setVisibility(View.VISIBLE);
                else
                    verified.setVisibility(View.GONE);



    }// End List down

    public void goHome(View view){
        startActivity(new Intent(RateablePage.this, HomePage.class));
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(RateablePage.this, HomePage.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_dashboard:
                    Intent intent1 = new Intent(RateablePage.this, Categories.class);
                    startActivity(intent1);

                    return true;
                case R.id.navigation_notifications:
                    Intent intent2 = new Intent(RateablePage.this, UserPage.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        }
    };
}