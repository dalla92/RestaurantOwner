package it.polito.group2.restaurantowner.owner;

import java.util.ArrayList;
import java.util.Calendar;

public class TakeAwayReservation {
    private String username, notes, restaurantId, takeAwayReservationId;
    private ArrayList<OrderedMeal> ordered_meals;
    private Calendar date;

    public TakeAwayReservation(String username, ArrayList<OrderedMeal> ordered_meals, Calendar date, String notes, String restaurantId, String takeAwayReservationId) {
        this.username = username;
        this.notes = notes;
        this.ordered_meals = new ArrayList<>();
        this.ordered_meals.addAll(ordered_meals);
        this.date = date;
        this.restaurantId = restaurantId;
        this.takeAwayReservationId = takeAwayReservationId;
    }

    public String getUsername() {
        return username;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getTakeAwayReservationId() {
        return takeAwayReservationId;
    }

    public ArrayList<OrderedMeal> getOrdered_meals() {
        return ordered_meals;
    }

    public String getNotes() {
        return notes;
    }

    public Calendar getDate() {
        return date;
    }
}
