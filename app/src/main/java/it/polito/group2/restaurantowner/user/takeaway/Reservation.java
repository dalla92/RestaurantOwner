package it.polito.group2.restaurantowner.user.takeaway;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Filippo on 02/05/2016.
 */
public class Reservation {
    private String id;
    private String restaurantID;
    private String username;
    private Calendar time;
    private String note;

    private ArrayList<ReservationMeal> mealList = new ArrayList<ReservationMeal>();

}
