package com.example.omara.oo10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;

public class CreateRateable extends AppCompatActivity {
    String spiner;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference TagRoot = database.getReference("tags");

    private EditText name;
    private RatingBar simpleRatingBar;
    private EditText description;
    private EditText tags;
    private ImageView img;
    private Uri selectedimage;
    private StorageReference imageRef;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private  ProgressDialog progressDialog;
    private  UploadTask uploadTask;
    private String url;
    private ImageView imgtest;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createrateable);

        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();

        img = findViewById(R.id.imageViewTest);
        spinnercall();
        buttoncall();
   }

//button call
    public void buttoncall() {
        Button submit = (Button) findViewById(R.id.submit);
        name = (EditText) findViewById(R.id.editTextname);
        description = (EditText) findViewById(R.id.descrption);
        simpleRatingBar = (RatingBar) findViewById(R.id.rating); // initiate a rating bar
        tags = (EditText) findViewById(R.id.tags);
        final TextView ten10 = (TextView) findViewById(R.id.ten10);

        simpleRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int x = (int) (v * 2);
                ten10.setText(String.valueOf((x)));
            }
        });
    }
         //end



    //submit button request
    public void SubmitRtb(View view) {
            // Toast.makeText(getBaseContext(),Toast.LENGTH_LONG).show();
            uploadImage();
            Picasso.with(CreateRateable.this).load(url).fit().centerCrop().into(img);
            // Create Rateable
    }



   //category chosing

    public void spinnercall(){
        final Spinner spinner = (Spinner) findViewById(R.id.spinnercotogory);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.catogray_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getBaseContext(),adapterView.getSelectedItem()+"",Toast.LENGTH_LONG).show();
                spiner= String.valueOf(adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }// Ends Spinner Code

    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedimage = imageReturnedIntent.getData();
                    //uploadImage();

                }
        }
    }

    public void uploadImage() {
        //create reference to images folder and assing a name to the file that will be uploaded
        imageRef = mStorageRef.child("images/"+selectedimage.getLastPathSegment());
        //creating and showing progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        //starting upload
        uploadTask = imageRef.putFile(selectedimage);
        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //sets and increments value of progressbar
                progressDialog.incrementProgressBy((int) progress);
            }
        });
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
               Toast.makeText(CreateRateable.this,"Error in uploading!",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(CreateRateable.this,"Upload successful",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                //showing the uploaded image in ImageView using the download url
                Picasso.with(CreateRateable.this).load(downloadUrl).into(img);
               // url = downloadUrl;

                url = downloadUrl.toString();
                Toast.makeText(CreateRateable.this,url,Toast.LENGTH_SHORT).show();
                //Name
                finish();

            }
        });
    }

    public void finish(){
        // Name of the object to be displayed
        String rtbName = name.getText().toString();
        // Name of the object in the database
        String rtbTagName = name.getText().toString().toLowerCase().replaceAll("\\s+","");
        // Out Of Ten
        double rt = simpleRatingBar.getRating()*2;
        Rating rtboo10 = new Rating("",rtbTagName,rt);
        // Decription
        String rtbDescription =description.getText().toString();
        // Used ID
        String rtbUserId = "";
        // Extra Tags
        String[] tages = tags.getText().toString().split(",");
        for(int i = 0; i < tages.length; i++){
            tages[i] = tages[i].toLowerCase().replaceAll("\\s+","");
        }
        // Create Rateable
        Rateable rtb = new Rateable(rtbTagName,rtbName,rtboo10,rtbDescription,TagRoot.child(spiner).getKey(),rtbUserId,tages,url);

        // Create tag with the name of the category
        TagRoot.child(spiner.toLowerCase().replaceAll("\\s+","")).child(rtbTagName).setValue(rtb);
        // Create tag with the name of the rateable
        TagRoot.child(rtbTagName.toLowerCase().replaceAll("\\s+","")).child(rtbTagName).setValue(rtb);
        // Create tags for each tag
        for(int i = 0; i < tages.length;i++){
            TagRoot.child(tages[i].toLowerCase().replaceAll("\\s+","")).child(rtbTagName).setValue(rtb);
        }
        startActivity(new Intent(CreateRateable.this, RateablePage.class));
    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
//            Uri targetUri = data.getData();
//            Bitmap bitmap;
//            try {
//                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
//                img.setImageBitmap(bitmap);
//
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//    }



}

