package it.polito.group2.restaurantowner.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.Order;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.login.LoginManagerActivity;

public class FirebaseUtil {

    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static ProgressDialog initProgressDialog(Context context){
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        //decomment when test are finish
            /*mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);*/

        return mProgressDialog;
    }

    public static void showProgressDialog(ProgressDialog mProgressDialog) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public static void hideProgressDialog(ProgressDialog mProgressDialog) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public static void showLoginDialog(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getResources().getString(R.string.want_to_login));
        alert.setMessage(context.getResources().getString(R.string.need_login));

        alert.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, LoginManagerActivity.class);
                intent.putExtra("login", true);
                context.startActivity(intent);
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public static DatabaseReference getCurrentUserRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("users").child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getRestaurantRef(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("restaurants/" + restaurantID);
    }

    public static DatabaseReference getMealsRef(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("meals/" + restaurantID);
    }

    public static DatabaseReference getOffersRef(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("offers/" + restaurantID);
    }

    public static DatabaseReference getOfferRef(String restaurantID, String offerID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("offers/" + restaurantID + "/" + offerID);
    }

    public static Query getOrdersByRestaurantRef(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("orders").orderByChild("restaurant_id").equalTo(restaurantID);
    }

    public static Query getReservationsByRestaurantRef(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("reservations").orderByChild("restaurant_id").equalTo(restaurantID);
    }

    public static Query getOrdersByUserRef(String userID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("orders").orderByChild("user_id").equalTo(userID);
    }

    public static DatabaseReference getOrdersRef() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("orders");
    }

}