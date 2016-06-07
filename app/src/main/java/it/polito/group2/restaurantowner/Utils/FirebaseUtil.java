package it.polito.group2.restaurantowner.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

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

    public static User getCurrentUser() {
        DatabaseReference ref = getCurrentUserRef();
        final User[] user = new User[1];
        if(ref != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user[0] = dataSnapshot.getValue(User.class);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return user[0];
        }
        return null;
    }

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

    public static Offer getOffer(String offerID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebase.getReference("offers/" + offerID);
        final Offer[] offer = new Offer[1];
        if(ref != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    offer[0] = dataSnapshot.getValue(Offer.class);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return offer[0];
        }
        return null;
    }

    public static ArrayList<Meal> getMealsByRestaurant(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        Query ref = firebase.getReference("meals").orderByChild("restaurant_id").equalTo(restaurantID);
        final ArrayList<Meal> meals = new ArrayList<Meal>();
        if(ref != null) {
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    meals.add(dataSnapshot.getValue(Meal.class));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Meal changedMeal = dataSnapshot.getValue(Meal.class);
                    for (Meal m : meals) {
                        if (m.getMeal_id().equals(changedMeal.getMeal_id())) {
                            meals.remove(m);
                            meals.add(changedMeal);
                            break;
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Meal changedMeal = dataSnapshot.getValue(Meal.class);
                    for (Meal m : meals) {
                        if (m.getMeal_id().equals(changedMeal.getMeal_id())) {
                            meals.remove(m);
                            break;
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return meals;
        }
        return null;
    }

    public static ArrayList<Order> getOrdersByRestaurant(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        Query ref = firebase.getReference("orders").orderByChild("restaurant_id").equalTo(restaurantID);
        final ArrayList<Order> orders = new ArrayList<Order>();
        if(ref != null) {
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    orders.add(dataSnapshot.getValue(Order.class));
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Order changedOrder = dataSnapshot.getValue(Order.class);
                    for (Order m : orders) {
                        if (m.getOrder_id().equals(changedOrder.getOrder_id())) {
                            orders.remove(m);
                            orders.add(changedOrder);
                            break;
                        }
                    }
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Order changedOrder = dataSnapshot.getValue(Order.class);
                    for (Order m : orders) {
                        if (m.getOrder_id().equals(changedOrder.getOrder_id())) {
                            orders.remove(m);
                            break;
                        }
                    }
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return orders;
        }
        return null;
    }

    public static ArrayList<Order> getOrdersByUser(String userID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        Query ref = firebase.getReference("orders").orderByChild("user_id").equalTo(userID);
        final ArrayList<Order> orders = new ArrayList<Order>();
        if(ref != null) {
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    orders.add(dataSnapshot.getValue(Order.class));
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Order changedOrder = dataSnapshot.getValue(Order.class);
                    for (Order m : orders) {
                        if (m.getOrder_id().equals(changedOrder.getOrder_id())) {
                            orders.remove(m);
                            orders.add(changedOrder);
                            break;
                        }
                    }
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Order changedOrder = dataSnapshot.getValue(Order.class);
                    for (Order m : orders) {
                        if (m.getOrder_id().equals(changedOrder.getOrder_id())) {
                            orders.remove(m);
                            break;
                        }
                    }
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return orders;
        }
        return null;
    }

    public static ArrayList<Offer> getOffersByRestaurant(String restaurantID) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        Query ref = firebase.getReference("offers").orderByChild("restaurant_id").equalTo(restaurantID);
        final ArrayList<Offer> offers = new ArrayList<Offer>();
        if(ref != null) {
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    offers.add(dataSnapshot.getValue(Offer.class));
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Offer changedOffer = dataSnapshot.getValue(Offer.class);
                    for (Offer m : offers) {
                        if (m.getOfferID().equals(changedOffer.getOfferID())) {
                            offers.remove(m);
                            offers.add(changedOffer);
                            break;
                        }
                    }
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Offer changedOffer = dataSnapshot.getValue(Offer.class);
                    for (Offer m : offers) {
                        if (m.getOfferID().equals(changedOffer.getOfferID())) {
                            offers.remove(m);
                            break;
                        }
                    }
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return offers;
        }
        return null;
    }
}