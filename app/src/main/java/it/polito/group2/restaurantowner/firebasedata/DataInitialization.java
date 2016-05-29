package it.polito.group2.restaurantowner.firebasedata;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by Alessio on 16/05/2016.
 */
public class DataInitialization {

    //first pool of ids
    public String user_id = "fake_user_id";
    public String restaurant_id = "fake_restaurant_id";
    public String meal_id = "fake_meal_id";
    public String favourite_id = "fake_favourite_id";
    public String addition_id = "fake_addition_id";
    public String category_id = "fake_category_id";
    public String order_id = "fake_order_id";
    public String offer_id = "fake_offer_id";
    public String review_id = "fake_review_id";
    public String table_reservation_id = "fake_table_reservation_id";
    //second pool of ids
    public String user_id2 = "fake_user_id2";
    public String restaurant_id2 = "fake_restaurant_id2";
    public String meal_id2 = "fake_meal_id2";
    public String favourite_id2 = "fake_favourite_id2";
    public String addition_id2 = "fake_addition_id2";
    public String category_id2 = "fake_category_id2";
    public String order_id2 = "fake_order_id2";
    public String offer_id2 = "fake_offer_id2";
    public String review_id2 = "fake_review_id2";
    public String table_reservation_id2 = "fake_table_reservation_id2";

    //first pool of element
    public Favourite f;
    public Meal m;
    public MealAddition ma;
    public MealCategory mc;
    public ArrayList<MealAddition> m_a = new ArrayList<MealAddition>();
    public ArrayList<MealCategory> m_c = new ArrayList<MealCategory>();
    public Offer of;
    public Order or;
    public Restaurant res;
    public RestaurantGallery r_g;
    public RestaurantPreview r_p;
    public RestaurantTimeSlot r_t_s;
    public Review rev;
    public TableReservation t;
    public User u;
    //second pool of element
    public Favourite f2;
    public Meal m2;
    public MealAddition ma2;
    public MealCategory mc2;
    public ArrayList<MealAddition> m_a2 = new ArrayList<MealAddition>();
    public ArrayList<MealCategory> m_c2 = new ArrayList<MealCategory>();
    public Offer of2;
    public Order or2;
    public Restaurant res2;
    public RestaurantGallery r_g2;
    public RestaurantPreview r_p2;
    public RestaurantTimeSlot r_t_s2;
    public Review rev2;
    public TableReservation t2;
    public User u2;

    public void init(){
        //meal_addition_fake_initialization();
        //meal_category_fake_initialization();
        //meal_fake_initialization();
        //offer_fake_initialization();
        order_fake_initialization();
        restaurant_time_slot_fake_initialization();
        restaurant_fake_initialization();
        restaurant_gallery_fake_initialization();
        restaurant_preview_fake_initialization();
        favourite_fake_initialization();
        user_fake_initialization();
        table_reservation_fake_initialization();
        review_fake_initialization();

        write_db();

        return;
    }

