package it.polito.group2.restaurantowner.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class LoginManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private TextInputLayout inputLayoutPassword, inputLayoutEmail;
    private EditText inputEmail, inputPassword;
    private String fbEmail;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean logIn = getIntent().getBooleanExtra("login", false);
        if(!logIn){
            signOut();
            Intent intent = new Intent(getApplicationContext(), UserRestaurantList.class);

            // Closing all the Activities from stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring UserRestaurantList Activity
            startActivity(intent);

            //ending Log In Activity
            finish();
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //sessionManager = new UserSessionManager(this);
        callbackManager = CallbackManager.Factory.create();
        firebase = FirebaseDatabase.getInstance();
        userRef = firebase.getReference("users");

        LoginButton fbButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        fbButton.setOnClickListener(this);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        //adding TextWatcher
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        findViewById(R.id.login_btn).setOnClickListener(this);

        findViewById(R.id.register_link).setOnClickListener(this);
        findViewById(R.id.forgot_password_link).setOnClickListener(this);



        mAuth = FirebaseAuth.getInstance();
    }

    private void handleAuthToken(String provider, String accessToken) {
        if (provider.equals("facebook")) {
            firebaseAuthWithFacebook(accessToken);
        }
        if(provider.equals("password")){
            firebaseAuthWithPassword();
        }
    }

    private void firebaseAuthWithPassword() {
        showProgressDialog();
        AuthCredential credential = EmailAuthProvider.getCredential(inputEmail.getText().toString().trim(), inputPassword.getText().toString().trim());
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        handleFirebaseAuthResult(result);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.d("prova", "auth:onFailure:" + e.getMessage());
                        handleFirebaseAuthResult(null);
                    }
                });
    }

    private void handleFirebaseAuthResult(AuthResult result) {
        if (result != null) {
            Log.d("prova", "handleFirebaseAuthResult:SUCCESS");
            FirebaseUser user = result.getUser();
            final String fullName = user.getDisplayName();
            final String email = user.getEmail();
            final String userID = user.getUid();
            String providerId = null;
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                providerId = profile.getProviderId();
            }
            if(providerId == null)
                providerId = "password";
            if(providerId.equals("facebook.com"))
                providerId = "facebook";

            final String targetProvider = providerId;

            Log.d("prova", targetProvider);

            final DatabaseReference userRef = firebase.getReference("users");
            final Query userQuery = userRef.orderByChild("user_email").equalTo(email);
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChildren()) {
                        Log.d("prova", fullName + " " + email + " " + userID);

                        User user = new User(userID, fullName, "", email);
                        user.getProviders().put(targetProvider, true);
                        userRef.child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideProgressDialog();
                                Intent i = new Intent(LoginManagerActivity.this, UserRestaurantList.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        });

                    } else {
                        User target = null;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            target = data.getValue(User.class);
                        }
                        /*HashMap<String, Boolean> providers = target.getProviders();
                        if(!providers.containsKey(targetProvider)){
                            providers.put(targetProvider, true);
                        }*/
                        userRef.child(target.getUser_id()).child("providers/" + targetProvider).setValue(true);

                        hideProgressDialog();
                        Intent i = new Intent(LoginManagerActivity.this, UserRestaurantList.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("prova", "check user calcelled!");
                }
            });
        } else {
            hideProgressDialog();
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }


    private void firebaseAuthWithFacebook(final String token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        handleFirebaseAuthResult(result);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            checkAndMerge(token);
                        } else {
                            e.printStackTrace();
                            Log.d("prova", "auth:onFailureFacebook:" + e.getMessage());
                            handleFirebaseAuthResult(null);
                        }
                    }
                });

    }


    private void checkAndMerge(final String token) {
        Log.d("prova", ""+ fbEmail);
        Query userQuery = userRef.orderByChild("user_email").equalTo(fbEmail);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User target = null;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    target = data.getValue(User.class);
                }
                assert target != null;
                final HashMap<String, Boolean> providers = target.getProviders();

                AlertDialog.Builder alert = new AlertDialog.Builder(LoginManagerActivity.this);
                alert.setTitle("Confirmation!");
                alert.setMessage("You have already an account with email: " + fbEmail +
                        "\nClick Yes to link your account and access your data or No to log in with another method.");

                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (providers.containsKey("password")) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginManagerActivity.this);
                            alert.setTitle("Insert your password!");

                            final EditText input = new EditText(LoginManagerActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            alert.setView(input);

                            alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String password = input.getText().toString();
                                    mergeWithPassword(password, token);
                                }
                            });
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hideProgressDialog();
                                    Log.d("prova", "input password cancelled");
                                    Toast.makeText(LoginManagerActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                        }

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideProgressDialog();
                        Log.d("prova", "link account refused");
                        Toast.makeText(LoginManagerActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                alert.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                Toast.makeText(LoginManagerActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mergeWithPassword(String password, final String token) {
        AuthCredential credential = EmailAuthProvider.getCredential(fbEmail, password);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        FirebaseUser user = result.getUser();
                        AuthCredential credential = FacebookAuthProvider.getCredential(token);
                        user.linkWithCredential(credential)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        handleFirebaseAuthResult(authResult);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginManagerActivity.this, "Account linking failed", Toast.LENGTH_SHORT).show();
                                        signOut();
                                    }
                                });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException)
                            Toast.makeText(LoginManagerActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                        else {
                            e.printStackTrace();
                            Log.d("prova", "auth:onFailure:" + e.getMessage());
                            Toast.makeText(LoginManagerActivity.this, "Error during login!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

        handleAuthToken("password", null);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestFacebookUserProfile(loginResult);
                handleAuthToken("facebook", loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Log.d("prova", "facebook login cancelled!");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("prova", error.toString());
            }
        });
    }

    public void requestFacebookUserProfile(LoginResult loginResult){
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            Log.e("prova", response.getError().toString());
                        } else {
                            try {
                                fbEmail = object.getString("email");
                                Log.d("prova", fbEmail);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
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

    private void signOut() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fb_login_button:
                showProgressDialog();
                signInWithFacebook();
                break;

            case R.id.login_btn:
                showProgressDialog();
                signInWithPassword();
                break;

            case R.id.register_link:
                Intent i = new Intent(LoginManagerActivity.this, RegisterActivity.class);
                startActivity(i);
                break;

            case R.id.forgot_password_link:
                sendPasswordResetEmail();
                break;
        }
    }

    private void sendPasswordResetEmail() {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginManagerActivity.this);
        alert.setTitle("Insert email");

        final EditText input = new EditText(LoginManagerActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        alert.setView(input);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailAddress = input.getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginManagerActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(e instanceof FirebaseAuthInvalidUserException)
                                    Toast.makeText(LoginManagerActivity.this, "No account exists with this email", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
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

}
