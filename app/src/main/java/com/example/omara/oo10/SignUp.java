package com.example.omara.oo10;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Created by rkcs on 12/3/2017.
 */

public class SignUp extends AppCompatActivity {
    private TextView password;
   // public FirebaseDatabase myFirebaseRef;
    private TextView email;
    private Button singup;
   // private Button fb;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    com.google.android.gms.common.SignInButton signInButton;
    public GoogleApiClient googleApiClient;
    //LoginButton loginButton;
    public static final int RequestSignInCode = 7;
   // private FirebaseAuth.AuthStateListener mAuthListener;
   String TAG ="Hey";
    private CallbackManager callbackManager;




    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Userroot = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        //https://oo10-507e2.firebaseapp.com/__/auth/handler

        mAuth = FirebaseAuth.getInstance();
        password = (TextView) findViewById(R.id.password);
        email = (TextView) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        singup = (Button) findViewById(R.id.singup);
        signInButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.signin);
        //fb = (Button) findViewById(R.id.button_facebook_sign_in);

        progressBar.setVisibility(View.GONE);
        email.setText("");
        password.setText("");
        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email1 = email.getText().toString().trim();
                String password1 = password.getText().toString().trim();

                if (TextUtils.isEmpty(email1)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password1)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password1.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                mAuth.createUserWithEmailAndPassword(email1, password1)
                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignUp.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    String a = task.getResult().getUser().getEmail();
                                    Userroot.child(task.getResult().getUser().getUid()).child("Email").setValue(a);
                                    startActivity(new Intent(SignUp.this, HomePage.class));



                                    //to proceed to new activity
                                }
                            }
                        });

            }
        });



        //google


        // Creating and Configuring Google Sign In object.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Creating and Configuring Google Api Client.
        googleApiClient = new GoogleApiClient.Builder(SignUp.this)
                .enableAutoManage(SignUp.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        // Adding Click listener to User Sign in Google button.
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserSignInMethod();

            }
        });


        //facebook

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (com.facebook.login.widget.LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                setProfileToView(object);

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });



    }

    //facebook


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }



    private void setProfileToView(JSONObject jsonObject) {
        try {
            String l = jsonObject.getString("email");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Userroot.child(user.getUid()).child("Email").setValue(l);
            } else {
                // No user is signed in
            }



            // facebookName.setText(jsonObject.getString("name"));

           // profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
           // profilePictureView.setProfileId(jsonObject.getString("id"));
           // infoLayout.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //















    // Sign In function Starts From Here.
    public void UserSignInMethod(){

        // Passing Google Api Client into Intent.
        Intent AuthIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

        startActivityForResult(AuthIntent, RequestSignInCode);
    }




    protected void setUpUser() {

        email.setText(email.getText().toString());
        password.setText(password.getText().toString());
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RequestSignInCode){

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()){

                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                FirebaseUserAuth(googleSignInAccount);
            }

        }
    }

    public void FirebaseUserAuth(GoogleSignInAccount googleSignInAccount) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        Toast.makeText(SignUp.this,""+ authCredential.getProvider(),Toast.LENGTH_LONG).show();

        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {

                        if (AuthResultTask.isSuccessful()){

                            // Getting Current Login user details.
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();


                            // Showing the TextView.
                            email.setVisibility(View.VISIBLE);


                            // Setting up name into TextView.


                            // Setting up Email into TextView.
                          String r =  firebaseUser.getEmail().toString();

                            Userroot.child(firebaseUser.getUid()).child("Email").setValue(r);


                        }else {
                            Toast.makeText(SignUp.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }




//google sign up

    //google client server id 958834887465-t213qc5h05haoqrc4rcjhplc88llj27f.apps.googleusercontent.com








}
