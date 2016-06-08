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
        alert.setTitle("Do you want to log in?");
        alert.setMessage("You must be logged in to perform this action");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, LoginManagerActivity.class);
                intent.putExtra("login", true);
                context.startActivity(intent);
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    public static DatabaseReference getOfferRef(String offerID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("offers/" + offerID);
    }

    public static Query getMealsByRestaurantRef(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        return firebase.getReference("meals").orderByChild("restaurant_id").equalTo(restaurantID);
    }










    //TODO deprecata
    @Deprecated
    public static User getCurrentUser() {
        return null;
    }

    //TODO deprecata
    @Deprecated
    public static Restaurant getRestaurant(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebase.getReference("restaurants/" + restaurantID);
        final Restaurant[] restaurant = new Restaurant[1];
        if(ref != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    restaurant[0] = dataSnapshot.getValue(Restaurant.class);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return restaurant[0];
        }
        return null;
    }

    //TODO deprecata
    @Deprecated
    public static Offer getOffer(String offerID) {
        return null;
    }

    //TODO deprecata
    @Deprecated
    public static ArrayList<Meal> getMealsByRestaurant(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        Query ref = firebase.getReference("meals").orderByChild("restaurant_id").equalTo(restaurantID);
        final ArrayList<Meal> meals = new ArrayList<Meal>();
        if(ref != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        meals.add(d.getValue(Meal.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return meals;
        }
        return null;
    }

    //TODO deprecata
    @Deprecated
    public static ArrayList<Order> getOrdersByRestaurant(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        Query ref = firebase.getReference("orders").orderByChild("restaurant_id").equalTo(restaurantID);
        final ArrayList<Order> orders = new ArrayList<Order>();
        if(ref != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        orders.add(d.getValue(Order.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return orders;
        }
        return null;
    }

    //TODO deprecata
    @Deprecated
    public static ArrayList<Order> getOrdersByUser(String userID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        Query ref = firebase.getReference("orders").orderByChild("user_id").equalTo(userID);
        final ArrayList<Order> orders = new ArrayList<Order>();
        if(ref != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        orders.add(d.getValue(Order.class));
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return orders;
        }
        return null;
    }

    //TODO deprecata
    @Deprecated
    public static ArrayList<Offer> getOffersByRestaurant(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        Query ref = firebase.getReference("offers").orderByChild("restaurant_id").equalTo(restaurantID);
        final ArrayList<Offer> offers = new ArrayList<Offer>();
        if(ref != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        offers.add(d.getValue(Offer.class));
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return offers;
        }
        return null;
    }
}