package it.polito.group2.restaurantowner.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class LoginManagerActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;
    private UserSessionManager sessionManager;
    private TextInputLayout inputLayoutPassword, inputLayoutEmail;
    private EditText inputEmail, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseAuth.getInstance().signOut();

        sessionManager = new UserSessionManager(this);
        callbackManager = CallbackManager.Factory.create();

        LoginButton fbButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbButton.setReadPermissions("public_profile", "email");
        fbButton.setOnClickListener(this);

        SignInButton googleBtn = (SignInButton) findViewById(R.id.google_login_button);
        googleBtn.setOnClickListener(this);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        //adding TextWatcher
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        findViewById(R.id.login_btn).setOnClickListener(this);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("51105168084-mt7a75l4aep1v8chjvkdo7i9rldbsiak.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String fullName = user.getDisplayName();
                    String email = user.getEmail();
                    String userID = user.getUid();
                    String providerId = null;
                    for (UserInfo profile : user.getProviderData()) {
                        // Id of the provider (ex: google.com)
                        providerId = profile.getProviderId();
                    }

                    if(providerId == null)
                        providerId = "password";

                    sessionManager.createUserLoginSession(userID, providerId);

                    Intent i = new Intent(LoginManagerActivity.this, UserRestaurantActivity.class);

                    i.putExtra("restaurant_id", "-KI35BWFjfamV1gY4l3G");

                    // Closing all the Activities from stack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Staring UserRestaurantList Activity
                    startActivity(i);

                    //ending Log In Activity
                    finish();

                    Log.d("prova", fullName + " " + email + " " + userID + " " + providerId);
                } else {
                    // User is signed out
                    Log.d("state", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void handleAuthToken(String provider, String accessToken) {
        if(provider.equals("facebook"))
            firebaseAuthWithFacebook(accessToken);
        if(provider.equals("google"))
            firebaseAuthWithGoogle(accessToken);
    }

    private void firebaseAuthWithGoogle(String accessToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(accessToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        hideProgressDialog();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginManagerActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithFacebook(String token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginManagerActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void signInWithPassword(){
        if (!validateEmail() || !validatePassword()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginManagerActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleAuthToken("facebook", loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.login_err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.login_err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /*private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        Toast.makeText(LoginManagerActivity.this, "LogOut", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                handleAuthToken("google", account.getIdToken());
            } else {
                // Google Sign In failed
                hideProgressDialog();
                Toast.makeText(LoginManagerActivity.this, "LogIn Failed result, Try again!", Toast.LENGTH_SHORT).show();
            }
        }
        else
            callbackManager.onActivityResult( requestCode, resultCode, data );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        hideProgressDialog();
        Toast.makeText(LoginManagerActivity.this, "LogIn Failed, Try again!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fb_login_button:
                showProgressDialog();
                signInWithFacebook();
                break;

            case R.id.google_login_button:
                showProgressDialog();
                signInWithGoogle();
                break;

            case R.id.login_btn:
                showProgressDialog();
                signInWithPassword();

        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }


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
