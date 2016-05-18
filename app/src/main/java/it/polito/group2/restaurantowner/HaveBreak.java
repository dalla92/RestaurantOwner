package it.polito.group2.restaurantowner;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;

import it.polito.group2.restaurantowner.firebasedata.DataInitialization;

public class HaveBreak extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //initialize data
        DataInitialization d = new DataInitialization();
        d.init();

    }
}