package it.polito.group2.restaurantowner.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;

import java.io.IOException;
import java.util.HashMap;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantList;

public class LoginManagerActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private CallbackManager callbackManager;
    //private Firebase firebase;
    private ProgressDialog progressDialog;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*firebase = new Firebase("https://have-break.firebaseio.com/");
        sessionManager = new UserSessionManager(this);
        callbackManager = CallbackManager.Factory.create();

        LoginButton fbButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbButton.setReadPermissions("public_profile", "email");

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = ProgressDialog.show(LoginManagerActivity.this, null, "Loading...", false, false);

                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("login", "Success");

                        onFacebookAccessTokenChange(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("login", "Cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("login", "Error");
                    }

                });
            }
        });
    }

    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            firebase.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                    HashMap<String, String> userData = (HashMap<String, String>) authData.getProviderData().get("cachedUserProfile");
                    String email = userData.get("email");
                    String firstName = userData.get("first_name");
                    String lastName = userData.get("last_name");
                    String gender = userData.get("gender");
                    String userID = authData.getUid();
                    Log.d("prova", email + " " + firstName + " " + lastName + " " + gender + " " + userID);


                    String provider = "facebook";

                    sessionManager.createUserLoginSession(userID, provider);

                    Intent i = new Intent(LoginManagerActivity.this, UserRestaurantActivity.class);

                    i.putExtra("restaurant_id", "-KI35BWFjfamV1gY4l3G";
                    // Closing all the Activities from stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Staring UserRestaurantList Activity
                    startActivity(i);

                    //ending Log In Activity
                    finish();



                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.d("firebaseLogin", "Error");
                }
            });
        } else {

            firebase.unauth();
        }
    }

    private void syncGoogleSignInToken( GoogleSignInAccount googleSignInAccount ){
        AsyncTask<GoogleSignInAccount, Void, String> task = new AsyncTask<GoogleSignInAccount, Void, String>() {
            @Override
            protected String doInBackground(GoogleSignInAccount... params) {
                GoogleSignInAccount gsa = params[0];
                String scope = "oauth2:profile email";
                String token = null;

                try {
                    token = GoogleAuthUtil.getToken(LoginManagerActivity.this, gsa.getEmail(), scope);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                super.onPostExecute(token);

                if( token != null ){
                    accessGoogleLoginData( token );
                }
                else{
                    showSnackbar("Google login falhou, tente novamente");
                }
            }
        };

        task.execute(googleSignInAccount);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == RC_SIGN_IN_GOOGLE && resultCode == RESULT_OK ){

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent( data );
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();

            syncGoogleSignInToken( account );
        }
        else{
            callbackManager.onActivityResult( requestCode, resultCode, data );
        }
    }*/

    }
}
