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
import android.widget.CheckBox;
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

public class RegisterActivity extends AppCompatActivity{

    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private TextInputLayout inputLayoutFirstName, inputLayoutLastName, inputLayoutPassword, inputLayoutConfirmPassword, inputLayoutEmail;
    private EditText inputFirstName, inputLastName, inputPassword, inputConfirmPassword, inputEmail, inputPhoneNumber;
    private CheckBox isOwnerCheckBox;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
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
        isOwnerCheckBox = (CheckBox) findViewById(R.id.is_owner);
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

                    if (!validateTelephoneNumber()) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                RegisterActivity.this);
                        // set title
                        alertDialogBuilder.setTitle(getResources().getString(R.string.missing_telephone_number));
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(getResources().getString(R.string.continue_missing_telephone_number))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.register_anyway), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        continue_registration();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    } else {
                        continue_registration();
                    }
                }
            }
        });
    }

    public boolean validateTelephoneNumber(){
        if(inputPhoneNumber.getText()==null || inputPhoneNumber.getText().toString().trim().equals(""))
            return false;
        return true;
    }

    public void continue_registration(){
        showProgressDialog();
        final String email = inputEmail.getText().toString().trim();
        final String password = inputConfirmPassword.getText().toString().trim();
        final String fullName = inputFirstName.getText().toString().trim() + " " + inputLastName.getText().toString().trim();
        final String phoneNumber = inputPhoneNumber.getText().toString().trim();
        final boolean isOwner = isOwnerCheckBox.isChecked();

        Log.d("prova", ""+isOwner);

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
                                    user.setOwnerUser(isOwner);
                                    user.getProviders().put("password", true);
                                    userRef.child(authResult.getUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            hideProgressDialog();
                                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();

                                            signOut();

                                            Intent i = new Intent(RegisterActivity.this, LoginManagerActivity.class);

                                            i.putExtra("login", true);

                                            // Closing all the Activities from stack
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                            // Add new Flag to start new Activity
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                            // Staring UserRestaurantList Activity
                                            startActivity(i);
                                            finish();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    hideProgressDialog();
                                    e.printStackTrace();
                                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
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
                        if (providers.containsKey("facebook")){
                            signInWithFacebook();
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.account_already_existing), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("prova", "check user calcelled!");
            }
        });
    }

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


    private void handleAuthToken(String provider, String accessToken) {
        if (provider.equals("facebook")) {
            Log.d("prova", "handleAuthTokenFacebook");
            firebaseAuthWithFacebook(accessToken);
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

    private void handleFirebaseAuthResult(AuthResult result) {
        if (result != null) {
            final FirebaseUser user = result.getUser();
            if(user.getDisplayName() != null) {
                if (!user.getDisplayName().equals(inputFirstName.getText().toString().trim() + " " + inputLastName.getText().toString().trim())) {
                    Log.d("prova", "different name");
                    signOut();
                    userRef.child(user.getUid() + "/providers/password").setValue(null);
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.already_registered_with_facebook), Toast.LENGTH_LONG).show();
                    return;
                }
            }

            AuthCredential credential = EmailAuthProvider.getCredential(inputEmail.getText().toString().trim(), inputConfirmPassword.getText().toString().trim());
            user.linkWithCredential(credential)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();

                            userRef.child(user.getUid() + "/ownerUser").setValue(isOwnerCheckBox.isChecked());
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
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.account_linking_failed), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            userRef.child(userID + "/providers/password").setValue(null);
            Toast.makeText(this, getResources().getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult( requestCode, resultCode, data );
    }

    private void signOut() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        hideProgressDialog();
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
