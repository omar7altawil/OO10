package com.example.omara.oo10;

import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rkcs on 12/3/2017.
 */

public class HomePage extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference TagRoot = database.getReference("tags");
    private EditText e;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:
                   // mTextMessage.setText(R.string.title_dashboard);
                    Intent intent1 = new Intent(HomePage.this, Categories.class);
                    startActivity(intent1);

                    return true;
                case R.id.navigation_notifications:
                   // mTextMessage.setText(R.string.title_notifications);
                    Intent intent2 = new Intent(HomePage.this, UserPage.class);
                    startActivity(intent2);

                    return true;

                default:
                    return false;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        e = (EditText) findViewById(R.id.editTextHome);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
       // navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
       navigation.setSelectedItemId(R.id.navigation_dashboard);
    }

    public void goList(View view)
    {
        List.setSI(e.getText().toString().toLowerCase());
        startActivity(new Intent(HomePage.this, List.class));
    }

}
