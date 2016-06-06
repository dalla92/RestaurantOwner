package it.polito.group2.restaurantowner.Utils;

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
import it.polito.group2.restaurantowner.firebasedata.Order;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;

public class FirebaseUtil {

    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
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
}