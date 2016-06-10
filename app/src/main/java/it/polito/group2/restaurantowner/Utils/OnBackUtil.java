package it.polito.group2.restaurantowner.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

/**
 * Created by Alessio on 10/06/2016.
 */
public class OnBackUtil {

    public static void clean_stack_and_go_to_main_activity(Context c) {
        Intent intent = new Intent(c, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        c.startActivity(intent);
    }

    public static void clean_stack_and_go_to_user_restaurant_list(Context c) {
        Intent intent = new Intent(c, UserRestaurantList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        c.startActivity(intent);
    }

    public static void clean_stack_and_exit_application(Context c, Activity a) {
        Intent intent = new Intent(c, UserRestaurantList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        c.startActivity(intent);
        a.finish();
    }
}
