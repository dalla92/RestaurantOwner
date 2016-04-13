package it.polito.group2.restaurantowner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class TakeAwayReservation {
    private String client_name, notes;
    private ArrayList<OrderedMeal> ordered_meals;
    private Calendar date;

    public TakeAwayReservation(String client_name, ArrayList<OrderedMeal> ordered_meals, Calendar date, String notes) {
        this.client_name = client_name;
        this.notes = notes;
        this.ordered_meals = new ArrayList<>();
        this.ordered_meals.addAll(ordered_meals);
        this.date = date;
    }

    public String getClient_name() {
        return client_name;
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
