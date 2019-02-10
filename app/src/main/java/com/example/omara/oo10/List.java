package com.example.omara.oo10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class List extends AppCompatActivity {

    public ImageView img;
    CustomAdapter customAdapter;
    private Button c; // Button search
    private EditText e; // Search Box
    private ListView listvew; // Display list
    private ImageView customImage; // Image from custom Layout
    Button bi;
    TextView noresults;


    // ListView Display Arrays
    public ArrayList<Rateable> obj = new ArrayList<Rateable>();
    public String[] arrayRateableNames;
    public String[] arrayImageUris;
    public double[] arrayRates;
    public Rating[] rateing ;
    public boolean[] verified ;


    public ArrayList<String> ratenames = new ArrayList<String>();


    public ArrayAdapter<Rateable> adapter;
    public ArrayList<String> listItems = new ArrayList<String>();

    // Fire base code
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference TagRoot = database.getReference("tags");

    private static String SearchItem = ""; // Item being searched from other pages

    public static void setSI(String s) {
        SearchItem = s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        adapter=new ArrayAdapter<Rateable>(this,android.R.layout.simple_list_item_1,obj);
        c = (Button) findViewById(R.id.button2);
        e = (EditText) findViewById(R.id.editText);
        listvew = (ListView) findViewById(R.id.listView);
        customImage = (ImageView) findViewById(R.id.customImageView);
         bi = (Button) findViewById(R.id.CreateRTB);
        noresults=(TextView) findViewById(R.id.noresults);
        // Search from database
        TagRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (SearchItem != "")
                    listDown(dataSnapshot, SearchItem);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });// End database search


    }



    ArrayList<String> a = new ArrayList<String>();

    public void searchclick(View v) { // Search Method
        // Search from database
        // obj.clear();
        TagRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listDown(dataSnapshot, e.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });// End database search
    }// End search button

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public void goCreate(View view) { // Goes to Create Rateable, called by button
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        //if user is logged in :

        if (user != null) {
           //user logged in
            Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(List.this, CreateRateable.class));
        } else {
            // user not logged in
            Toast.makeText(this, "no user", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(List.this, SignUp.class));
        }
    }
    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

    public void listDown(DataSnapshot dataSnapshot, String searchIt) { // Researches database
        obj.clear();


        boolean found = false;
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            // Get a tag from the database
            String a = data.getKey().toString().toLowerCase().replaceAll("\\s+", "");
            // Get what the user typed
            String b = searchIt.toLowerCase().replaceAll("\\s+", "");

            // If matches
            if (b.equals(a)) {
                found = true;
                /// this shit to add to array so we can access all the tags
                final ArrayList<String> arrayOfMatches = new ArrayList<String>();

                // Add each matched object to the array
                for (DataSnapshot dataMatched : data.getChildren()) {
                    arrayOfMatches.add(dataMatched.getKey());
                }

                // Display each object found
                for (int i = 0; i < arrayOfMatches.size(); i++) {
                    TagRoot.child(a).child(arrayOfMatches.get(i)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Rateable ret = dataSnapshot.getValue(Rateable.class);
                            obj.add(ret);
                            // Creates arrays for display
                            verified=new boolean[obj.size()];
                            rateing=new Rating[obj.size()];
                            arrayRateableNames = new String[obj.size()];
                            arrayImageUris = new String[obj.size()];
                            arrayRates = new double[obj.size()];
                            for (int i = 0; i < obj.size(); i++) {
                                arrayRateableNames[i] = obj.get(i).getName();
                                arrayImageUris[i] = obj.get(i).getPicUrl();
                                arrayRates[i] = obj.get(i).getRating();
                                verified[i]=obj.get(i).getverify();
                                for(int j=0;j<obj.get(i).getRatings().size();j++)
                                    rateing[i]=obj.get(i).getRatings().get(j);
                            }
                            bi.setVisibility(View.GONE);
                            noresults.setVisibility(View.GONE);
                            customAdapter = new CustomAdapter();
                            listvew.setAdapter(customAdapter);
                            //we have to move it to sarchclick after fixing the clicking proplem
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }



            }

        }
        if (!found && searchIt != "") {
            bi.setVisibility(View.VISIBLE);
            noresults.setVisibility(View.VISIBLE);
            listvew.setAdapter(null);

        }

    }// End List down

    public ListView getListView() {
        return listvew;
    }

    class CustomAdapter extends BaseAdapter {
        TextView nameDisplay;
        ImageView imageDisplay;
        RatingBar ratingDisplay;

        public int getCount() {
            return arrayRateableNames.length;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout, null);

            nameDisplay = (TextView) view.findViewById(R.id.customNameDisplay);
            nameDisplay.setText(arrayRateableNames[i]);

            imageDisplay = (ImageView) view.findViewById(R.id.customImageView);
            String pic = arrayImageUris[i];
            Glide.with(getApplicationContext()).load(pic).into(imageDisplay);

            ratingDisplay = (RatingBar) view.findViewById(R.id.customRatingBar);
            ratingDisplay.setRating((float) arrayRates[i]);

            return view;
        }
    }

    public void MagicFunction(View view){
        ListView lv = getListView();
        int position = lv.getPositionForView(view);
        RateablePage.setRequestedObj(obj.get(position));
        startActivity(new Intent(List.this, RateablePage.class));

    }
}