    public void write_db(){
        DatabaseReference root_ref_0 = FirebaseDatabase.getInstance().getReference(); //Database URL is automatically determined from the google-services.json file you downloaded
        //erase DB, and rewrite all
        root_ref_0.setValue(null);

        //root node
        FirebaseDatabase root_ref = FirebaseDatabase.getInstance();
        /*DatabaseReference favourites_ref = root_ref.getReference("favourites");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference favourite_new = favourites_ref.push();
        favourite_new.setValue(f, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference favourite_new2 = favourites_ref.push();
        favourite_new2.setValue(f2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("favourites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });*/

        //meals node
        DatabaseReference meals_ref  = root_ref.getReference("meals");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference meal_new = meals_ref.push();
        meal_new.setValue(m, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference meal_new2 = meals_ref.push();
        meal_new2.setValue(m2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("meals").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

        //offers node
        DatabaseReference offers_ref  = root_ref.getReference("offers");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference offer_new = offers_ref.push();
        offer_new.setValue(of, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference offer_new2 = offers_ref.push();
        offer_new2.setValue(f2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

        //orders node
        DatabaseReference orders_ref  = root_ref.getReference("orders");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference order_new = orders_ref.push();
        order_new.setValue(or, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference order_new2 = orders_ref.push();
        order_new2.setValue(or2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

        //restaurants node
        DatabaseReference restaurants_ref  = root_ref.getReference("restaurants");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference restaurant_new = restaurants_ref.push();
        res.setRestaurant_id(restaurant_new.getKey());
        restaurant_new.setValue(res, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference restaurant_new2 = restaurants_ref.push();
        res2.setRestaurant_id(restaurant_new.getKey());
        restaurant_new2.setValue(res2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("restaurants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

        //restaurants_galleries node
        DatabaseReference restaurants_galleries_ref = root_ref.getReference("restaurants_galleries");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference restaurants_galleries_new = restaurants_galleries_ref.push();
        restaurants_galleries_new.setValue(r_g, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference restaurants_galleries_new2 = restaurants_galleries_ref.push();
        restaurants_galleries_new2.setValue(r_g2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("restaurants_galleries").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

        //restaurants_previews node
        DatabaseReference restaurants_previews_ref = root_ref.getReference("restaurants_previews");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference restaurants_previews_new = restaurants_previews_ref.push();
        restaurants_previews_new.setValue(r_p, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference restaurants_previews_new2 = restaurants_previews_ref.push();
        restaurants_previews_new2.setValue(r_p2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("restaurants_previews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

        //reviews node
        DatabaseReference reviews_ref  = root_ref.getReference("reviews");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference reviews_new = reviews_ref.push();
        reviews_new.setValue(rev, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference reviews_new2 = reviews_ref.push();
        reviews_new2.setValue(rev2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

        //table_reservations node
        DatabaseReference table_reservations_ref = root_ref.getReference("table_reservations");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference table_reservations_new = table_reservations_ref.push();
        table_reservations_new.setValue(t, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference table_reservations_new2 = table_reservations_ref.push();
        table_reservations_new2.setValue(t2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("table_reservations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

        //users node
        DatabaseReference users_ref  = root_ref.getReference("users");
        //new node: wrap this data as data of an upper child (which will wrap it through a key)
        DatabaseReference users_new = users_ref.push();
        users_new.setValue(u, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        DatabaseReference users_new2 = users_ref.push();
        users_new2.setValue(u2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    Log.d("firebase", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("firebase", "Data saved successfully.");
                }
            }
        });
        //add a listener to activate when there will be new data on server to be changed in the app
        root_ref.getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebase", "Result: " + snapshot.getValue() + "....." + snapshot.getKey());
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("firebase", "The read failed: " + error.getMessage());
            }
        });

    }

    public void meal_addition_fake_initialization() {
        ma = new MealAddition();
        ma.setAdditionSelected(true);
        ma.setMeal_addition_id(meal_id);
        ma.setMeal_addition_price(0.50);
        ma.setMeal_addition_name("Parmigiano");
        m_a.add(ma);

        ma2 = new MealAddition();
        ma2.setAdditionSelected(true);
        ma2.setMeal_addition_id(meal_id2);
        ma2.setMeal_addition_price(1.0);
        ma2.setMeal_addition_name("Pancetta");
        m_a.add(ma2);
    }

    public void meal_category_fake_initialization() {
        mc = new MealCategory();
        mc.setMeal_category_id(category_id);
        mc.setMeal_category_name("Pasta");
        m_c.add(mc);

        mc2 = new MealCategory();
        mc2.setMeal_category_id(category_id2);
        mc2.setMeal_category_name("Spaghetti");
        m_c.add(mc2);
    }

    public void meal_fake_initialization() {
        m = new Meal();
        m.setMeal_name("Pasta con il pomodoro");
        m.setRestaurant_id(restaurant_id);
        m.setMealAvailable(true);
        m.setMealGlutenFree(false);
        m.setMealTakeAway(true);
        m.setMealVegan(false);
        m.setMealVegetarian(false);
        m.setMeal_additions(m_a);
        m.setMeal_tags(m_c);
        m.setMeal_category("Primo");
        m.setMeal_cooking_time(10);
        m.setMeal_description("Salsa rustica");
        m.setMeal_id(meal_id);
        m.setMeal_price(5.0);
        m.setMeal_thumbnail("https://www.flickr.com/photos/142675931@N04/26456332813/in/dateposted-public/");
        m.setMeal_photo_firebase_URL("https://www.flickr.com/photos/142675931@N04/26785604090/in/dateposted-public/");

        m2 = new Meal();
        m2.setMeal_name("Carne arrostita");
        m2.setRestaurant_id(restaurant_id2);
        m2.setMealAvailable(true);
        m2.setMealGlutenFree(false);
        m2.setMealTakeAway(true);
        m2.setMealVegan(false);
        m2.setMealVegetarian(false);
        m2.setMeal_additions(m_a);
        m2.setMeal_tags(m_c);
        m2.setMeal_category("Secondo");
        m2.setMeal_cooking_time(6);
        m2.setMeal_description("Alla brace");
        m2.setMeal_id(meal_id2);
        m2.setMeal_price(4.50);
        m2.setMeal_thumbnail("https://www.flickr.com/photos/142675931@N04/27038081936/in/dateposted-public/");
        m2.setMeal_photo_firebase_URL("https://www.flickr.com/photos/142675931@N04/26976826402/in/dateposted-public/");
    }
/*
    public void offer_fake_initialization() {
        of = new Offer();
        of.setOfferAtDinner(true);
        of.setOfferAtLunch(false);
        of.setOffer_description("Due porzioni di pasta al prezzo di una");
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 5, 25);
        of.setOffer_end_date((GregorianCalendar)cal);
        of.setOffer_id(offer_id);
        of.setOffer_meal_name("Pasta col pomodoro");
        of.setOffer_meal_id(meal_id);
        of.setOffer_name("2x1");
        of.setOffer_meal_photo_firebase_URL("https://www.flickr.com/photos/142675931@N04/26456332813/in/dateposted-public/");
        of.setOffer_meal_thumbnail("https://www.flickr.com/photos/142675931@N04/26785604090/in/dateposted-public/");
        of.setRestaurant_id(restaurant_id);

        of2 = new Offer();
        of2.setOfferAtDinner(true);
        of2.setOfferAtLunch(false);
        of2.setOffer_description("Per ogni porzione di carne arrostita, una bibita in omaggio");
        Calendar cal2 = Calendar.getInstance();
        cal2.set(2016, 5, 25);
        of2.setOffer_end_date((GregorianCalendar)cal2);
        of2.setOffer_id(offer_id2);
        of2.setOffer_meal_name("Carne arrostita");
        of2.setOffer_meal_id(meal_id2);
        of2.setOffer_name("Bibita omaggio");
        of2.setOffer_meal_photo_firebase_URL("https://www.flickr.com/photos/142675931@N04/26976826402/in/dateposted-public/");
        of2.setOffer_meal_thumbnail("https://www.flickr.com/photos/142675931@N04/27038081936/in/dateposted-public/");
        of2.setRestaurant_id(restaurant_id2);
    }
    */

    public void order_fake_initialization() {
        or = new Order();
        or.setUser_id(user_id);
        or.setRestaurant_id(restaurant_id);
        or.setOrder_id(order_id);
        ArrayList<Meal> order_meals = new ArrayList<>();
        order_meals.add(m);
        or.setOrder_meals(order_meals);
        or.setOrder_date(Calendar.getInstance());
        or.setOrder_notes("Portali il prima possibile");
        or.setUser_full_name("Alessando Del Piero");

        or2 = new Order();
        or2.setUser_id(user_id2);
        or2.setRestaurant_id(restaurant_id2);
        or2.setOrder_id(order_id2);
        //ArrayList<Meal> order_meals2 = new ArrayList<>();
        order_meals.add(m2);
        or2.setOrder_meals(order_meals);
        or2.setOrder_date(Calendar.getInstance());
        or2.setOrder_notes("Bella calda, grazie!");
        or2.setUser_full_name("Alessando Del Piero");
    }

    public void restaurant_time_slot_fake_initialization() {
        r_t_s = new RestaurantTimeSlot();
        r_t_s.setRestaurant_id(restaurant_id);
        r_t_s.setClose_dinner_time("23:00");
        r_t_s.setClose_lunch_time("15:00");
        r_t_s.setDay_of_week(0);
        r_t_s.setDinner(true);
        r_t_s.setLunch(true);
        r_t_s.setOpen_dinner_time("19:00");
        r_t_s.setOpen_lunch_time("12:00");

        r_t_s2 = new RestaurantTimeSlot();
        r_t_s2.setRestaurant_id(restaurant_id);
        //r_t_s2.setClose_dinner_time("23:00");
        r_t_s2.setClose_lunch_time("16:00");
        r_t_s2.setDay_of_week(0);
        r_t_s2.setDinner(false);
        r_t_s2.setLunch(true);
        //r_t_s2.setOpen_dinner_time("19:00");
        r_t_s2.setOpen_lunch_time("11:30");
    }

    public void restaurant_fake_initialization() {
        res = new Restaurant();
        res.setUser_id(user_id);
        res.setRestaurant_id(restaurant_id);
        HashMap<String, Boolean> favourites_users = new HashMap<>();
        favourites_users.put("fake_user_id", true);
        favourites_users.put("fake_user_id3", true);
        favourites_users.put("fake_user_id2", true);
        res.setFavouriteUsers(favourites_users);
        res.setAirConditionedPresent(true);
        res.setAnimalAllowed(true);
        res.setCeliacFriendly(true);
        res.setCreditCardAccepted(true);
        res.setFidelityProgramAccepted(true);
        res.setTableReservationAllowed(true);
        res.setTakeAwayAllowed(true);
        res.setTvPresent(false);
        res.setWifiPresent(true);
        res.setRestaurant_address("Via Nazario Sauro 10");
        res.setRestaurant_orders_per_hour(10);
        res.setRestaurant_photo_firebase_URL("https://www.flickr.com/photos/142675931@N04/26785604660/in/dateposted-public/");
        ArrayList<RestaurantTimeSlot> restaurantTimeSlots_list = new ArrayList<>();
        restaurantTimeSlots_list.add(r_t_s);
        res.setRestaurant_time_slot(restaurantTimeSlots_list);
        res.setRestaurant_total_tables_number(100);
        res.setRestaurant_telephone_number("+39980988789890");
        res.setRestaurant_squared_meters(50);
        res.setRestaurant_category("Italian Restaurant");
        res.setRestaurant_closest_bus("Bertola");
        res.setRestaurant_closest_metro("Porta Susa");
        res.setRestaurant_name("Bella Italia");
        res.setRestaurant_rating((float) 3.7);
        res.setRestaurant_price_range(2);
        res.setRestaurant_latitude_position(39.7853889);
        res.setRestaurant_longitude_position(-120.4056973);
        /*
        OR (https://github.com/firebase/geofire-java)
        //Point to the right restaurant location, for example
        GeoFire geoFire = new GeoFire(new DatabaseReference("https://have-break-9713d.firebaseio.com/restaurants/-KHy2d6GPPiNRzG_jFZ9"));
        geoFire.setLocation("firebase-hq", new GeoLocation(37.7853889, -122.4056973), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }
        });
        */

        res2 = new Restaurant();
        res2.setUser_id(user_id2);
        res2.setRestaurant_id(restaurant_id2);
        res2.setAirConditionedPresent(false);
        res2.setAnimalAllowed(false);
        res2.setCeliacFriendly(false);
        res2.setCreditCardAccepted(false);
        res2.setFidelityProgramAccepted(false);
        res2.setTableReservationAllowed(false);
        res2.setTakeAwayAllowed(false);
        res2.setTvPresent(false);
        res2.setWifiPresent(false);
        res2.setRestaurant_address("Piazza Adriano 99/c");
        res2.setRestaurant_orders_per_hour(50);
        res2.setRestaurant_photo_firebase_URL("https://www.flickr.com/photos/142675931@N04/26796728940/in/dateposted-public/");
        ArrayList<RestaurantTimeSlot> restaurantTimeSlots_list2 = new ArrayList<>();
        restaurantTimeSlots_list2.add(r_t_s2);
        res2.setRestaurant_time_slot(restaurantTimeSlots_list2);
        res2.setRestaurant_total_tables_number(50);
        res2.setRestaurant_telephone_number("+397896567578");
        res2.setRestaurant_squared_meters(40);
        res2.setRestaurant_category("Kebab");
        res2.setRestaurant_closest_bus("Firenze Ovest");
        res2.setRestaurant_closest_metro("Vinzaglio");
        res2.setRestaurant_name("Istanbul");
        res2.setRestaurant_rating((float) 4.7);
        res2.setRestaurant_price_range(1);
        res2.setRestaurant_latitude_position(38.7853889);
        res2.setRestaurant_longitude_position(-121.4056973);
    }

    public void restaurant_gallery_fake_initialization() {
        r_g = new RestaurantGallery();
        //r_g.setRestaurant_gallery_image_URL("https://www.flickr.com/photos/142675931@N04/26785604560/in/dateposted-public/");
        r_g.setRestaurant_id(restaurant_id);

        r_g2 = new RestaurantGallery();
        //r_g2.setRestaurant_gallery_image_URL("https://www.flickr.com/photos/142675931@N04/27038201146/in/dateposted-public/");
        r_g2.setRestaurant_id(restaurant_id);
    }

    public void restaurant_preview_fake_initialization() {
        r_p = new RestaurantPreview();
        r_p.setRestaurant_id(restaurant_id);
        r_p.setRestaurant_name("Bella Italia");
        r_p.setRating((float)3.7);
        r_p.setReservations_number(16);
        r_p.setRestaurant_cover_firebase_URL("https://www.flickr.com/photos/142675931@N04/26785604660/in/dateposted-public/");
        r_p.setTables_number(100);

        r_p2 = new RestaurantPreview();
        r_p2.setRestaurant_id(restaurant_id2);
        r_p2.setRestaurant_name("Istanbul");
        r_p2.setRating((float)4.7);
        r_p2.setReservations_number(40);
        r_p2.setRestaurant_cover_firebase_URL("https://www.flickr.com/photos/142675931@N04/26796728940/in/dateposted-public/");
        r_p2.setTables_number(50);
    }

    public void favourite_fake_initialization() {
        f = new Favourite();
        f.setFavourite_id(favourite_id);
        f.setRestaurant_preview(r_p);
        f.setUser_id(user_id);

        f2 = new Favourite();
        f2.setFavourite_id(favourite_id2);
        f2.setRestaurant_preview(r_p2);
        f2.setUser_id(user_id);
    }

    public void user_fake_initialization() {
        u = new User();
        u.setUser_id(user_id);
        u.setUser_full_name("Paolo Parisi");
        HashMap<String, Boolean> favourites_restaurants = new HashMap<>();
        favourites_restaurants.put("fake_user_id", true);
        favourites_restaurants.put("fake_user_id3", true);
        favourites_restaurants.put("fake_user_id2", true);
        u.setFavourites_restaurants(favourites_restaurants);
        u.setOwnerUser(true);
        u.setOwner_vat_number("656456464645");
        u.setUser_email("paolo.parisi@gmail.com");
        u.setUser_fidelity_points(81);
        u.setUser_photo_firebase_URL("https://www.flickr.com/photos/142675931@N04/26456332783/in/dateposted-public/");
        u.setUser_thumbnail("https://www.flickr.com/photos/142675931@N04/26785603890/in/dateposted-public/");
        u.setUser_telephone_number("+3932889238932892892");

        u2 = new User();
        u2.setUser_id(user_id2);
        u2.setUser_full_name("Alessandro Del Piero");
        u2.setOwnerUser(false);
        u2.setOwner_vat_number("");
        u2.setUser_email("ale_the_best@hotmail.it");
        u2.setUser_fidelity_points(800);
        u2.setUser_photo_firebase_URL("https://www.flickr.com/photos/142675931@N04/27038082126/in/dateposted-public/");
        u2.setUser_thumbnail("https://www.flickr.com/photos/142675931@N04/27002842891/in/dateposted-public/");
        u2.setUser_telephone_number("+393434343434");
    }

    public void review_fake_initialization() {
        rev = new Review();
        rev.setUser_id(user_id);
        rev.setRestaurant_id(restaurant_id);
        rev.setReview_comment("Niente di buono");
        rev.setUser_thumbnail("https://www.flickr.com/photos/142675931@N04/26785603890/in/dateposted-public/");
        rev.setUser_full_name("Paolo Parisi");
        rev.setReview_rating((float) 3.3);
        rev.setReview_id(review_id);
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2016, 4, 25);
        rev.setReview_date(cal);

        rev2 = new Review();
        rev2.setUser_id(user_id2);
        rev2.setRestaurant_id(restaurant_id);
        rev2.setReview_comment("Il posto è bello, il servizio un pò meno");
        rev2.setUser_thumbnail("https://www.flickr.com/photos/142675931@N04/27002842891/in/dateposted-public/");
        rev2.setUser_full_name("Alessandro Del Piero");
        rev2.setReview_rating((float)4.3);
        rev2.setReview_id(review_id2);
        GregorianCalendar cal2 = new GregorianCalendar();
        cal2.set(2016, 4, 27);
        rev2.setReview_date(cal2);
    }

    public void table_reservation_fake_initialization() {
        t = new TableReservation();
        t.setUser_id(user_id);
        t.setRestaurant_id(restaurant_id);
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 5, 25);
        t.setTable_reservation_date((GregorianCalendar)cal);
        t.setTable_reservation_guests_number(3);
        t.setTable_reservation_id(table_reservation_id);
        t.setUser_full_name("Paolo Parisi");
        t.setTable_reservation_notes("Sono amico del proprietario");

        t2 = new TableReservation();
        t2.setUser_id(user_id2);
        t2.setRestaurant_id(restaurant_id);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(2016, 5, 26);
        t2.setTable_reservation_date((GregorianCalendar)cal2);
        t2.setTable_reservation_guests_number(13);
        t2.setTable_reservation_id(table_reservation_id2);
        t2.setUser_full_name("Alessandro Del Piero");
        t2.setTable_reservation_notes("Potremmo portare ritardo");
    }


}