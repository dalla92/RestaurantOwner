package it.polito.group2.restaurantowner;

import java.util.Calendar;

public class TableReservation {
    private String username, notes, restaurantId, tableReservationId;
    private int n_people;
    private Calendar date;

    public TableReservation(String username, int n_people, Calendar date, String notes, String restaurantId, String tableReservationId) {
        this.username = username;
        this.notes = notes;
        this.n_people = n_people;
        this.date = date;
        this.restaurantId = restaurantId;
        this.tableReservationId = tableReservationId;
    }

    public String getTableReservationId() {
        return tableReservationId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getUsername() {
        return username;
    }

    public int getN_people() {
        return n_people;
    }

    public String getNotes() {
        return notes;
    }

    public Calendar getDate() {
        return date;
    }
}
