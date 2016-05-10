package it.polito.group2.restaurantowner.user.takeaway;

import android.graphics.Bitmap;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.data.Meal;

/**
 * Created by Filippo on 02/05/2016.
 */
public class ReservationMeal {
    private String id;
    private String reservationID;
    private String mealID;
    private int quantity;

    private Meal meal;

    private ArrayList<ReservationMealAddition> additionList = new ArrayList<ReservationMealAddition>();
}
