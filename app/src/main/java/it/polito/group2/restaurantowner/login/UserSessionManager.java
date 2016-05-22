package it.polito.group2.restaurantowner.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class UserSessionManager {

    // Shared Preferences reference
    private SharedPreferences pref;

    // Editor reference for Shared preferences
    private Editor editor;

    // All Shared Preferences Keys
    private final String IS_USER_LOGIN = "IsUserLoggedIn";

    // User ID
    public static final String USER_ID = "userId";

    //provider used to logIn: google, fb, classic (logIn with email and password)
    public static final String PROVIDER = "provider";

    // Constructor
    public UserSessionManager(Context context){
        pref = context.getSharedPreferences( "SessionPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
    }

    //Create login session
    public void createUserLoginSession(String userID, String provider){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing userID and provider in pref
        editor.putString(USER_ID, userID);
        editor.putString(PROVIDER, provider);

        // commit changes
        editor.apply();
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getSessionData(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<>();

        // user id
        user.put(USER_ID, pref.getString(USER_ID, null));

        // provider used to log in
        user.put(PROVIDER, pref.getString(PROVIDER, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        /*String provider = pref.getString(PROVIDER, null);
        if(provider != null) {
            if (provider.equals("facebook"))
                LoginManager.getInstance().logOut();

            if(provider.equals("google.com"))

        }*/

        FirebaseAuth.getInstance().signOut();

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.apply();
    }


    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
