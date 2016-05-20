package it.polito.group2.restaurantowner.firebasedata;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Alessio on 16/05/2016.
 */
public class TableReservation {

    private String table_reservation_id;
    private String restaurant_id;
    private String user_id;
    private String user_full_name;
    private GregorianCalendar table_reservation_date;
    private String table_reservation_notes;
    private int table_reservation_guests_number;

    public TableReservation(){

    }

    public String getTable_reservation_id() {
        return table_reservation_id;
    }

    public void setTable_reservation_id(String table_reservation_id) {
        this.table_reservation_id = table_reservation_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public GregorianCalendar getTable_reservation_date() {
        return table_reservation_date;
    }

    public void setTable_reservation_date(GregorianCalendar table_reservation_date) {
        this.table_reservation_date = table_reservation_date;
    }

    public String getTable_reservation_notes() {
        return table_reservation_notes;
    }

    public void setTable_reservation_notes(String table_reservation_notes) {
        this.table_reservation_notes = table_reservation_notes;
    }

    public int getTable_reservation_guests_number() {
        return table_reservation_guests_number;
    }

    public void setTable_reservation_guests_number(int table_reservation_guests_number) {
        this.table_reservation_guests_number = table_reservation_guests_number;
    }
}
