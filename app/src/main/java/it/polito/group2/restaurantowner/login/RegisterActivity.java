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
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private TextInputLayout inputLayoutFirstName, inputLayoutLastName, inputLayoutPassword, inputLayoutConfirmPassword, inputLayoutEmail;
    private EditText inputFirstName, inputLastName, inputPassword, inputConfirmPassword, inputEmail, inputPhoneNumber;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference userRef;
    private String userID;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        firebase = FirebaseDatabase.getInstance();
        callbackManager = CallbackManager.Factory.create();
        userRef = firebase.getReference("users");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("51105168084-mt7a75l4aep1v8chjvkdo7i9rldbsiak.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        inputLayoutFirstName = (TextInputLayout) findViewById(R.id.input_layout_first_name);
        inputLayoutLastName = (TextInputLayout) findViewById(R.id.input_layout_last_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);

        inputFirstName = (EditText) findViewById(R.id.input_first_name);
        inputLastName = (EditText) findViewById(R.id.input_last_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPhoneNumber = (EditText) findViewById(R.id.input_phone_number);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        //adding TextWatcher

        inputFirstName.addTextChangedListener(new MyTextWatcher(inputFirstName));
        inputLastName.addTextChangedListener(new MyTextWatcher(inputLastName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputConfirmPassword.addTextChangedListener(new MyTextWatcher(inputConfirmPassword));

        Button registerBtn = (Button) findViewById(R.id.register_btn);
        assert registerBtn != null;
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFirstName() && validateLastName() && validateEmail() &&
                        validatePassword() && validateConfirmPassword()) {

                    showProgressDialog();
                    final String email = inputEmail.getText().toString().trim();
                    final String password = inputConfirmPassword.getText().toString().trim();
                    final String fullName = inputFirstName.getText().toString().trim() + " " + inputLastName.getText().toString().trim();
                    final String phoneNumber = inputPhoneNumber.getText().toString().trim();

                    Query userQuery = userRef.orderByChild("user_email").equalTo(email);
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChildren()) {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                User user = new User(authResult.getUser().getUid(), fullName, phoneNumber, email);
                                                user.getProviders().put("password", true);
                                                userRef.child(authResult.getUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        hideProgressDialog();
                                                        Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                                                        signOut();

                                                        Intent i = new Intent(RegisterActivity.this, LoginManagerActivity.class);

                                                        i.putExtra("login", true);

                                                        // Closing all the Activities from stack
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                        // Add new Flag to start new Activity
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                        // Staring UserRestaurantList Activity
                                                        startActivity(i);
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                hideProgressDialog();
                                                e.printStackTrace();
                                                Toast.makeText(RegisterActivity.this, "Registration failed, try again!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                User target = null;
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    target = data.getValue(User.class);
                                }
                                Log.d("prova", target.getUser_email() + " " + target.getUser_full_name() + " " + target.getUser_id() + " " + target.getProviders());
                                userID = target.getUser_id();
                                target.setUser_telephone_number(inputPhoneNumber.getText().toString().trim());
                                HashMap<String, Boolean> providers = target.getProviders();
                                if (!providers.containsKey("password")) {

                                    providers.put("password", true);
                                    userRef.child(target.getUser_id()).setValue(target);
                                    if(providers.containsKey("google")){
                                        signInWithGoogle();
                                    }
                                    if (providers.containsKey("facebook")){
                                        signInWithFacebook();
                                    }
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, "This account already exists!", Toast.LENGTH_SHORT).show();
                                    hideProgressDialog();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("prova", "check user calcelled!");
                        }
                    });

                    /*mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                            Toast.makeText(RegisterActivity.this, "This email is already registered!", Toast.LENGTH_SHORT).show();
                                            hideProgressDialog();
                                        }
                                        else {
                                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                            showProgressDialog();
                                        }
                                    }

                                }
                            });*/

                }
            }
        });
    }


   /* @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }*/

    private boolean validateFirstName() {
        if (inputFirstName.getText().toString().trim().isEmpty()) {
            inputLayoutFirstName.setError(getString(R.string.err_msg_first_name));
            requestFocus(inputFirstName);
            return false;
        } else {
            inputLayoutFirstName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLastName() {
        if (inputLastName.getText().toString().trim().isEmpty()) {
            inputLayoutLastName.setError(getString(R.string.err_msg_last_name));
            requestFocus(inputLastName);
            return false;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }

        return true;
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
        String password = inputPassword.getText().toString().trim();

        if (password.isEmpty() || password.length() < 6) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword() {
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_confirm_password));
            requestFocus(inputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
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

    private void signInWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("prova", "signInWithFacebookSuccess");
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
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    private void signInWithGoogle() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmation!");
        alert.setMessage("You have already logged in with Google using this email," +
                "\nIt's not possible to have more then one account with the same email." +
                "Click yes to use your google account and merge the two account to access the same data.");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                userRef.child(userID + "/providers/password").setValue(null);
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void handleAuthToken(String provider, String accessToken) {
        if (provider.equals("facebook")) {
            Log.d("prova", "handleAuthTokenFacebook");
            firebaseAuthWithFacebook(accessToken);
        }
        if (provider.equals("google")) {
            firebaseAuthWithGoogle(accessToken);
        }
    }

    private void firebaseAuthWithFacebook(String token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        Log.d("prova", "firebaseAuthWithFacebook Success");
                        handleFirebaseAuthResult(result);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.d("prova", "auth:onFailureFacebook:" + e.getMessage());
                        handleFirebaseAuthResult(null);
                    }
                });

    }

    private void firebaseAuthWithGoogle(String accessToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(accessToken, null);
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
                        Log.d("prova", "auth:onFailure:" + e.getMessage());
                        handleFirebaseAuthResult(null);
                    }
                });
    }

    private void handleFirebaseAuthResult(AuthResult result) {
        if (result != null) {
            final FirebaseUser user = result.getUser();
            if(user.getDisplayName() != null) {
                if (!user.getDisplayName().equals(inputFirstName.getText().toString().trim() + " " + inputLastName.getText().toString().trim())) {
                    Log.d("prova", "different name");
                    signOut();
                    userRef.child(user.getUid() + "/providers/password").setValue(null);
                    Toast.makeText(RegisterActivity.this, "This email is already register with Google or Facebook with a different name," +
                            "\ntry again with the correct First name and Last name to merge the account data!", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            AuthCredential credential = EmailAuthProvider.getCredential(inputEmail.getText().toString().trim(), inputConfirmPassword.getText().toString().trim());
            user.linkWithCredential(credential)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                            signOut();

                            Intent i = new Intent(RegisterActivity.this, LoginManagerActivity.class);

                            i.putExtra("login", true);

                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            // Staring UserRestaurantList Activity
                            startActivity(i);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            signOut();
                            userRef.child(user.getUid() + "/providers/password").setValue(null);
                            Toast.makeText(RegisterActivity.this, "Registration failed in link.", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            userRef.child(userID + "/providers/password").setValue(null);
            Toast.makeText(this, "Registration failed result = null.", Toast.LENGTH_SHORT).show();
        }
    }

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
                userRef.child(userID + "/providers/password").setValue(null);
                hideProgressDialog();
                Toast.makeText(RegisterActivity.this, "Error during registration, Try again!", Toast.LENGTH_SHORT).show();
            }
        }
        else
            callbackManager.onActivityResult( requestCode, resultCode, data );
    }

    private void signOut() {
        if(mGoogleApiClient.isConnected())
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        hideProgressDialog();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        hideProgressDialog();
        Toast.makeText(RegisterActivity.this, "Fatal error,try again!", Toast.LENGTH_SHORT).show();
        finish();
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
                case R.id.input_confirm_password:
                    validateConfirmPassword();
                    break;
                case R.id.input_first_name:
                    validateFirstName();
                    break;
                case R.id.input_last_name:
                    validateLastName();
                    break;
            }
        }
    }

}
