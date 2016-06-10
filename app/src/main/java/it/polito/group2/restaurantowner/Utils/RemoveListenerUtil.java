package it.polito.group2.restaurantowner.Utils;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Alessio on 10/06/2016.
 */
public class RemoveListenerUtil {

    public static void remove_value_event_listener(Query q, ValueEventListener l){
        q.removeEventListener(l);
    }

    public static void remove_child_event_listener(Query q, ChildEventListener l){
        q.removeEventListener(l);
    }

}
