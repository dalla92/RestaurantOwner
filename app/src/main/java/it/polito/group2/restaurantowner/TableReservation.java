package it.polito.group2.restaurantowner;

import java.util.Calendar;

public class TableReservation {
    private String client_name, notes;
    private int n_people;
    private Calendar time;

    public TableReservation(String client_name, int n_people, Calendar time, String notes) {
        this.client_name = client_name;
        this.notes = notes;
        this.n_people = n_people;
        this.time = time;
    }

    public String getClient_name() {
        return client_name;
    }

    public int getN_people() {
        return n_people;
    }

    public String getNotes() {
        return notes;
    }

    public Calendar getTime() {
        return time;
    }
}
